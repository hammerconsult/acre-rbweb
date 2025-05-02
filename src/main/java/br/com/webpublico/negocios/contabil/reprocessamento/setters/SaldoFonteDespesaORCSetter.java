package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.SaldoFonteDespesaORC;
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
public class SaldoFonteDespesaORCSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = "INSERT INTO SALDOFONTEDESPESAORC " +
        " (ID, DOTACAO, EMPENHADO, LIQUIDADO, PAGO, DATASALDO, FONTEDESPESAORC_ID, ALTERACAO, RESERVADO, UNIDADEORGANIZACIONAL_ID, SUPLEMENTADO, REDUZIDO, EMPENHADOPROCESSADO, EMPENHADONAOPROCESSADO, LIQUIDADOPROCESSADO, LIQUIDADONAOPROCESSADO, PAGOPROCESSADO, PAGONAOPROCESSADO, RESERVADOPORLICITACAO, VERSAO) " +
        " VALUES " +
        "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE = "UPDATE SALDOFONTEDESPESAORC " +
        "SET DOTACAO = ?, EMPENHADO = ?, LIQUIDADO = ?, PAGO = ?, DATASALDO = ?, FONTEDESPESAORC_ID = ?, ALTERACAO = ?, RESERVADO = ?, UNIDADEORGANIZACIONAL_ID = ?, SUPLEMENTADO = ?, REDUZIDO = ?, EMPENHADOPROCESSADO = ?, EMPENHADONAOPROCESSADO = ?, LIQUIDADOPROCESSADO = ?, LIQUIDADONAOPROCESSADO = ?, PAGOPROCESSADO = ?, PAGONAOPROCESSADO = ?, RESERVADOPORLICITACAO = ?, VERSAO  = ?" +
        " WHERE ID = ? ";

    private final List<SaldoFonteDespesaORC> saldos;
    private final SingletonGeradorId geradorDeIds;

    public SaldoFonteDespesaORCSetter(List<SaldoFonteDespesaORC> saldos, SingletonGeradorId geradorDeIds) {
        this.saldos = saldos;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        SaldoFonteDespesaORC saldo = saldos.get(i);
        saldo.setId(geradorDeIds.getProximoId());

        ps.setLong(1, saldo.getId());
        ps.setBigDecimal(2, saldo.getDotacao());
        ps.setBigDecimal(3, saldo.getEmpenhado());
        ps.setBigDecimal(4, saldo.getLiquidado());
        ps.setBigDecimal(5, saldo.getPago());
        ps.setDate(6, new Date(saldo.getDataSaldo().getTime()));
        ps.setLong(7, saldo.getFonteDespesaORC().getId());
        ps.setBigDecimal(8, saldo.getAlteracao());
        ps.setBigDecimal(9, saldo.getReservado());
        ps.setLong(10, saldo.getUnidadeOrganizacional().getId());
        ps.setBigDecimal(11, saldo.getSuplementado());
        ps.setBigDecimal(12, saldo.getReduzido());
        ps.setBigDecimal(13, saldo.getEmpenhadoProcessado());
        ps.setBigDecimal(14, saldo.getEmpenhadoNaoProcessado());
        ps.setBigDecimal(15, saldo.getLiquidadoProcessado());
        ps.setBigDecimal(16, saldo.getLiquidadoNaoProcessado());
        ps.setBigDecimal(17, saldo.getPagoProcessado());
        ps.setBigDecimal(18, saldo.getPagoNaoProcessado());
        ps.setBigDecimal(19, saldo.getReservadoPorLicitacao());
        if (saldo.getVersao() == null) {
            ps.setLong(20, 0);
        } else {
            ps.setLong(20, saldo.getVersao());
        }
    }

    @Override
    public int getBatchSize() {
        return saldos.size();
    }
}
