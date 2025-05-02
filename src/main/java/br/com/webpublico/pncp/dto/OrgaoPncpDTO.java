package br.com.webpublico.pncp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrgaoPncpDTO {
    private Long id;
    private String codigoIBGE;
    @JsonProperty("codigoUnidade")
    private String codigo;
    @JsonProperty("nomeUnidade")
    private String nome;
    private Boolean isIntegradoPNCP;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoIBGE() {
        return codigoIBGE;
    }

    public void setCodigoIBGE(String codigoIBGE) {
        this.codigoIBGE = codigoIBGE;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getIntegradoPNCP() {
        if (isIntegradoPNCP == null) {
            isIntegradoPNCP = Boolean.FALSE;
        }
        return isIntegradoPNCP;
    }

    public void setIntegradoPNCP(Boolean integradoPNCP) {
        isIntegradoPNCP = integradoPNCP;
    }
}
