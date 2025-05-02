package br.com.webpublico.pncp.dto;

import br.com.webpublico.pncp.entidadeauxiliar.EventoPncpVO;
import br.com.webpublico.pncp.enums.TipoProcessoPncp;

import java.util.Date;
import java.util.List;

public class PlanoContratacaoAnualConsultaDTO {
    private Long id;
    private String idPncp;
    private String sequencialIdPncp;
    private String cnpj;
    private Long numero;
    private Integer anoPca;
    private Date inicioElaboracao;
    private Date fimElaboracao;
    private String codigoUnidade;
    private TipoProcessoPncp tipoProcesso;
    private EventoPncpVO eventoPncpVO;
    private List<PlanoContratacaoAnualItemConsultaDTO> itensPlano;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdPncp() {
        return idPncp;
    }

    public void setIdPncp(String idPncp) {
        this.idPncp = idPncp;
    }

    public String getSequencialIdPncp() {
        return sequencialIdPncp;
    }

    public void setSequencialIdPncp(String sequencialIdPncp) {
        this.sequencialIdPncp = sequencialIdPncp;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public Integer getAnoPca() {
        return anoPca;
    }

    public void setAnoPca(Integer anoPca) {
        this.anoPca = anoPca;
    }

    public Date getInicioElaboracao() {
        return inicioElaboracao;
    }

    public void setInicioElaboracao(Date inicioElaboracao) {
        this.inicioElaboracao = inicioElaboracao;
    }

    public Date getFimElaboracao() {
        return fimElaboracao;
    }

    public void setFimElaboracao(Date fimElaboracao) {
        this.fimElaboracao = fimElaboracao;
    }

    public EventoPncpVO getEventoPncpVO() {
        return eventoPncpVO;
    }

    public void setEventoPncpVO(EventoPncpVO eventoPncpVO) {
        this.eventoPncpVO = eventoPncpVO;
    }

    public TipoProcessoPncp getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcessoPncp tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public List<PlanoContratacaoAnualItemConsultaDTO> getItensPlano() {
        return itensPlano;
    }

    public void setItensPlano(List<PlanoContratacaoAnualItemConsultaDTO> itensPlano) {
        this.itensPlano = itensPlano;
    }
}
