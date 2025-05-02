package br.com.webpublico.enums.rh.relatorios;

public enum TipoContribuicao {

    RPPS("RPPS", "3"),
    INSS("INSS", "1"),
    AC_PREVIDENCIA("AC Previdência", "4");
    private String descricao;
    private String codigo;

    TipoContribuicao(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
