package br.com.webpublico.negocios.tributario.setter;


import br.com.webpublico.entidades.ItemCalculoIPTU;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import com.sun.btrace.org.objectweb.asm.Type;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ItemCalculoIPTUSetter implements BatchPreparedStatementSetter {
    private List<ItemCalculoIPTU> itens;
    private SingletonGeradorId geradorDeIds;
    public ItemCalculoIPTUSetter(List<ItemCalculoIPTU> itens, SingletonGeradorId geradorDeIds) {
        this.itens = itens;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ItemCalculoIPTU item = itens.get(i);
        item.setId(geradorDeIds.getProximoId());
        ps.setLong(1, item.getId());
        ps.setLong(2, item.getCalculoIPTU().getId());
        ps.setLong(3, item.getEvento().getId());
        ps.setDate(4, new java.sql.Date(item.getDataRegistro().getTime()));
        ps.setBoolean(5,  item.getIsento());
        ps.setBigDecimal(6,  item.getValorReal());
        ps.setBigDecimal(7,  item.getValorEfetivo());
        if (item.getConstrucao().isDummy()) {
            ps.setNull(8, Type.LONG);
        } else {
            ps.setLong(8, item.getConstrucao().getId());
        }
        ps.setBoolean(9, item.getImune());

    }

    @Override
    public int getBatchSize() {
        return itens.size();
    }
}
