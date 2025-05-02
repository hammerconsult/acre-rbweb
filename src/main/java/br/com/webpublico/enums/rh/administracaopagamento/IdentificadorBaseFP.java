package br.com.webpublico.enums.rh.administracaopagamento;

public enum IdentificadorBaseFP {
    PREVIDENCIA_PROPRIA("Previdência Própria"),
    PREVIDENCIA_PROPRIA_13("Previdência Própria 13º Salário"),
    BASE_PREVIDENCIA_GERAL_MENSAL("Base Previdência Geral Mensal"),
    BASE_PREVIDENCIA_GERAL_13("Base Previdência Geral 13º Salário"),
    BASE_IRRF_MENSAL("Base IRRF Mensal"),
    BASE_IRRF_13("Base IRRF 13º Salário");


    private String descricao;

    IdentificadorBaseFP(String descricao) {
        this.descricao = descricao;
    }

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
