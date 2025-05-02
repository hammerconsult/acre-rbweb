package br.com.webpublico.enums.tributario;

public enum TipoDamEmitido {

    SIM(1, "Sim"),
    NAO(2, " Não");

    private Integer codigo;
    private String descricao;

    TipoDamEmitido(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
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
