package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.MovimentoContaFinanceira;
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
public class MovimentoContaFinanceiraSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = "INSERT INTO MOVIMENTOCONTAFINANCEIRA " +
        " (ID, DATASALDO, UNIDADEORGANIZACIONAL_ID, SUBCONTA_ID, FONTEDERECURSOS_ID, EVENTOCONTABIL_ID, HISTORICO, MOVIMENTACAOFINANCEIRA, TOTALDEBITO, TOTALCREDITO, UUID, CONTADEDESTINACAO_ID)  " +
        " VALUES " +
        " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE = "UPDATE MOVIMENTOCONTAFINANCEIRA " +
        " SET DATASALDO = ?, UNIDADEORGANIZACIONAL_ID = ?, SUBCONTA_ID = ?, FONTEDERECURSOS_ID = ?, EVENTOCONTABIL_ID = ?, HISTORICO = ?, MOVIMENTACAOFINANCEIRA = ?, TOTALDEBITO = ?, TOTALCREDITO = ?, UUID = ?, CONTADEDESTINACAO_ID = ? " +
        " WHERE ID = ? ";

    private final List<MovimentoContaFinanceira> saldos;
    private final SingletonGeradorId geradorDeIds;

    public MovimentoContaFinanceiraSetter(List<MovimentoContaFinanceira> saldos, SingletonGeradorId geradorDeIds) {
        this.saldos = saldos;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        MovimentoContaFinanceira saldo = saldos.get(i);
        saldo.setId(geradorDeIds.getProximoId());

        ps.setLong(1, saldo.getId());
        ps.setDate(2, new Date(saldo.getDataSaldo().getTime()));
        ps.setLong(3, saldo.getUnidadeOrganizacional().getId());
        ps.setLong(4, saldo.getSubConta().getId());
        ps.setLong(5, saldo.getFonteDeRecursos().getId());
        ps.setLong(6, saldo.getEventoContabil().getId());
        ps.setString(7, saldo.getHistorico());
        ps.setString(8, saldo.getMovimentacaoFinanceira().name());
        ps.setBigDecimal(9, saldo.getTotalDebito());
        ps.setBigDecimal(10, saldo.getTotalCredito());
        ps.setString(11, saldo.getUuid());
        ps.setLong(12, saldo.getContaDeDestinacao().getId());

    }

    @Override
    public int getBatchSize() {
        return saldos.size();
    }
}
