package br.com.webpublico.controle;

import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.ExecucaoContrato;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamentoContrato;
import br.com.webpublico.entidadesauxiliares.ContratoCorrecaoVO;
import br.com.webpublico.entidadesauxiliares.ExecucaoContratoCorrecaoOrigemVO;
import br.com.webpublico.entidadesauxiliares.ExecucaoContratoCorrecaoVO;
import br.com.webpublico.enums.SituacaoContrato;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ReprocessamentoSaldoContratoFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-reprocessamento-saldo-contrato", pattern = "/reprocessamento-saldo-contrato/", viewId = "/faces/administrativo/contrato/reprocessamento-contrato/edita.xhtml")
})
public class ReprocessamentoSaldoContratoControlador implements Serializable {

    @EJB
    private ReprocessamentoSaldoContratoFacade facade;
    private Future<AssistenteReprocessamentoContrato> future;
    private AssistenteReprocessamentoContrato assistente;

    public ReprocessamentoSaldoContratoControlador() {
    }

    @URLAction(mappingId = "novo-reprocessamento-saldo-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        assistente = new AssistenteReprocessamentoContrato();
        assistente.setAssistenteBarraProgresso(new AssistenteBarraProgresso());
        assistente.setDataFinal(new Date());
        assistente.setDataInicial(new Date());
        assistente.setDataOperacao(new Date());
    }

    public void reprocessar() {
        try {
            validarReprocessamento();
            future = facade.reprocessar(assistente);
            FacesUtil.executaJavaScript("dialogProgressBar.show()");
            FacesUtil.executaJavaScript("poll.start()");
            FacesUtil.atualizarComponente("formDialogProgressBar");
        } catch (ValidacaoException ve) {
            future = null;
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.executaJavaScript("poll.stop()");
        } catch (ExcecaoNegocioGenerica e) {
            future = null;
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.executaJavaScript("poll.stop()");
        } catch (Exception ex) {
            future = null;
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.executaJavaScript("poll.stop()");
            FacesUtil.addErrorGenerico(ex);
        }
    }

    public void buscarExecucaoContratoCorrecaoOrigem() {
        try {

            assistente = facade.buscarExecucoesContratoCorrecaoOrigem(assistente);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void separarOrigemExecucaoCorreta(ContratoCorrecaoVO contVO) {
        try {
            facade.separarOrigemExecucaoCorreta(contVO);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void aplicarCorrecaoOrigemExecucao(ContratoCorrecaoVO contVO) {
        try {
            ExecucaoContratoCorrecaoOrigemVO origemVO = contVO.getOrigensExecucao().stream().filter(ExecucaoContratoCorrecaoOrigemVO::getSelecionada).findFirst().orElse(null);
            if (origemVO == null || contVO.getExecucoesCorrecao().stream().noneMatch(ExecucaoContratoCorrecaoVO::getSelecionada)) {
                FacesUtil.addOperacaoNaoPermitida("Selecione uma origem para continuar.");
                return;
            }
            for (ExecucaoContratoCorrecaoVO execVO : contVO.getExecucoesCorrecao()) {
                if (execVO.getSelecionada()) {
                    ExecucaoContrato execucao = execVO.getExecucaoContrato();
                    execucao.setOrigem(origemVO.getOrigem());
                    execucao.setIdOrigem(origemVO.getIdOrigem());
                    execucao.setOperacaoOrigem(origemVO.getOperacaoOrigem());
                    execucao.setReprocessada(true);

                    ExecucaoContratoCorrecaoVO execCorrigida = new ExecucaoContratoCorrecaoVO();
                    execCorrigida.setOrigemRelacionadaVO(origemVO);
                    execCorrigida.setExecucaoContrato(execucao);
                    contVO.getExecucoesCorrigidas().add(execCorrigida);
                }
            }
            contVO.getExecucoesCorrecao().removeIf(ExecucaoContratoCorrecaoVO::getSelecionada);
            contVO.getOrigensExecucao().removeIf(ExecucaoContratoCorrecaoOrigemVO::getSelecionada);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void marcarExecucaoComoReprocessada(ContratoCorrecaoVO contVO) {
        try {
            contVO = facade.marcarExecucaoComoReprocessada(contVO);
            assistente.getContratosCorrecoes().remove(contVO);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarReprocessamento() {
        ValidacaoException ve = new ValidacaoException();
        if (assistente.getContrato() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo contrato deve ser informado.");
        }
        ve.lancarException();
    }

    public void consultarProcesso() {
        if (future != null && future.isDone()) {
            assistente.getAssistenteBarraProgresso().setExecutando(Boolean.FALSE);
            FacesUtil.executaJavaScript("poll.stop()");
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.executaJavaScript("dialogProgressBar.hide()");
            FacesUtil.addOperacaoRealizada("Reprocessamento realizado com sucesso.");
        }
    }

    public List<Contrato> completarContrato(String parte) {
        if (assistente.getExecucaoReprocessada()) {
            return facade.getContratoFacade().buscarContratoComExecucaoReprocessada(parte.trim());
        }
        return facade.getContratoFacade().buscarFiltrandoPorSituacao(parte.trim(), SituacaoContrato.situacoesDiferenteEmElaboracao);
    }

    public Future<AssistenteReprocessamentoContrato> getFuture() {
        return future;
    }

    public void setFuture(Future<AssistenteReprocessamentoContrato> future) {
        this.future = future;
    }

    public AssistenteReprocessamentoContrato getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteReprocessamentoContrato assistente) {
        this.assistente = assistente;
    }
}
