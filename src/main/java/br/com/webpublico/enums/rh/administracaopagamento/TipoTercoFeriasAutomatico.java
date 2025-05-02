package br.com.webpublico.enums.rh.administracaopagamento;

public enum TipoTercoFeriasAutomatico {
    FINAL_PERIODO_AQUISITIVO(1, "Baseando-se no Final de Vigência do Período Aquisitivo"),
    ANIVERSARIO_CONTRATO(2, "Baseando-se no Aniversário de Contrato");
    private Integer codigo;
    private String descricao;

    TipoTercoFeriasAutomatico(Integer codigo, String descricao) {
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
        return codigo + " - " + descricao;
    }
}
