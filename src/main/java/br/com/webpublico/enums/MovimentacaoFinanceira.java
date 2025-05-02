package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 24/09/13
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
public enum MovimentacaoFinanceira {
    AJUSTE_ATIVO_DISPONIVEL_NORMAL("Ajuste Ativo Disponível Normal", "AjusteAtivoDisponivel"),
    AJUSTE_ATIVO_DISPONIVEL_ESTORNO("Ajuste Ativo Disponível Estorno", "AjusteAtivoDisponivel"),
    DESPESA_EXTRA("Despesa Extraorçamentária", "PagamentoExtra"),
    ESTORNO_DE_PAGAMENTO("Estorno de Pagamento", "PagamentoEstorno"),
    ESTORNO_DE_PAGAMENTO_DE_RESTOS_A_PAGAR("Estorno de Pagamento de Restos a Pagar", "PagamentoEstorno"),
    ESTORNO_DE_RECEITA_REALIZADA("Estorno de Receita Realizada", "ReceitaORCEstorno"),
    ESTORNO_DA_DESPESA_EXTRA("Estorno da Despesa Extraorçamentária", "PagamentoExtraEstorno"),
    ESTORNO_DA_TRANFERENCIA_FINANCEIRA_DE_MESMA_UNIDADE("Estorno de Transferência Financeira Mesma Unidade", "EstornoTransfMesmaUnidade"),
    ESTORNO_TRANSFERENCIA_SALDO_CONTA_FINANCEIRA("Estorno de Transferência Financeira", "EstornoTransferencia"),
    ESTORNO_LIBERACAO_SALDO_CONTA_FINANCEIRA("Estorno de Liberação Financeira", "EstornoLibCotaFinanceira"),
    ESTORNO_DE_RECEITA_EXTRA("Estorno de Receita Extraorçamentária", "ReceitaExtraEstorno"),
    OPERACAO_DA_CONCILIACAO("Pendência da Conciliação Bancária", "LancamentoConciliacaoBancaria"),
    LIBERACAO_DE_COTA_FINANCEIRA("Liberação Financeira", "LiberacaoCotaFinanceira"),
    PAGAMENTO("Pagamento", "Pagamento"),
    PAGAMENTO_DE_RESTOS_A_PAGAR("Pagamento de Restos a Pagar", "Pagamento"),
    RECEITA_EXTRAORCAMENTARIA("Receita Extraorçamentária", "ReceitaExtra"),
    RECEITA_REALIZADA("Receita Realizada", "LancamentoReceitaOrc"),
    TRANSFERENCIA_FINANCEIRA("Transferência Financeira", "TransferenciaContaFinanceira"),
    TRANSPARENCIA_DE_SALDO_NA_MESMA_UNIDADE("Transferência Financeira Mesma Unidade", "TransferenciaMesmaUnidade");

    private String descricao;
    private String classe;

    private MovimentacaoFinanceira(String descricao, String classe) {
        this.descricao = descricao;
        this.classe = classe;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
