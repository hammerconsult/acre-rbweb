package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.InscricaoDividaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ItemInscricaoParcelaDividaAtivaSetter implements BatchPreparedStatementSetter {
    private final List<InscricaoDividaParcela> parcelas;
    private final List<Long> ids;


    public ItemInscricaoParcelaDividaAtivaSetter(List<InscricaoDividaParcela> parcelas, List<Long> ids) {
        this.parcelas = parcelas;
        this.ids = ids;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        InscricaoDividaParcela parcela = parcelas.get(i);
        parcela.setId(ids.get(i));
        ps.setLong(1, parcela.getId());
        ps.setLong(2, parcela.getItemInscricaoDividaAtiva().getId());
        ps.setLong(3, parcela.getIdParcela());

    }

    @Override
    public int getBatchSize() {
        return parcelas.size();
    }
}
