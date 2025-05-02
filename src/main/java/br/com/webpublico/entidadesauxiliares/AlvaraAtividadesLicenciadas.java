package br.com.webpublico.entidadesauxiliares;

public class AlvaraAtividadesLicenciadas {

    private Long alvaraId;
    private String codigoCnae;
    private String descricaoDetalhada;
    private String horarios;

    public AlvaraAtividadesLicenciadas() {
    }

    public AlvaraAtividadesLicenciadas(Long alvaraId, String codigoCnae, String descricaoDetalhada, String horarios) {
        this.alvaraId = alvaraId;
        this.codigoCnae = codigoCnae;
        this.descricaoDetalhada = descricaoDetalhada;
        this.horarios = horarios;
    }

    public Long getAlvaraId() {
        return alvaraId;
    }

    public void setAlvaraId(Long alvaraId) {
        this.alvaraId = alvaraId;
    }

    public String getCodigoCnae() {
        return codigoCnae;
    }

    public void setCodigoCnae(String codigoCnae) {
        this.codigoCnae = codigoCnae;
    }

    public String getDescricaoDetalhada() {
        return descricaoDetalhada;
    }

    public void setDescricaoDetalhada(String descricaoDetalhada) {
        this.descricaoDetalhada = descricaoDetalhada;
    }

    public String getHorarios() {
        return horarios;
    }

    public void setHorarios(String horarios) {
        this.horarios = horarios;
    }
}
