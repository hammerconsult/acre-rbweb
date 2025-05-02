package br.com.webpublico.enums.rh.esocial;

public enum OrgaoClasse {
    CONSELHO_REGIONAL_MEDICINA("Conselho Regional de Medicina - CRM", 1),
    CONSELHO_REGIONAL_ODONTOLOGIA("Conselho Regional de Odontologia - CRO", 2),
    REGISTRO_MINISTERIO_SAUDE("Registro do Ministério da Saúde - RMS", 3);

    OrgaoClasse(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    private String descricao;
    private Integer codigo;


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
}
