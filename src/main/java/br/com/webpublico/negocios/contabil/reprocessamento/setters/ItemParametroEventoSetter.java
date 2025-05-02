package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.ItemParametroEvento;
import br.com.webpublico.entidades.LancamentoContabil;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 23/10/14
 * Time: 17:03
 * To change this template use File | Settings | File Templates.
 */
public class ItemParametroEventoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = "INSERT INTO ITEMPARAMETROEVENTO " +
            "(ID, VALOR, PARAMETROEVENTO_ID, TAGVALOR, OPERACAOCLASSECREDOR)" +
            " VALUES " +
            "(?, ?, ?, ?, ?)";

    private final List<ItemParametroEvento> itens;
    private final SingletonGeradorId geradorDeIds;

    public ItemParametroEventoSetter(List<ItemParametroEvento> itens, SingletonGeradorId geradorDeIds) {
        this.itens = itens;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ItemParametroEvento itemParametroEvento = itens.get(i);
        itemParametroEvento.setId(geradorDeIds.getProximoId());

        ps.setLong(1, itemParametroEvento.getId());
        ps.setBigDecimal(2, itemParametroEvento.getValor());
        ps.setLong(3, itemParametroEvento.getParametroEvento().getId());
        ps.setString(4, itemParametroEvento.getTagValor().name());
        if (itemParametroEvento.getOperacaoClasseCredor() != null) {
            ps.setString(5, itemParametroEvento.getOperacaoClasseCredor().name());
        } else {
            ps.setString(5, null);
        }

    }

    @Override
    public int getBatchSize() {
        return itens.size();
    }
}
