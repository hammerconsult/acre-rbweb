package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.LancamentoContabil;
import br.com.webpublico.entidades.ParametroEvento;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
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
public class ParametroEventoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = "INSERT INTO PARAMETROEVENTO " +
            "(ID, DATAEVENTO, COMPLEMENTOHISTORICO, EVENTOCONTABIL_ID, UNIDADEORGANIZACIONAL_ID, CLASSEORIGEM, IDORIGEM, COMPLEMENTOID, TIPOBALANCETE)" +
            " VALUES " +
            "(?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final List<ParametroEvento> objetos;
    private final SingletonGeradorId geradorDeIds;

    public ParametroEventoSetter(List<ParametroEvento> objetos, SingletonGeradorId geradorDeIds) {
        this.objetos = objetos;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ParametroEvento parametroEvento = objetos.get(i);
        parametroEvento.setId(geradorDeIds.getProximoId());

        ps.setLong(1, parametroEvento.getId());
        ps.setDate(2, new java.sql.Date(parametroEvento.getDataEvento().getTime()));
        ps.setString(3, parametroEvento.getComplementoHistorico());
        ps.setLong(4, parametroEvento.getEventoContabil().getId());
        ps.setLong(5, parametroEvento.getUnidadeOrganizacional().getId());
        ps.setString(6, parametroEvento.getClasseOrigem());
        ps.setString(7, parametroEvento.getIdOrigem());
        ps.setString(8, parametroEvento.getComplementoId().name());
        ps.setString(9, parametroEvento.getTipoBalancete().name());

    }

    @Override
    public int getBatchSize() {
        return objetos.size();
    }
}
