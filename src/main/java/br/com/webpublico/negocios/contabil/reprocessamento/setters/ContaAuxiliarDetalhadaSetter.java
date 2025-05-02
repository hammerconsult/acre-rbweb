package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidadesauxiliares.contabil.ContaAuxiliarDetalhada;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/10/14
 * Time: 09:34
 * To change this template use File | Settings | File Templates.
 */
public class ContaAuxiliarDetalhadaSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT = "INSERT INTO CONTAAUXILIARDETALHADA " +
        "(ID, CONTACONTABIL_ID, conta_id, exercicioAtual_id, unidadeOrganizacional_id," +
        " subSistema, dividaConsolidada, es, classificacaoFuncional, hashContaAuxiliarDetalhada, " +
        " tipoContaAuxiliar_id, ContaDeDestinacao_id, exercicioOrigem_id, codigoco)" +
        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final ContaAuxiliarDetalhada contaAuxiliarDetalhada;
    private final Long id;

    public ContaAuxiliarDetalhadaSetter(ContaAuxiliarDetalhada contaAuxiliarDetalhada, Long id) {
        this.contaAuxiliarDetalhada = contaAuxiliarDetalhada;
        this.id = id;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, id);
        if (contaAuxiliarDetalhada.getContaContabil() != null) {
            ps.setLong(2, contaAuxiliarDetalhada.getContaContabil().getId());
        } else {
            ps.setNull(2, java.sql.Types.NUMERIC);
        }
        if (contaAuxiliarDetalhada.getConta() != null) {
            ps.setLong(3, contaAuxiliarDetalhada.getConta().getId());
        } else {
            ps.setNull(3, java.sql.Types.NUMERIC);
        }
        if (contaAuxiliarDetalhada.getExercicioAtual() != null) {
            ps.setLong(4, contaAuxiliarDetalhada.getExercicioAtual().getId());
        } else {
            ps.setNull(4, java.sql.Types.NUMERIC);
        }
        if (contaAuxiliarDetalhada.getUnidadeOrganizacional() != null) {
            ps.setLong(5, contaAuxiliarDetalhada.getUnidadeOrganizacional().getId());
        } else {
            ps.setNull(5, java.sql.Types.NUMERIC);
        }
        if (contaAuxiliarDetalhada.getSubSistema() != null) {
            ps.setString(6, contaAuxiliarDetalhada.getSubSistema().name());
        } else {
            ps.setNull(6, Types.VARCHAR);
        }
        if (contaAuxiliarDetalhada.getDividaConsolidada() != null) {
            ps.setInt(7, contaAuxiliarDetalhada.getDividaConsolidada());
        } else {
            ps.setNull(7, Types.INTEGER);
        }
        if (contaAuxiliarDetalhada.getEs() != null) {
            ps.setInt(8, contaAuxiliarDetalhada.getEs());
        } else {
            ps.setNull(8, Types.INTEGER);
        }
        if (contaAuxiliarDetalhada.getClassificacaoFuncional() != null) {
            ps.setString(9, contaAuxiliarDetalhada.getClassificacaoFuncional());
        } else {
            ps.setNull(9, Types.VARCHAR);
        }
        ps.setInt(10, contaAuxiliarDetalhada.getHashContaAuxiliarDetalhada());
        if (contaAuxiliarDetalhada.getTipoContaAuxiliar() != null) {
            ps.setLong(11, contaAuxiliarDetalhada.getTipoContaAuxiliar().getId());
        } else {
            ps.setNull(11, java.sql.Types.NUMERIC);
        }
        if (contaAuxiliarDetalhada.getContaDeDestinacao() != null) {
            ps.setLong(12, contaAuxiliarDetalhada.getContaDeDestinacao().getId());
        } else {
            ps.setNull(12, java.sql.Types.NUMERIC);
        }
        if (contaAuxiliarDetalhada.getExercicioOrigem() != null) {
            ps.setLong(13, contaAuxiliarDetalhada.getExercicioOrigem().getId());
        } else {
            ps.setNull(13, java.sql.Types.NUMERIC);
        }
        if (contaAuxiliarDetalhada.getCodigoCO() != null) {
            ps.setString(14, contaAuxiliarDetalhada.getCodigoCO());
        } else {
            ps.setNull(14, Types.VARCHAR);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
