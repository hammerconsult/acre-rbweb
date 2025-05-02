package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ItemInscricaoDividaAtiva;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class ItemInscricaoDividaAtivaSetter implements BatchPreparedStatementSetter {
    private List<ItemInscricaoDividaAtiva> itens;


    public ItemInscricaoDividaAtivaSetter(List<ItemInscricaoDividaAtiva> itens) {
        this.itens = itens;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ItemInscricaoDividaAtiva item = itens.get(i);
        ps.setLong(1, item.getId());
        ps.setLong(2, item.getInscricaoDividaAtiva().getId());
        ps.setLong(3, item.getResultadoParcela().getIdDivida());
        if (item.getResultadoParcela().getIdPessoa() != null) {
            ps.setLong(4, item.getResultadoParcela().getIdPessoa());
        } else {
            ps.setNull(4, Types.NUMERIC);
        }
        ps.setString(5, ItemInscricaoDividaAtiva.Situacao.ATIVO.name());
        ps.setLong(6, item.getExercicio().getId());
    }

    @Override
    public int getBatchSize() {
        return itens.size();
    }
}
