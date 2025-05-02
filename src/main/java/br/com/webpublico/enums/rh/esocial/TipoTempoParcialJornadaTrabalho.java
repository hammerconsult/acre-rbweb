package br.com.webpublico.enums.rh.esocial;

/**
 * Created by William on 11/09/2018.
 */
public enum TipoTempoParcialJornadaTrabalho {
    NAO_E_CONTRATO_PARCIAL("Não é contrato em tempo parcial", 0),
    LIMITADO_25_HORAS_SEMANAIS("Limitado a 25 horas semanais", 1),
    LIMITADO_30_HORAS_SEMANAIS("Limitado a 30 horas semanais", 2),
    LIMITADO_26_HORAS_SEMANAIS("Limitado a 26 horas semanais", 3);


    private String descricao;
    private Integer codigoESocial;

    TipoTempoParcialJornadaTrabalho(String descricao, Integer codigoESocial) {
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
