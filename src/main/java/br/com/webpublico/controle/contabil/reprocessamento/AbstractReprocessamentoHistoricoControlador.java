package br.com.webpublico.controle.contabil.reprocessamento;

import br.com.webpublico.entidades.ReprocessamentoHistorico;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.enums.TipoReprocessamentoHistorico;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoHistoricoFacade;
import br.com.webpublico.util.FacesUtil;

import javax.ejb.EJB;
import java.util.concurrent.Future;

public abstract class AbstractReprocessamentoHistoricoControlador {
    @EJB
    private ReprocessamentoHistoricoFacade reprocessamentoHistoricoFacade;
    protected Long id;
    protected ReprocessamentoHistorico selecionado;
    protected Future<AssistenteReprocessamento> future;
    protected AssistenteReprocessamento assistente;

    public abstract String getCaminhoPadrao();

    protected abstract TipoReprocessamentoHistorico getTipo();

    public void inicializarAssistente() {
        assistente.validarDatas();
        assistente.setExercicio(reprocessamentoHistoricoFacade.getSistemaFacade().getExercicioCorrente());
        assistente.inicializarHistorico(getTipo(), reprocessamentoHistoricoFacade.getSistemaFacade().getUsuarioCorrente());
    }

    public void gerarLogPDF(boolean isApenasErros) {
        try {
            reprocessamentoHistoricoFacade.geraPDFLog(selecionado, isApenasErros, getTipo());
        } catch (Exception e) {
            FacesUtil.addError("Erro", e.getMessage());
        }
    }

    public void consultarReprocessamento() {
        if (future != null && future.isDone()) {
            assistente.getAssistenteBarraProgresso().setCalculando(Boolean.FALSE);
            FacesUtil.executaJavaScript("poll.stop()");
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.executaJavaScript("dialogProgressBar.hide()");
            FacesUtil.addOperacaoRealizada("Reprocessamento realizado com sucesso.");
        }
    }

    public void cancelar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }

    protected void recuperarObjeto() {
        if (id != null) {
            selecionado = reprocessamentoHistoricoFacade.recuperar(id);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReprocessamentoHistorico getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ReprocessamentoHistorico selecionado) {
        this.selecionado = selecionado;
    }

    public Future<AssistenteReprocessamento> getFuture() {
        return future;
    }

    public void setFuture(Future<AssistenteReprocessamento> future) {
        this.future = future;
    }

    public AssistenteReprocessamento getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteReprocessamento assistente) {
        this.assistente = assistente;
    }
}
