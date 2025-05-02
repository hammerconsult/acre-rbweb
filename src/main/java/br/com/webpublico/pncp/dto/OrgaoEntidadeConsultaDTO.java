package br.com.webpublico.pncp.dto;

import br.com.webpublico.enums.EsferaDoPoder;
import br.com.webpublico.enums.EsferaGovernamental;
import br.com.webpublico.pncp.entidadeauxiliar.EventoPncpVO;

import java.util.List;

public class OrgaoEntidadeConsultaDTO {

    private Long idEntidade;
    private Long idEntidadePncp;
    private String cnpj;
    private String razaoSocial;
    private EsferaDoPoder poderId;
    private EsferaGovernamental esferaId;
    private Boolean integrado;
    private String codigo;
    private EventoPncpVO eventoPncpVO;
    private List<UnidadeConsultaDTO> unidades;
    private List<EnteAutorizadoDTO> entesAutorizados;

    public Long getIdEntidade() {
        return idEntidade;
    }

    public void setIdEntidade(Long idEntidade) {
        this.idEntidade = idEntidade;
    }

    public Long getIdEntidadePncp() {
        return idEntidadePncp;
    }

    public void setIdEntidadePncp(Long idEntidadePncp) {
        this.idEntidadePncp = idEntidadePncp;
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

    public List<UnidadeConsultaDTO> getUnidades() {
        return unidades;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setUnidades(List<UnidadeConsultaDTO> unidades) {
        this.unidades = unidades;
    }

    public EventoPncpVO getEventoPncpVO() {
        return eventoPncpVO;
    }

    public void setEventoPncpVO(EventoPncpVO eventoPncpVO) {
        this.eventoPncpVO = eventoPncpVO;
    }

    public Boolean getIntegrado() {
        if (integrado == null) {
            integrado = Boolean.FALSE;
        }
        return integrado;
    }

    public void setIntegrado(Boolean integrado) {
        this.integrado = integrado;
    }

    public List<EnteAutorizadoDTO> getEntesAutorizados() {
        return entesAutorizados;
    }

    public void setEntesAutorizados(List<EnteAutorizadoDTO> entesAutorizados) {
        this.entesAutorizados = entesAutorizados;
    }
}
