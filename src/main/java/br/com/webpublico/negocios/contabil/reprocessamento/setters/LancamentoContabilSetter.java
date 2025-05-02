package br.com.webpublico.negocios.contabil.reprocessamento.setters;

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
public class LancamentoContabilSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = "INSERT INTO LANCAMENTOCONTABIL " +
        "(ID, DATALANCAMENTO, VALOR, COMPLEMENTOHISTORICO, CONTACREDITO_ID, CONTADEBITO_ID, " +
        "CLPHISTORICOCONTABIL_ID, UNIDADEORGANIZACIONAL_ID, LCP_ID, ITEMPARAMETROEVENTO_ID, " +
        "CONTAAUXILIARCREDITO_ID, CONTAAUXILIARDEBITO_ID, NUMERO, CONTAAUXILIARDEBSICONFI_ID, " +
        "CONTAAUXILIARCREDSICONFI_ID, CONTAAUXCREDETALHADASICONFI_ID, CONTAAUXDEBDETALHADASICONFI_ID)" +
        " VALUES " +
        "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final List<LancamentoContabil> lancamentos;
    private final SingletonGeradorId geradorDeIds;

    public LancamentoContabilSetter(List<LancamentoContabil> lancamentos, SingletonGeradorId geradorDeIds) {
        this.lancamentos = lancamentos;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        LancamentoContabil lancamentoContabil = lancamentos.get(i);
        lancamentoContabil.setId(geradorDeIds.getProximoId());

        ps.setLong(1, lancamentoContabil.getId());
        ps.setDate(2, new java.sql.Date(lancamentoContabil.getDataLancamento().getTime()));

        ps.setBigDecimal(3, lancamentoContabil.getValor());
        ps.setString(4, lancamentoContabil.getComplementoHistorico());
        if (lancamentoContabil.getContaCredito() != null) {
            ps.setLong(5, lancamentoContabil.getContaCredito().getId());
        } else {
            ps.setNull(5, java.sql.Types.NUMERIC);
        }
        if (lancamentoContabil.getContaDebito() != null) {
            ps.setLong(6, lancamentoContabil.getContaDebito().getId());
        } else {
            ps.setNull(6, java.sql.Types.NUMERIC);
        }
        if (lancamentoContabil.getClpHistoricoContabil() != null) {
            ps.setLong(7, lancamentoContabil.getClpHistoricoContabil().getId());
        } else {
            ps.setNull(7, java.sql.Types.NUMERIC);
        }
        ps.setLong(8, lancamentoContabil.getUnidadeOrganizacional().getId());
        ps.setLong(9, lancamentoContabil.getLcp().getId());
        ps.setLong(10, lancamentoContabil.getItemParametroEvento().getId());
        if (lancamentoContabil.getContaAuxiliarCredito() != null) {
            ps.setLong(11, lancamentoContabil.getContaAuxiliarCredito().getId());
        } else {
            ps.setNull(11, java.sql.Types.NUMERIC);
        }

        if (lancamentoContabil.getContaAuxiliarDebito() != null) {
            ps.setLong(12, lancamentoContabil.getContaAuxiliarDebito().getId());
        } else {
            ps.setNull(12, java.sql.Types.NUMERIC);
        }
        if (lancamentoContabil.getNumero() != null) {
            ps.setLong(13, lancamentoContabil.getNumero());
        } else {
            ps.setNull(13, java.sql.Types.NUMERIC);
        }
        if (lancamentoContabil.getContaAuxiliarDebSiconfi() != null) {
            ps.setLong(14, lancamentoContabil.getContaAuxiliarDebSiconfi().getId());
        } else {
            ps.setNull(14, java.sql.Types.NUMERIC);
        }
        if (lancamentoContabil.getContaAuxiliarCredSiconfi() != null) {
            ps.setLong(15, lancamentoContabil.getContaAuxiliarCredSiconfi().getId());
        } else {
            ps.setNull(15, java.sql.Types.NUMERIC);
        }

        if (lancamentoContabil.getContaAuxCreDetalhadaSiconfi() != null) {
            ps.setLong(16, lancamentoContabil.getContaAuxCreDetalhadaSiconfi().getId());
        } else {
            ps.setNull(16, java.sql.Types.NUMERIC);
        }
        if (lancamentoContabil.getContaAuxDebDetalhadaSiconfi() != null) {
            ps.setLong(17, lancamentoContabil.getContaAuxDebDetalhadaSiconfi().getId());
        } else {
            ps.setNull(17, java.sql.Types.NUMERIC);
        }
    }

    @Override
    public int getBatchSize() {
        return lancamentos.size();
    }
}
