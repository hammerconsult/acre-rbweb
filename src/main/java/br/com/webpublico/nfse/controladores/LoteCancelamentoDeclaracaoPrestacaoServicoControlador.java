package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.RevisaoAuditoria;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.nfse.domain.CancelamentoDeclaracaoPrestacaoServico;
import br.com.webpublico.nfse.domain.LoteCancelamentoDeclaracaoPrestacaoServico;
import br.com.webpublico.nfse.domain.NotaFiscal;
import br.com.webpublico.nfse.domain.dtos.NotaFiscalSearchDTO;
import br.com.webpublico.nfse.domain.dtos.enums.SituacaoDeclaracaoNfseDTO;
import br.com.webpublico.nfse.enums.MotivoCancelamentoNota;
import br.com.webpublico.nfse.enums.TipoDocumentoNfse;
import br.com.webpublico.nfse.facades.LoteCancelamentoDeclaracaoPrestacaoServicoFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-lote-cancelamento-nfse", pattern = "/nfse/lote-cancelamento-nfse/novo/",
        viewId = "/faces/tributario/nfse/lote-cancelamento/edita.xhtml"),
    @URLMapping(id = "listar-lote-cancelamento-nfse", pattern = "/nfse/lote-cancelamento-nfse/listar/",
        viewId = "/faces/tributario/nfse/lote-cancelamento/lista.xhtml"),
    @URLMapping(id = "ver-lote-cancelamento-nfse", pattern = "/nfse/lote-cancelamento-nfse/ver/#{loteCancelamentoDeclaracaoPrestacaoServicoControlador.id}/",
        viewId = "/faces/tributario/nfse/lote-cancelamento/visualizar.xhtml"),
})
public class LoteCancelamentoDeclaracaoPrestacaoServicoControlador extends PrettyControlador<LoteCancelamentoDeclaracaoPrestacaoServico> implements CRUD {

    @EJB
    private LoteCancelamentoDeclaracaoPrestacaoServicoFacade facade;
    private CadastroEconomico cadastroEconomico;
    private Long numero;
    private NotaFiscalSearchDTO notaFiscalSearch;
    private Future<LoteCancelamentoDeclaracaoPrestacaoServico> future;
    private AssistenteBarraProgresso assistente;
    private Boolean finalizado = Boolean.FALSE;


    public LoteCancelamentoDeclaracaoPrestacaoServicoControlador() {
        super(LoteCancelamentoDeclaracaoPrestacaoServico.class);
    }

    @Override
    public LoteCancelamentoDeclaracaoPrestacaoServicoFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/lote-cancelamento-nfse/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public NotaFiscalSearchDTO getNotaFiscalSearch() {
        return notaFiscalSearch;
    }

    public void setNotaFiscalSearch(NotaFiscalSearchDTO notaFiscalSearch) {
        this.notaFiscalSearch = notaFiscalSearch;
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteBarraProgresso assistente) {
        this.assistente = assistente;
    }

    public Boolean getFinalizado() {
        return finalizado;
    }

    public void setFinalizado(Boolean finalizado) {
        this.finalizado = finalizado;
    }

    @Override
    @URLAction(mappingId = "novo-lote-cancelamento-nfse", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataCancelamento(new Date());
        selecionado.setFiscalResponsavel(facade.getUsuarioCorrente());
    }

    @Override
    @URLAction(mappingId = "ver-lote-cancelamento-nfse", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        selecionado.setNotasFiscais(facade.buscarNotasFiscaisLote(selecionado));
    }

    public List<SelectItem> getTiposDocumento() {
        return Util.getListSelectItem(CancelamentoDeclaracaoPrestacaoServico.TipoDocumento.values());
    }

    public List<SelectItem> getMotivos() {
        return Util.getListSelectItem(MotivoCancelamentoNota.values());
    }

    public void validarFiltrosNotaFiscal() {
        ValidacaoException ve = new ValidacaoException();
        if (cadastroEconomico == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Prestador deve ser informado.");
        }
        if (numero == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número deve ser informado.");
        }
        ve.lancarException();
    }

