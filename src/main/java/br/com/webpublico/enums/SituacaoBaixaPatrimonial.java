package br.com.webpublico.enums;

public enum SituacaoBaixaPatrimonial {

    EM_ELABORACAO("Em Elaboração"),
    AGUARDANDO_PARECER("Aguardando Parecer"),
    AGUARDANDO_EFETIVACAO("Aguardando Efetivação"),
    FINALIZADA("Finalizada");
    private String descricao;

    SituacaoBaixaPatrimonial(String descricao) {
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
