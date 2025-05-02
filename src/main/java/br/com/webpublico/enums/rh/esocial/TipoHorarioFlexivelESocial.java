package br.com.webpublico.enums.rh.esocial;

public enum TipoHorarioFlexivelESocial {
    INTERVALO_EM_HORARIO_FIXO("Intervalo em Horário Fixo", 1),
    INTERVALO_EM_HORARIO_VARIAVEL("Intervalo em Horário Variável", 2);

    private String descricao;
    private Integer codigo;

    TipoHorarioFlexivelESocial(String descricao, Integer codigo) {
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
