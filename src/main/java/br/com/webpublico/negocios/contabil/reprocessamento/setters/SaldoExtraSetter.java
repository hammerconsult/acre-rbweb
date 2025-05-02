package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.SaldoExtraorcamentario;
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
public class SaldoExtraSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = "INSERT INTO SALDOEXTRAORCAMENTARIO (ID, DATASALDO, VALOR, CONTAEXTRAORCAMENTARIA_ID, FONTEDERECURSOS_ID, UNIDADEORGANIZACIONAL_ID, CONTADEDESTINACAO_ID) " +
        " VALUES " +
        "(?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE = "UPDATE SALDOEXTRAORCAMENTARIO " +
        " SET DATASALDO = ?, VALOR = ?, CONTAEXTRAORCAMENTARIA_ID = ?, FONTEDERECURSOS_ID = ?, UNIDADEORGANIZACIONAL_ID = ?, CONTADEDESTINACAO_ID = ? " +
        " WHERE ID = ? ";

    private final List<SaldoExtraorcamentario> saldos;
    private final SingletonGeradorId geradorDeIds;

    public SaldoExtraSetter(List<SaldoExtraorcamentario> saldos, SingletonGeradorId geradorDeIds) {
        this.saldos = saldos;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        SaldoExtraorcamentario saldo = saldos.get(i);
        saldo.setId(geradorDeIds.getProximoId());

        ps.setLong(1, saldo.getId());
        ps.setDate(2, new Date(saldo.getDataSaldo().getTime()));
        ps.setBigDecimal(3, saldo.getValor());
        ps.setLong(4, saldo.getContaExtraorcamentaria().getId());
        ps.setLong(5, saldo.getFonteDeRecursos().getId());
        ps.setLong(6, saldo.getUnidadeOrganizacional().getId());
        ps.setLong(7, saldo.getContaDeDestinacao().getId());
    }

    @Override
    public int getBatchSize() {
        return saldos.size();
    }
}
