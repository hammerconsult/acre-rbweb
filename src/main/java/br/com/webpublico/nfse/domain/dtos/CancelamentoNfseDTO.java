package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.MotivoCancelamentoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.SituacaoDeferimentoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.enums.TipoCancelamentoNfseDTO;

import java.io.Serializable;
import java.util.Date;

public class CancelamentoNfseDTO implements Serializable, NfseDTO {

    private Long id;
    private Date dataCancelamento;
    private TipoCancelamentoNfseDTO tipoCancelamento;
    private TipoDocumentoCancelamentoNfseDTO tipoDocumento;
    private String solicitante;
    private MotivoCancelamentoNfseDTO motivoCancelamento;
    private SituacaoDeferimentoNfseDTO situacaoFiscal;
    private UserNfseDTO usuarioTomador;
    private SituacaoDeferimentoNfseDTO situacaoTomador;
    private String observacoesCancelamento;
    private String observacoesFiscal;
    private Date dataDeferimentoFiscal;
    private Date dataDeferimentoTomador;
    private NotaFiscalNfseDTO notaFiscal;
    private NotaFiscalNfseDTO notaFiscalSubstituta;
    private ServicoDeclaradoNfseDTO servicoDeclarado;
    private ServicoDeclaradoNfseDTO servicoDeclaradoSubstituto;
    private Boolean visualizadoPeloContribuinte;

    public CancelamentoNfseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public TipoCancelamentoNfseDTO getTipoCancelamento() {
        return tipoCancelamento;
    }

    public void setTipoCancelamento(TipoCancelamentoNfseDTO tipoCancelamento) {
        this.tipoCancelamento = tipoCancelamento;
    }

    public TipoDocumentoCancelamentoNfseDTO getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumentoCancelamentoNfseDTO tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public MotivoCancelamentoNfseDTO getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(MotivoCancelamentoNfseDTO motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public SituacaoDeferimentoNfseDTO getSituacaoFiscal() {
        return situacaoFiscal;
    }

    public void setSituacaoFiscal(SituacaoDeferimentoNfseDTO situacaoFiscal) {
        this.situacaoFiscal = situacaoFiscal;
    }

    public UserNfseDTO getUsuarioTomador() {
        return usuarioTomador;
    }

    public void setUsuarioTomador(UserNfseDTO usuarioTomador) {
        this.usuarioTomador = usuarioTomador;
    }

    public SituacaoDeferimentoNfseDTO getSituacaoTomador() {
        return situacaoTomador;
    }

    public void setSituacaoTomador(SituacaoDeferimentoNfseDTO situacaoTomador) {
        this.situacaoTomador = situacaoTomador;
    }

    public String getObservacoesCancelamento() {
        return observacoesCancelamento;
    }

    public void setObservacoesCancelamento(String observacoesCancelamento) {
        this.observacoesCancelamento = observacoesCancelamento;
    }

    public String getObservacoesFiscal() {
        return observacoesFiscal;
    }

    public void setObservacoesFiscal(String observacoesFiscal) {
        this.observacoesFiscal = observacoesFiscal;
    }

    public Date getDataDeferimentoFiscal() {
        return dataDeferimentoFiscal;
    }

    public void setDataDeferimentoFiscal(Date dataDeferimentoFiscal) {
        this.dataDeferimentoFiscal = dataDeferimentoFiscal;
    }

    public Date getDataDeferimentoTomador() {
        return dataDeferimentoTomador;
    }

    public void setDataDeferimentoTomador(Date dataDeferimentoTomador) {
        this.dataDeferimentoTomador = dataDeferimentoTomador;
    }

    public NotaFiscalNfseDTO getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(NotaFiscalNfseDTO notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public NotaFiscalNfseDTO getNotaFiscalSubstituta() {
        return notaFiscalSubstituta;
    }

    public void setNotaFiscalSubstituta(NotaFiscalNfseDTO notaFiscalSubstituta) {
        this.notaFiscalSubstituta = notaFiscalSubstituta;
    }

    public ServicoDeclaradoNfseDTO getServicoDeclarado() {
        return servicoDeclarado;
    }

    public void setServicoDeclarado(ServicoDeclaradoNfseDTO servicoDeclarado) {
        this.servicoDeclarado = servicoDeclarado;
    }

    public ServicoDeclaradoNfseDTO getServicoDeclaradoSubstituto() {
        return servicoDeclaradoSubstituto;
    }

    public void setServicoDeclaradoSubstituto(ServicoDeclaradoNfseDTO servicoDeclaradoSubstituto) {
        this.servicoDeclaradoSubstituto = servicoDeclaradoSubstituto;
    }

    public Boolean getVisualizadoPeloContribuinte() {
        return visualizadoPeloContribuinte;
    }

    public void setVisualizadoPeloContribuinte(Boolean visualizadoPeloContribuinte) {
        this.visualizadoPeloContribuinte = visualizadoPeloContribuinte;
    }
}
