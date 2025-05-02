package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import java.math.BigDecimal;
import java.sql.Connection;

/**
 * Created by fernando on 29/01/15.
 */
public final class DeferirPagamentoJDBC extends SkeletonJDBC {

    private static DeferirPagamentoJDBC instancia;

    private DeferirPagamentoJDBC(Connection conn) {
        super();
        this.conexao = conn;
    }

    public static synchronized DeferirPagamentoJDBC createInstance(Connection conn) {
        if (instancia == null) {
            instancia = new DeferirPagamentoJDBC(conn);
        }
        showConnection(instancia.getClass());
        return instancia;
    }

    public void deferirPagamento(Pagamento pag) {
        if (validaSaldoDaLiquidacao(pag)) {
            getMovimentoSaldoDespesaOrcJDBC().createMovimentoDespesaOrc(pag);
            getConsignacaoJDBC().createReceitaExtra(pag);
            getInscricaoDiariaJDBC().createDiariaContabilizacao(pag);
            getSaldoDividaPublicaJDBC().createSaldoDividaPublica(pag, TipoLancamento.NORMAL, OperacaoMovimentoDividaPublica.PAGAMENTO_AMORTIZACAO, OperacaoDiarioDividaPublica.EMPENHO);
            getSaldoContaFinanceiraJDBC().createSaldoContaFinanceira(pag, TipoOperacao.DEBITO, MovimentacaoFinanceira.PAGAMENTO);
            getContabilizarPagamentoJDBC().contabilizarPagamento(pag, TipoLancamento.NORMAL);
            getUpdatePagamentoLiquidacaoJDBC().updatePagamentoLiquidacao(pag);
        } else {
            throw new ExcecaoNegocioGenerica("Saldo insuficiente na liquidação!");
        }
    }

    private boolean validaSaldoDaLiquidacao(Pagamento pag) {
        System.out.println("Saldo Liquidação: " + pag.getLiquidacao().getSaldo());
        System.out.println("Valor Pagamento: " + pag.getValor());
        System.out.println("Compare: " + (pag.getLiquidacao().getSaldo().subtract(pag.getValor()).compareTo(BigDecimal.ZERO) >= 0));
        return (pag.getLiquidacao().getSaldo().subtract(pag.getValor())).compareTo(BigDecimal.ZERO) >= 0;
    }

    @Override
    public Connection getConnection() {
        return instancia.conexao;
    }
}
