package br.com.webpublico.enums;

public enum QualidadeDaConstrucao {
    ESPECIAL(1, "Especial"),
    ELEVADO(2, "Elevado"),
    MEDIO(3, "Médio"),
    REGULAR(4, "Regular"),
    SIMPLES(5, "Simples"),
    RESIDENCIA_MULTIFAMILIAR(6, "Residencial Multifamiliar");
    private String descricao;
    private Integer codigo;

    private QualidadeDaConstrucao(Integer codigo, String descricao) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public static String getQualidade(Integer codigo){
        switch (codigo){
            case 1 : return ESPECIAL.getDescricao();
            case 2 : return ELEVADO.getDescricao();
            case 3 : return MEDIO.getDescricao();
            case 4 : return REGULAR.getDescricao();
            case 5 : return SIMPLES.getDescricao();
            default: return RESIDENCIA_MULTIFAMILIAR.getDescricao();
        }
    }
}
