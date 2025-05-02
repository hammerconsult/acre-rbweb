package br.com.webpublico.enums;

/**
 * Created by mga on 28/06/2017.
 */
public enum TipoMovimentoExecucaoOrcamentaria {

    EMPENHO("Empenho"),
    EMPENHO_ESTORNO("Empenho Estorno"),
    OBRIGACAO_PAGAR("Obrigação a Pagar"),
    LIQUIDACAO("Liquidação"),
    LIQUIDACAO_ESTORNO("Liquidação Estorno"),
    PAGAMENTO("Pagamento"),
    PAGAMENTO_ESTORNO("Pagamento Estorno");
    private String descricao;

    TipoMovimentoExecucaoOrcamentaria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
