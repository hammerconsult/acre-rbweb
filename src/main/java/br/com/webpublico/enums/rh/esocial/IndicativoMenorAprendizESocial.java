package br.com.webpublico.enums.rh.esocial;

public enum IndicativoMenorAprendizESocial {
    DISPENSADO_ACORDO_COM_A_LEI("Dispensado de acordo com a lei", 0),
    DISPENSADO_PROCESSO_JUDICIAL("Dispensado, mesmo que parcialmente, em virtude de processo judicial", 1),
    OBRIGADO("Obrigado", 2);

    private String descricao;
    private Integer codigo;

    IndicativoMenorAprendizESocial(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
