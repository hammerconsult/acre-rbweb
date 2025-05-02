package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ItemParcelaValorDivida;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ItemParcelaValorDividaSetter implements BatchPreparedStatementSetter {

    private final List<ItemParcelaValorDivida> itens;
    private final List<Long> ids;
    private Long idRevisao;

    public ItemParcelaValorDividaSetter(List<ItemParcelaValorDivida> itens, List<Long> ids) {
        this.itens = itens;
        this.ids = ids;
    }

    public ItemParcelaValorDividaSetter(List<ItemParcelaValorDivida> itens, List<Long> ids, Long idRevisao) {
        this.itens = itens;
        this.ids = ids;
        this.idRevisao = idRevisao;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ItemParcelaValorDivida item = itens.get(i);
        item.setId(ids.get(i));
        ps.setLong(1, item.getId());
        ps.setLong(2, item.getParcelaValorDivida().getId());
        ps.setLong(3, item.getItemValorDivida().getId());
        ps.setBigDecimal(4, item.getValor());
        if (idRevisao != null) {
            ps.setLong(5, idRevisao);
            ps.setLong(6, 0L);
        }
    }

    @Override
    public int getBatchSize() {
        return itens.size();
    }
}
