package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.SaldoContaContabil;
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
public class SaldoContaContabilSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = "INSERT INTO SALDOCONTACONTABIL " +
            "(ID, DATASALDO, TOTALCREDITO, TOTALDEBITO, CONTACONTABIL_ID, UNIDADEORGANIZACIONAL_ID, TIPOBALANCETE)" +
            " VALUES " +
            "(?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE = "UPDATE SALDOCONTACONTABIL " +
        "SET DATASALDO = ?, TOTALCREDITO = ?, TOTALDEBITO = ?, CONTACONTABIL_ID = ?, UNIDADEORGANIZACIONAL_ID = ?, TIPOBALANCETE = ? " +
        " WHERE ID = ? ";

    private final List<SaldoContaContabil> saldos;
    private final SingletonGeradorId geradorDeIds;

    public SaldoContaContabilSetter(List<SaldoContaContabil> saldos, SingletonGeradorId geradorDeIds) {
        this.saldos = saldos;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        SaldoContaContabil saldo = saldos.get(i);
        saldo.setId(geradorDeIds.getProximoId());
        ps.setLong(1, saldo.getId());
        ps.setDate(2, new java.sql.Date(saldo.getDataSaldo().getTime()));
        ps.setBigDecimal(3, saldo.getTotalCredito());
        ps.setBigDecimal(4, saldo.getTotalDebito());
        ps.setLong(5, saldo.getContaContabil().getId());
        ps.setLong(6, saldo.getUnidadeOrganizacional().getId());
        ps.setString(7, saldo.getTipoBalancete().name());
    }

    @Override
    public int getBatchSize() {
        return saldos.size();
    }
}
