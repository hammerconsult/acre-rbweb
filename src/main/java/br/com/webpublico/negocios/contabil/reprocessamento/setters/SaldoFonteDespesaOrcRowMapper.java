package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidades.SaldoFonteDespesaORC;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SaldoFonteDespesaOrcRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        SaldoFonteDespesaORC saldo = new SaldoFonteDespesaORC();
        saldo.setId(rs.getLong("ID"));
        saldo.setDotacao(rs.getBigDecimal("DOTACAO"));
        saldo.setEmpenhado(rs.getBigDecimal("EMPENHADO"));
        saldo.setLiquidado(rs.getBigDecimal("LIQUIDADO"));
        saldo.setPago(rs.getBigDecimal("PAGO"));
        saldo.setDataSaldo(rs.getDate("DATASALDO"));

        FonteDespesaORC fonteDespesaORC = new FonteDespesaORC();
        fonteDespesaORC.setId(rs.getLong("FONTEDESPESAORC_ID"));
        saldo.setFonteDespesaORC(fonteDespesaORC);

        saldo.setAlteracao(rs.getBigDecimal("ALTERACAO"));
        saldo.setReservado(rs.getBigDecimal("RESERVADO"));

        UnidadeOrganizacional unidadeOrganizacional = new UnidadeOrganizacional();
        unidadeOrganizacional.setId(rs.getLong("UNIDADEORGANIZACIONAL_ID"));
        saldo.setUnidadeOrganizacional(unidadeOrganizacional);

        saldo.setSuplementado(rs.getBigDecimal("SUPLEMENTADO"));
        saldo.setReduzido(rs.getBigDecimal("REDUZIDO"));
        saldo.setEmpenhadoProcessado(rs.getBigDecimal("EMPENHADOPROCESSADO"));
        saldo.setEmpenhadoNaoProcessado(rs.getBigDecimal("EMPENHADONAOPROCESSADO"));
        saldo.setLiquidadoProcessado(rs.getBigDecimal("LIQUIDADOPROCESSADO"));
        saldo.setLiquidadoNaoProcessado(rs.getBigDecimal("LIQUIDADONAOPROCESSADO"));
        saldo.setPagoProcessado(rs.getBigDecimal("PAGOPROCESSADO"));
        saldo.setPagoNaoProcessado(rs.getBigDecimal("PAGONAOPROCESSADO"));
        saldo.setReservadoPorLicitacao(rs.getBigDecimal("RESERVADOPORLICITACAO"));
        saldo.setVersao(rs.getLong("VERSAO"));

        return saldo;
    }
}
