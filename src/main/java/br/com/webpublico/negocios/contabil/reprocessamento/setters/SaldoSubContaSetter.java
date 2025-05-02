package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.SaldoFonteDespesaORC;
import br.com.webpublico.entidades.SaldoSubConta;
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
public class SaldoSubContaSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = "INSERT INTO SALDOSUBCONTA (ID, DATASALDO, VALOR, SUBCONTA_ID, UNIDADEORGANIZACIONAL_ID, FONTEDERECURSOS_ID, TOTALCREDITO, TOTALDEBITO, VERSAO, CONTADEDESTINACAO_ID) " +
        " VALUES " +
        "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE = "UPDATE SALDOSUBCONTA " +
        " SET DATASALDO = ?, VALOR = ?, SUBCONTA_ID = ?, UNIDADEORGANIZACIONAL_ID = ?, FONTEDERECURSOS_ID = ?, TOTALCREDITO = ?, TOTALDEBITO = ?, VERSAO = ?, CONTADEDESTINACAO_ID = ? " +
        " WHERE ID = ? ";

    private final List<SaldoSubConta> saldos;
    private final SingletonGeradorId geradorDeIds;

    public SaldoSubContaSetter(List<SaldoSubConta> saldos, SingletonGeradorId geradorDeIds) {
        this.saldos = saldos;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        SaldoSubConta saldo = saldos.get(i);
        saldo.setId(geradorDeIds.getProximoId());

        ps.setLong(1, saldo.getId());
        ps.setDate(2, new Date(saldo.getDataSaldo().getTime()));
        ps.setBigDecimal(3, saldo.getValor());
        ps.setLong(4, saldo.getSubConta().getId());
        ps.setLong(5, saldo.getUnidadeOrganizacional().getId());
        ps.setLong(6, saldo.getFonteDeRecursos().getId());
        ps.setBigDecimal(7, saldo.getTotalCredito());
        ps.setBigDecimal(8, saldo.getTotalDebito());
        if (saldo.getVersao() == null) {
            ps.setLong(9, 0);
        } else {
            ps.setLong(9, saldo.getVersao());
        }
        ps.setLong(10, saldo.getContaDeDestinacao().getId());
    }

    @Override
    public int getBatchSize() {
        return saldos.size();
    }
}
