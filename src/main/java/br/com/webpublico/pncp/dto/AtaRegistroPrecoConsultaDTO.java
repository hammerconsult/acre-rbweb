package br.com.webpublico.pncp.dto;

import br.com.webpublico.pncp.entidadeauxiliar.EventoPncpVO;
import br.com.webpublico.pncp.enums.TipoProcessoPncp;

import java.util.Date;

public class AtaRegistroPrecoConsultaDTO {
    private Long id;
    private String idPncp;
    private String sequencialIdPncp;
    private Integer anoCompra;
    private String sequencialCompra;
    private String codigoUnidadeCompradora;
    private String numeroAtaRegistroPreco;
    private Integer anoAta;
    private Date dataAssinatura;
    private Date dataVigenciaInicio;
    private Date dataVigenciaFim;
    private TipoProcessoPncp tipoProcesso;
    private EventoPncpVO eventoPncpVO;

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

    public Integer getAnoCompra() {
        return anoCompra;
    }

    public void setAnoCompra(Integer anoCompra) {
        this.anoCompra = anoCompra;
    }

    public String getSequencialCompra() {
        return sequencialCompra;
    }

    public void setSequencialCompra(String sequencialCompra) {
        this.sequencialCompra = sequencialCompra;
    }

    public String getCodigoUnidadeCompradora() {
        return codigoUnidadeCompradora;
    }

    public void setCodigoUnidadeCompradora(String codigoUnidadeCompradora) {
        this.codigoUnidadeCompradora = codigoUnidadeCompradora;
    }

    public String getNumeroAtaRegistroPreco() {
        return numeroAtaRegistroPreco;
    }

    public void setNumeroAtaRegistroPreco(String numeroAtaRegistroPreco) {
        this.numeroAtaRegistroPreco = numeroAtaRegistroPreco;
    }

    public Integer getAnoAta() {
        return anoAta;
    }

    public void setAnoAta(Integer anoAta) {
        this.anoAta = anoAta;
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public Date getDataVigenciaInicio() {
        return dataVigenciaInicio;
    }

    public void setDataVigenciaInicio(Date dataVigenciaInicio) {
        this.dataVigenciaInicio = dataVigenciaInicio;
    }

    public Date getDataVigenciaFim() {
        return dataVigenciaFim;
    }

    public void setDataVigenciaFim(Date dataVigenciaFim) {
        this.dataVigenciaFim = dataVigenciaFim;
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
