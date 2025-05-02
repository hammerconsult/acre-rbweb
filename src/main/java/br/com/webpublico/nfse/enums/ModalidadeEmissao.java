package br.com.webpublico.nfse.enums;

public enum ModalidadeEmissao {
    IDENTIFICADO("Identificado"), SEM_CPF("Sem CPF"), NAO_IDENTIFICADO("Não Identificado");

    private String descricao;

    ModalidadeEmissao(String descricao) {
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
