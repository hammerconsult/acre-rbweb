package br.com.webpublico.enums;

public enum SituacaoIntegracaoRHContabil {
    ABERTO("Aberto"),
    VALIDADO("Validado"),
    PROCESSADO("Processado"),
    ESTORNADO("Estornado");
    private String descricao;

    private SituacaoIntegracaoRHContabil(String descricao) {
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
