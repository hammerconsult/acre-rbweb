package br.com.webpublico.enums.rh.esocial;

/**
 * Created by William on 11/09/2018.
 */
public enum TipoJornadaTrabalho {
    JORNADA_HORARIO_DIARIO_FOLGA_FIXA("Jornada com horário diário fixo e folga fixa (no domingo)", 4),
    JORNADA_12_X_36("Jornada 12 x 36 (12 horas de trabalho seguidas de 36 horas ininterruptas de descanso)", 2),
    JORNADA_HORARIO_DIARIO_FIXO_FOLGA_VARIAVEL("Jornada com horário diário fixo e folga variável", 3),
    JORNADA_HORARIO_DIARIO_FOLGA_FIXA_EXCETO_DOMINGO("Jornada com horário diário fixo e folga fixa (exceto no domingo)", 5),
    JORNADA_HORARIO_DIARIO_FOLGA_PERIODICA(" Jornada com horário diário fixo e folga fixa (em outro dia da semana), com folga adicional periódica no domingo", 6),
    TURNO_ININTERRUPTO_REVEZAMENTO("Turno ininterrupto de revezamento", 7),
    DEMAIS_JORNADAS(" Demais tipos de jornada", 9);

    private String descricao;
    private Integer codigoESocial;

    TipoJornadaTrabalho(String descricao, Integer codigoESocial) {
        this.descricao = descricao;
        this.codigoESocial = codigoESocial;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodigoESocial() {
        return codigoESocial;
    }

    public void setCodigoESocial(Integer codigoESocial) {
        this.codigoESocial = codigoESocial;
    }

    @Override
    public String toString() {
        return codigoESocial + " - " + descricao;
    }
}
