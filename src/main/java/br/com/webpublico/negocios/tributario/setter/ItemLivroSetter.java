package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ItemLivroDividaAtiva;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ItemLivroSetter implements BatchPreparedStatementSetter {

    private final List<ItemLivroDividaAtiva> itens;
    private final List<Long> ids;

    public ItemLivroSetter(List<ItemLivroDividaAtiva> itens, List<Long> ids) {
        this.itens = itens;
        this.ids = ids;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ItemLivroDividaAtiva item = itens.get(i);
        item.setId(ids.get(i));
        ps.setLong(1, item.getId());
        ps.setLong(2, item.getLivroDividaAtiva().getId());
        ps.setLong(3, item.getInscricaoDividaAtiva().getId());
        ps.setBoolean(4, item.getProcessado());

    }

    @Override
    public int getBatchSize() {
        return itens.size();
    }
}
