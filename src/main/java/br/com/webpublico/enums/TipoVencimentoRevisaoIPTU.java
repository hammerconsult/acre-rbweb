package br.com.webpublico.enums;

public enum TipoVencimentoRevisaoIPTU {
    VENCIMENTO_ATUAL("Vencimento Atual"),
    VENCIMENTO_OPCAO_PAGAMENTO("Vencimento da Opção de Pagamento");

    private final String descricao;

    TipoVencimentoRevisaoIPTU(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
