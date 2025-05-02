package br.com.webpublico.enums;

/**
 * @Author peixe on 13/09/2016  14:45.
 */
public enum LocalAcidente {

    ESTABELECIMENTO_EMPREGADOR_BRASIL("Estabelecimento do Empregador no Brasil", 1),
    ESTABELECIMENTO_EMPREGADOR_EXTERIOR("Estabelecimento do Empregador no exterior", 2),
    ESTABELECIMENTO_TERCEIROS("Estabelecimento de terceiros onde o empregador presta serviços", 3),
    VIA_PUBLICA("Via Pública", 4),
    AREA_RURAL("Área Rural", 5),
    EMBARCACAO("Embarcação", 6),
    OUTROS("Outros", 9);

    private String descricao;
    private Integer codigoEsocial;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodigoEsocial() {
        return codigoEsocial;
    }

    public void setCodigoEsocial(Integer codigoEsocial) {
        this.codigoEsocial = codigoEsocial;
    }

    LocalAcidente(String descricao, Integer codigoEsocial) {
        this.descricao = descricao;
        this.codigoEsocial = codigoEsocial;
    }

    @Override
    public String toString() {
        return codigoEsocial + " - " + descricao;
    }
}
