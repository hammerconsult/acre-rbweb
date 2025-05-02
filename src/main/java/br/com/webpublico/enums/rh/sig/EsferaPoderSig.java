package br.com.webpublico.enums.rh.sig;

public enum EsferaPoderSig {
    MUNICIPAL("Municipal", "1"),
    ESTADUAL("Estadual", "2"),
    FEDERAL("Federal", "3");

    private String descricao;
    private String codigo;

    EsferaPoderSig(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
