package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ItemValorDivida;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ItemValorDividaSetter implements BatchPreparedStatementSetter {

    private final List<ItemValorDivida> itens;
    private final List<Long> ids;
    private Long idRevisao;

    public ItemValorDividaSetter(List<ItemValorDivida> itens, List<Long> ids) {
        this.itens = itens;
        this.ids = ids;
    }

    public ItemValorDividaSetter(List<ItemValorDivida> itens, List<Long> ids, Long idRevisao) {
        this.itens = itens;
        this.ids = ids;
        this.idRevisao = idRevisao;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ItemValorDivida item = itens.get(i);
        item.setId(ids.get(i));
        ps.setLong(1, item.getId());
        ps.setBigDecimal(2, item.getValor());
        ps.setLong(3, item.getTributo().getId());
        ps.setLong(4, item.getValorDivida().getId());
        ps.setBoolean(5, item.getIsento());
        ps.setBoolean(6, item.getImune());
        if (idRevisao != null) {
            ps.setLong(7, idRevisao);
            ps.setLong(8, 0L);
        }
    }

    @Override
    public int getBatchSize() {
        return itens.size();
    }
}
