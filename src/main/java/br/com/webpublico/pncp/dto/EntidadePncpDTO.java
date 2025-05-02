package br.com.webpublico.pncp.dto;

import br.com.webpublico.enums.EsferaGovernamental;
import br.com.webpublico.enums.EsferaDoPoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntidadePncpDTO {

    private Long id;
    private String cnpj;
    private String razaoSocial;
    private String cnpjEnteResponsavel;
    private EsferaDoPoder poderId;
    private EsferaGovernamental esferaId;
    private Boolean validado;
    private Date dataValidacao;
    private Boolean isIntegradoPNCP;
    private String codigo;
    private List<OrgaoPncpDTO> orgaos = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpjEnteResponsavel() {
        return cnpjEnteResponsavel;
    }

    public void setCnpjEnteResponsavel(String cnpjEnteResponsavel) {
        this.cnpjEnteResponsavel = cnpjEnteResponsavel;
    }

    public EsferaDoPoder getPoderId() {
        return poderId;
    }

    public void setPoderId(EsferaDoPoder poderId) {
        this.poderId = poderId;
    }

    public EsferaGovernamental getEsferaId() {
        return esferaId;
    }

    public void setEsferaId(EsferaGovernamental esferaId) {
        this.esferaId = esferaId;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    public Date getDataValidacao() {
        return dataValidacao;
    }

    public void setDataValidacao(Date dataValidacao) {
        this.dataValidacao = dataValidacao;
    }

    public Boolean getIntegradoPNCP() {
        return isIntegradoPNCP;
    }

    public void setIntegradoPNCP(Boolean integradoPNCP) {
        isIntegradoPNCP = integradoPNCP;
    }

    public List<OrgaoPncpDTO> getOrgaos() {
        return orgaos;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