    public void buscarNotaFiscal() {
        try {
            validarFiltrosNotaFiscal();
            notaFiscalSearch = facade.buscarNotaFiscalOrServicoDeclarado(selecionado.getTipoDocumento(), cadastroEconomico, numero);
            if (notaFiscalSearch == null) {
                FacesUtil.addAtencao("NFS-e não encontrada.");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public Boolean podeVerNotaFiscal() {
        return notaFiscalSearch != null &&
            TipoDocumentoNfse.NFSE.name().equals(notaFiscalSearch.getTipoDocumentoNfse());
    }

    public void verNotaFiscal() {
        FacesUtil.redirecionamentoInterno("/nfse/nfse/ver/" + notaFiscalSearch.getId() + "/");
    }

    public void imprimirNotaFiscal() {
        try {
            byte[] bytes = facade.gerarImpressaoNotaFiscalSistemaNfse(notaFiscalSearch.getId());
            AbstractReport report = AbstractReport.getAbstractReport();
            report.setGeraNoDialog(true);
            report.escreveNoResponse("NotaFiscalEletronica.jasper", bytes);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void validarAdicaoNotaFiscal() {
        ValidacaoException ve = new ValidacaoException();
        if (notaFiscalSearch == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Por favor selecione uma Nfs-e");
        } else {
            if (!SituacaoDeclaracaoNfseDTO.EMITIDA.equals(notaFiscalSearch.getSituacaoEnum())) {
                ve.adicionarMensagemDeCampoObrigatorio("A NFS-e não pode ser cancelada. Situação da NFS-e " +
                    notaFiscalSearch.getSituacaoEnum().getDescricao());
            }
        }
        ve.lancarException();
    }

    public void limparFiltrosNfse() {
        notaFiscalSearch = null;
        numero = null;
    }

    public void adicionarNotaFiscal() {
        try {
            validarAdicaoNotaFiscal();
            if (selecionado.getNotasFiscais() == null) {
                selecionado.setNotasFiscais(new ArrayList<NotaFiscalSearchDTO>());
            }
            selecionado.getNotasFiscais().add(notaFiscalSearch);
            limparFiltrosNfse();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void removerNotaFiscal(NotaFiscalSearchDTO notaFiscalSearch) {
        selecionado.getNotasFiscais().remove(notaFiscalSearch);
    }

    public boolean temNotaFiscal() {
        return selecionado.getNotasFiscais() != null && !selecionado.getNotasFiscais().isEmpty();
    }

    public void mudouTipoDocumento() {
        selecionado.setNotasFiscais(new ArrayList<NotaFiscalSearchDTO>());
        limparFiltrosNfse();
    }

    public void iniciarCancelamento() {
        try {
            selecionado.realizarValidacoes();
            finalizado = Boolean.FALSE;
            assistente = new AssistenteBarraProgresso();
            future = facade.efetivar(assistente, selecionado);
            FacesUtil.executaJavaScript("iniciarCancelamento()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void acompanharCancelamento() throws ExecutionException, InterruptedException {
        if (future.isDone() || future.isCancelled()) {
            selecionado = future.get();
            finalizado = Boolean.TRUE;
            FacesUtil.executaJavaScript("finalizarCancelamento()");
        }
    }

    public void irParaVisualizacao() {
        redireciona(String.format("%sver/%s", getCaminhoPadrao(), selecionado.getId()));
    }

    @Override
    public RevisaoAuditoria getUltimaRevisao() {
        if (ultimaRevisao == null) {
            ultimaRevisao = buscarUltimaAuditoria();
            if (!selecionado.getNotasFiscais().isEmpty()) {
                for (NotaFiscalSearchDTO notaFiscal : selecionado.getNotasFiscais()) {
                    RevisaoAuditoria revisaoAuditoria = buscarUltimaAuditoria(NotaFiscal.class, notaFiscal.getId());
                    if (ultimaRevisao == null || (revisaoAuditoria != null && revisaoAuditoria.getDataHora().after(ultimaRevisao.getDataHora()))) {
                        ultimaRevisao = revisaoAuditoria;
                    }
                }
            }
        }
        return ultimaRevisao;
    }

}
