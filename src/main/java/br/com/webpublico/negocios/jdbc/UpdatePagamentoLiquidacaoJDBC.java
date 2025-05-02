package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by fernando on 30/01/15.
 */
public class UpdatePagamentoLiquidacaoJDBC extends ClassPatternJDBC {

    private static UpdatePagamentoLiquidacaoJDBC instancia;
    private PreparedStatement psUpdatePagamento;
    private PreparedStatement psUpdateLiquidacao;

    private UpdatePagamentoLiquidacaoJDBC(Connection conn) {
        this.conexao = conn;
    }

    public static synchronized UpdatePagamentoLiquidacaoJDBC createInstance(Connection conn) {
        if (instancia == null) {
            instancia = new UpdatePagamentoLiquidacaoJDBC(conn);
        }
        showConnection(instancia.getClass());
        return instancia;
    }

    public void updatePagamentoLiquidacao(Pagamento pag) {
        atualizaPagamento(pag);
        atualizaLiquidacao(pag);
    }

    private void atualizaPagamento(Pagamento pag) {
        try {
            preparaUpdatePagamento();
            psUpdatePagamento.clearParameters();
            psUpdatePagamento.setBigDecimal(1, pag.getValor());
            psUpdatePagamento.setLong(2, pag.getId());
            psUpdatePagamento.executeUpdate();
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao atualizar status e saldo de pagamento: " + ex.getMessage(), ex);
        }
    }

    private void preparaUpdatePagamento() throws SQLException {
        if (psUpdatePagamento == null) {
            String sql = "update pagamento set status = 'DEFERIDO', saldo = ? where id = ?";
            psUpdatePagamento = this.conexao.prepareStatement(sql);
        }
    }

    private void atualizaLiquidacao(Pagamento pag) {
        try {
            preparaUpdateLiquidacao();
            psUpdateLiquidacao.clearParameters();
            psUpdateLiquidacao.setBigDecimal(1, pag.getValor());
            psUpdateLiquidacao.setLong(2, pag.getLiquidacao().getId());
            psUpdateLiquidacao.executeUpdate();
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao atualizar saldo de liquidação: " + ex.getMessage(), ex);
        }
    }

    private void preparaUpdateLiquidacao() throws SQLException {
        if (psUpdateLiquidacao == null) {
            String sql = "update liquidacao set saldo = saldo - ? where id = ?";
            psUpdateLiquidacao = this.conexao.prepareStatement(sql);
        }
    }
}
