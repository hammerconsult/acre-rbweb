package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ItemCertidaoDividaAtiva;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ItemCertidaoDividaAtivaSetter implements BatchPreparedStatementSetter {

    private final List<ItemCertidaoDividaAtiva> itens;
    private final List<Long> ids;

    public ItemCertidaoDividaAtivaSetter(List<ItemCertidaoDividaAtiva> certidoes, List<Long> ids) {
        this.itens = certidoes;
        this.ids = ids;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ItemCertidaoDividaAtiva item = itens.get(i);
        item.setId(ids.get(i));
        ps.setLong(1, item.getId());
        ps.setLong(2, item.getCertidao().getId());
        ps.setLong(3, item.getItemInscricaoDividaAtiva().getId());

    }

    @Override
    public int getBatchSize() {
        return itens.size();
    }
}
