package br.com.webpublico.pncp.dto;

import br.com.webpublico.pncp.entidadeauxiliar.EventoPncpVO;

public class UnidadeConsultaDTO {

    private Long idUnidade;
    private Long idUnidadePncp;
    private String cnpj;
    private String codigoUnidade;
    private String nomeUnidade;
    private String codigoIBGE;
    private Boolean integrado;
    private EventoPncpVO eventoPncpVO;

    public Long getIdUnidade() {
        return idUnidade;
    }

    public void setIdUnidade(Long idUnidade) {
        this.idUnidade = idUnidade;
    }

    public Long getIdUnidadePncp() {
        return idUnidadePncp;
    }

    public void setIdUnidadePncp(Long idUnidadePncp) {
        this.idUnidadePncp = idUnidadePncp;
    }

    public String getCodigoIBGE() {
        return codigoIBGE;
    }

    public void setCodigoIBGE(String codigoIBGE) {
        this.codigoIBGE = codigoIBGE;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getNomeUnidade() {
        return nomeUnidade;
    }

    public void setNomeUnidade(String nomeUnidade) {
        this.nomeUnidade = nomeUnidade;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Boolean getIntegradoPNCP() {
        if (integrado == null) {
            integrado = Boolean.FALSE;
        }
        return integrado;
    }

    public void setIntegradoPNCP(Boolean integradoPNCP) {
        integrado = integradoPNCP;
    }

    public EventoPncpVO getEventoPncpVO() {
        return eventoPncpVO;
    }

    public void setEventoPncpVO(EventoPncpVO eventoPncpVO) {
        this.eventoPncpVO = eventoPncpVO;
    }
}
