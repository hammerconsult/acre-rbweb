package br.com.webpublico.enums.tributario.regularizacaoconstrucao;


public enum TipoMedida {

    HECTARES(1, "Hectares"),
    METRO_QUADRADO(2, "Metro Quadrado"),
    METRO(3, "Metro"),
    TIPO_DE_BOMBA_JAZIGO_SIMPLES(4, "Tipo de bomba: Jazigo Simples"),
    TIPO_DE_BOMBA_JAZIGO_DE_LUXO(5, "Tipo de bomba: Jazigo de Luxo"),
    TIPO_DE_CONSTRUCAO_RESIDENCIAL(6, "Tipo de construção: Residencial"),
    TIPO_DE_CONSTRUCAO_COMERCIAL(7, "Tipo de construção: Comercial"),
    TIPO_DE_CONSTRUCAO_INDUSTRIAL(8, "Tipo de construção: Industrial"),
    UNIDADE(9, "Unidade");

    private Integer codigo;
    private String descricao;

    TipoMedida(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return this.codigo + " - " + this.descricao;
    }
}
