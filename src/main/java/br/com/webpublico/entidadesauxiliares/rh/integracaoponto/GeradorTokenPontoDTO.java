package br.com.webpublico.entidadesauxiliares.rh.integracaoponto;

public class GeradorTokenPontoDTO {
    private String grant_type;
    private Integer client_id;
    private String client_secret;

    public GeradorTokenPontoDTO() {
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public Integer getClient_id() {
        return client_id;
    }

    public void setClient_id(Integer client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }
}
