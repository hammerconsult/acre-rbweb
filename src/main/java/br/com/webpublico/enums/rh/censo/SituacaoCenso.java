package br.com.webpublico.enums.rh.censo;

public enum SituacaoCenso {
    AGUARDANDO_LIBERACAO("Aguardando Liberação"),
    LIBERADO("Liberado"),
    REJEITADO("Rejeitado");

    SituacaoCenso(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
