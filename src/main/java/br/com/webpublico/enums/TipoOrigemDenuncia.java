package br.com.webpublico.enums;

public enum TipoOrigemDenuncia {
    FORMULARIO("Formulário", "02"),
    OFICIO("Ofício", "03"),
    PROCESSO("Processo", "01");

    private String descricao;
    private String codigo;

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    private TipoOrigemDenuncia(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }


    @Override
    public String toString() {
        return descricao;
    }
}
