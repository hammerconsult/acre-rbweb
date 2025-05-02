package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.MovimentoDespesaORC;
import br.com.webpublico.entidades.MovimentoDespesaORC;
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
 * Time: 17:38
 * To change this template use File | Settings | File Templates.
 */
public class MovimentoDespesaOrcSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = "INSERT INTO MovimentoDespesaORC " +
        " (ID, DATAMOVIMENTO, VALOR, TIPOOPERACAOORC, OPERACAOORC, FONTEDESPESAORC_ID, UNIDADEORGANIZACIONAL_ID, CLASSEORIGEM, NUMEROMOVIMENTO, HISTORICO, IDORIGEM) " +
        " VALUES " +
        "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE = "UPDATE MovimentoDespesaORC " +
        " SET DATAMOVIMENTO = ?, VALOR = ?, TIPOOPERACAOORC = ?, OPERACAOORC = ?, FONTEDESPESAORC_ID = ?, UNIDADEORGANIZACIONAL_ID = ?, CLASSEORIGEM = ?, NUMEROMOVIMENTO = ?, HISTORICO = ?, IDORIGEM = ? " +
        " WHERE ID = ? ";

    private final List<MovimentoDespesaORC> saldos;
    private final SingletonGeradorId geradorDeIds;

    public MovimentoDespesaOrcSetter(List<MovimentoDespesaORC> saldos, SingletonGeradorId geradorDeIds) {
        this.saldos = saldos;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        MovimentoDespesaORC saldo = saldos.get(i);
        saldo.setId(geradorDeIds.getProximoId());

        ps.setLong(1, saldo.getId());
        ps.setDate(2, new Date(saldo.getDataMovimento().getTime()));
        ps.setBigDecimal(3, saldo.getValor());
        ps.setString(4, saldo.getTipoOperacaoORC().name());
        ps.setString(5, saldo.getOperacaoORC().name());
        ps.setLong(6, saldo.getFonteDespesaORC().getId());
        ps.setLong(7, saldo.getUnidadeOrganizacional().getId());
        ps.setString(8, saldo.getClasseOrigem());
        ps.setString(9, saldo.getNumeroMovimento());
        ps.setString(10, saldo.getHistorico());
        ps.setString(11, saldo.getIdOrigem());
    }

    @Override
    public int getBatchSize() {
        return saldos.size();
    }
}
