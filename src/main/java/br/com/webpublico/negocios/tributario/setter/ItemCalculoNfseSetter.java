package br.com.webpublico.negocios.tributario.setter;

import br.com.webpublico.entidades.ItemCalculoIss;
import br.com.webpublico.entidades.ItemCalculoNfse;
import br.com.webpublico.negocios.tributario.singletons.SingletonLancamentoGeralISSFixo;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 30/09/13
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
public class ItemCalculoNfseSetter implements BatchPreparedStatementSetter {
    private List<ItemCalculoNfse> itens;

    public ItemCalculoNfseSetter(List<ItemCalculoNfse> itensCalculoIss) {
        itens = itensCalculoIss;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ItemCalculoNfse item = itens.get(i);
        item.setId(SingletonLancamentoGeralISSFixo.getInstance().getProximoId());
        ps.setLong(1, item.getId());
        ps.setLong(2, item.getCalculoNfse().getId());
        ps.setLong(3, item.getTributo().getId());
    }

    @Override
    public int getBatchSize() {
        return itens.size();
    }
}
