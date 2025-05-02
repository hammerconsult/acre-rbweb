package br.com.webpublico.enums.rh.sig;

public enum TipoAposentadoriaEspecialSIG {
    APOSENTADORIA_ESPECIAL_DO_PROFESSOR("Aposentadoria Especial do Professor", 1),
    APOSENTADORIA_ESPECIAL_DO_POLICIAL_E_OU_AGENTES_PRISIONAIS("Aposentadoria Especial do Policial e/ou Agentes Prisionais", 2),
    APOSENTADORIA_ESPECIAL_PARA_PORTADOR_DE_DEFICIENCIA("Aposentadoria Especial para Portador de Deficiência", 3),
    APOSENTADORIA_ESPECIAL_SERVIDOR_QUE_EXERCE_SUAS_FUNÇÕES_EM_CONDIÇÕES_QUE_PREJUDIQUEM_A_SAUDE_E_A_INTEGRIDADE_FISICA("Servidor que exerce suas funções em condições que prejudiquem a saúde e a integridade física", 4);

    private Integer codigo;
    private String descricao;

    TipoAposentadoriaEspecialSIG(String descricao, Integer codigo) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return getCodigo() + " - " + getDescricao();
    }
}

