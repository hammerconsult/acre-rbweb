package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;

public class ResultadoSorteioDTO implements Serializable {

    private Long idPremio;
    private Integer sequenciaPremio;
    private String descricaoPremio;
    private Long idCupom;
    private String numeroCupom;

    public Long getIdPremio() {
        return idPremio;
    }

    public void setIdPremio(Long idPremio) {
        this.idPremio = idPremio;
    }

    public Integer getSequenciaPremio() {
        return sequenciaPremio;
    }

    public void setSequenciaPremio(Integer sequenciaPremio) {
        this.sequenciaPremio = sequenciaPremio;
    }

    public String getDescricaoPremio() {
        return descricaoPremio;
    }

    public void setDescricaoPremio(String descricaoPremio) {
        this.descricaoPremio = descricaoPremio;
    }

    public Long getIdCupom() {
        return idCupom;
    }

    public void setIdCupom(Long idCupom) {
        this.idCupom = idCupom;
    }

    public String getNumeroCupom() {
        return numeroCupom;
    }

    public void setNumeroCupom(String numeroCupom) {
        this.numeroCupom = numeroCupom;
    }
}
