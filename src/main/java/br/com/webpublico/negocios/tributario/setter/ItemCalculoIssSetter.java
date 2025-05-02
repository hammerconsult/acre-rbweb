package br.com.webpublico.negocios.tributario.setter;

import br.com.webpublico.entidades.ItemCalculoIss;
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
public class ItemCalculoIssSetter implements BatchPreparedStatementSetter {
    private List<ItemCalculoIss> itens;

    public ItemCalculoIssSetter(List<ItemCalculoIss> itensCalculoIss) {
        itens = itensCalculoIss;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ItemCalculoIss item = itens.get(i);
        item.setId(SingletonLancamentoGeralISSFixo.getInstance().getProximoId());

        ps.setLong(1, item.getId());

        if (item.getServico() != null) {
            ps.setLong(2, item.getServico().getId());
        } else {
            atribuirValorNuloTypeNumeric(ps, 2);
        }

        if (item.getCalculo() != null) {
            ps.setLong(3, item.getCalculo().getId());
        } else {
            atribuirValorNuloTypeNumeric(ps, 3);
        }

        if (item.getAliquota() != null) {
            ps.setBigDecimal(4, item.getAliquota());
        } else {
            atribuirValorNuloTypeNumeric(ps, 4);
        }

        if (item.getBaseCalculo() != null) {
            ps.setBigDecimal(5, item.getBaseCalculo());
        } else {
            atribuirValorNuloTypeNumeric(ps, 5);
        }

        if (item.getFaturamento() != null) {
            ps.setBigDecimal(6, item.getFaturamento());
        } else {
            atribuirValorNuloTypeNumeric(ps, 6);
        }

        if (item.getTributo() != null) {
            ps.setLong(7, item.getTributo().getId());
        } else {
            atribuirValorNuloTypeNumeric(ps, 7);
        }

        if (item.getValorCalculado() != null) {
            ps.setBigDecimal(8, item.getValorCalculado());
        } else {
            atribuirValorNuloTypeNumeric(ps, 8);
        }
    }

    private void atribuirValorNuloTypeNumeric(PreparedStatement ps, int posicao) throws SQLException {
        ps.setNull(posicao, Types.NUMERIC);
    }

    @Override
    public int getBatchSize() {
        return itens.size();
    }
}
