package br.com.webpublico.pncp.dto;

import br.com.webpublico.pncp.entidadeauxiliar.EventoPncpVO;
import br.com.webpublico.pncp.enums.TipoProcessoPncp;

import java.util.Date;

public class ContratoEmpenhoConsultaDTO {

    private Long id;
    private TipoProcessoPncp tipoProcesso;
    private String idPncp;
    private String sequencialIdPncp;
    private Long idProcessoCompra;
    private Integer anoCompra;
    private Integer sequencialCompra;
    private String numeroContratoEmpenho;
    private Integer anoContrato;
    private String processo;
    private String codigoUnidade;
    private String niFornecedor;
    private String nomeRazaoSocialFornecedor;
    private Date dataAssinatura;
    private EventoPncpVO eventoPncpVO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoProcessoPncp getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcessoPncp tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
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

    public Long getIdProcessoCompra() {
        return idProcessoCompra;
    }

    public void setIdProcessoCompra(Long idProcessoCompra) {
        this.idProcessoCompra = idProcessoCompra;
    }

    public Integer getAnoCompra() {
        return anoCompra;
    }

    public void setAnoCompra(Integer anoCompra) {
        this.anoCompra = anoCompra;
    }

    public Integer getSequencialCompra() {
        return sequencialCompra;
    }

    public void setSequencialCompra(Integer sequencialCompra) {
        this.sequencialCompra = sequencialCompra;
    }

    public String getNumeroContratoEmpenho() {
        return numeroContratoEmpenho;
    }

    public void setNumeroContratoEmpenho(String numeroContratoEmpenho) {
        this.numeroContratoEmpenho = numeroContratoEmpenho;
    }

    public Integer getAnoContrato() {
        return anoContrato;
    }

    public void setAnoContrato(Integer anoContrato) {
        this.anoContrato = anoContrato;
    }

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getNiFornecedor() {
        return niFornecedor;
    }

    public void setNiFornecedor(String niFornecedor) {
        this.niFornecedor = niFornecedor;
    }

    public String getNomeRazaoSocialFornecedor() {
        return nomeRazaoSocialFornecedor;
    }

    public void setNomeRazaoSocialFornecedor(String nomeRazaoSocialFornecedor) {
        this.nomeRazaoSocialFornecedor = nomeRazaoSocialFornecedor;
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public EventoPncpVO getEventoPncpVO() {
        return eventoPncpVO;
    }

    public void setEventoPncpVO(EventoPncpVO eventoPncpVO) {
        this.eventoPncpVO = eventoPncpVO;
    }

    public String getNumeroAnoTermo() {
        return numeroContratoEmpenho + " / " + anoContrato;
    }
}
