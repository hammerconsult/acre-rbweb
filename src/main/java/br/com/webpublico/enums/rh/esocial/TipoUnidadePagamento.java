package br.com.webpublico.enums.rh.esocial;

public enum TipoUnidadePagamento {
    HORA("Por Hora", 1),
    DIA("Por Dia", 2),
    SEMANA("Por Semana", 3),
    QUINZENA("Por Quinzena", 4),
    MES("Por Mês", 5),
    TAREFA("Por Tarefa", 6),
    NA0_APLICAVEL("Não aplicável - salário exclusivamente variável", 7);

    private String descricao;
    private Integer codigo;

    TipoUnidadePagamento(String descricao, Integer codigo) {
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
        return descricao;
    }
}
