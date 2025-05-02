package br.com.webpublico.enums.rh.esocial;

public enum TipoResponsavelAmbiental {
    CONSELHO_REGIONAL_MEDICINA("Conselho Regional de Medicina - CRM", 1),
    CONSELHO_REGIONAL_ENGENHARIA_E_AGRONOMIA("Conselho Regional de Engenharia e Agronomia - CREA", 4),
    OUTROS("Outros", 9);

    private String descricao;
    private Integer codigo;

    TipoResponsavelAmbiental(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
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
        return codigo + " - " + descricao;
    }
}
