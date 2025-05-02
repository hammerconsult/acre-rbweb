package br.com.webpublico.pncp.dto;

import br.com.webpublico.pncp.entidadeauxiliar.EventoPncpVO;
import br.com.webpublico.pncp.enums.TipoProcessoPncp;

import java.util.Date;
import java.util.List;

public class ContratacaoConsultaDTO {

    private Long id;
    private String idPncp;
    private String sequencialIdPncp;
    private Long idProcessoCompra;
    private String codigoUnidadeCompradora;
    private String modalidadeId;
    private String numeroCompra;
    private Integer anoCompra;
    private String numeroProcesso;
    private String objetoCompra;
    private Date dataAberturaProposta;
    private TipoProcessoPncp tipoProcesso;
    private EventoPncpVO eventoPncpVO;
    private List<ItemCompraConsultaDTO> itensCompra;
    private List<ResultadoItemConsultaDTO> resultados;

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

    public Long getIdProcessoCompra() {
        return idProcessoCompra;
    }

    public void setIdProcessoCompra(Long idProcessoCompra) {
        this.idProcessoCompra = idProcessoCompra;
    }

    public String getModalidadeId() {
        return modalidadeId;
    }

    public void setModalidadeId(String modalidadeId) {
        this.modalidadeId = modalidadeId;
    }

    public String getCodigoUnidadeCompradora() {
        return codigoUnidadeCompradora;
    }

    public void setCodigoUnidadeCompradora(String codigoUnidadeCompradora) {
        this.codigoUnidadeCompradora = codigoUnidadeCompradora;
    }

    public String getNumeroCompra() {
        return numeroCompra;
    }

    public void setNumeroCompra(String numeroCompra) {
        this.numeroCompra = numeroCompra;
    }

    public Integer getAnoCompra() {
        return anoCompra;
    }

    public void setAnoCompra(Integer anoCompra) {
        this.anoCompra = anoCompra;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(String objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public Date getDataAberturaProposta() {
        return dataAberturaProposta;
    }

    public void setDataAberturaProposta(Date dataAberturaProposta) {
        this.dataAberturaProposta = dataAberturaProposta;
    }

    public List<ItemCompraConsultaDTO> getItensCompra() {
        return itensCompra;
    }

    public void setItensCompra(List<ItemCompraConsultaDTO> itensCompra) {
        this.itensCompra = itensCompra;
    }

    public List<ResultadoItemConsultaDTO> getResultados() {
        return resultados;
    }

    public void setResultados(List<ResultadoItemConsultaDTO> resultados) {
        this.resultados = resultados;
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
}
