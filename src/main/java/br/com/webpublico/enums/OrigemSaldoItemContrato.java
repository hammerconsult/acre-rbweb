package br.com.webpublico.enums;

public enum OrigemSaldoItemContrato {

    CONTRATO("Contrato", 1),
    ADITIVO("Aditivo", 2),
    APOSTILAMENTO("Apostilamento", 3);
    private String descricao;
    private Integer ordem;

    OrigemSaldoItemContrato(String descricao, Integer ordem) {
        this.descricao = descricao;
        this.ordem = ordem;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public boolean isContrato() {
        return CONTRATO.equals(this);
    }

    public boolean isAditivo() {
        return ADITIVO.equals(this);
    }

    public boolean isApostilamento() {
        return APOSTILAMENTO.equals(this);
    }
}
