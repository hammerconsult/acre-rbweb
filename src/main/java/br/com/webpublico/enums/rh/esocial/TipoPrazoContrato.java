package br.com.webpublico.enums.rh.esocial;

/**
 * Created by William on 11/09/2018.
 */
public enum TipoPrazoContrato {
    PRAZO_INDETERMINADO("Prazo Indeterminado", 1),
    PRAZO_DETERMINADO("Prazo Determinado", 2),
    PRAZO_DETERMINADO_VINCULADO_OCORRENCIA("Prazo determinado, vinculado à ocorrência de um fato", 2);

    private String descricao;
    private Integer codigoESocial;

    TipoPrazoContrato(String descricao, Integer codigoESocial) {
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
