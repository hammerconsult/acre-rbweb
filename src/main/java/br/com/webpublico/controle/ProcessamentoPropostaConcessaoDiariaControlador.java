package br.com.webpublico.controle;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ProcessamentoPropostaConcessaoDiariaFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "processamento-diaria", pattern = "/processamento-diaria/", viewId = "/faces/financeiro/concessaodediarias/processamento/edita.xhtml")
})

public class ProcessamentoPropostaConcessaoDiariaControlador {

    @EJB
    private ProcessamentoPropostaConcessaoDiariaFacade facade;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Date dataInicial;
    private Date dataFinal;
    private Future future;

    @URLAction(mappingId = "processamento-diaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        if (!facade.getSistemaFacade().getUsuarioCorrente().hasRole("ROLE_ADMIN")) {
            FacesUtil.redirecionamentoInterno("/");
        }
        dataInicial = new Date();
        dataFinal = new Date();
    }

    public void acompanharGeracao() {
        if (future != null) {
            if (future.isDone()) {
                if (!assistenteBarraProgresso.getMensagens().isEmpty()) {
                    for (String msg : assistenteBarraProgresso.getMensagens()) {
                        FacesUtil.addOperacaoNaoRealizada(msg);
                    }
                    FacesUtil.executaJavaScript("erroNoProcessamento()");
                } else {
                    FacesUtil.executaJavaScript("finalizarProcessamento()");
                }

            } else if (future.isCancelled()) {
                FacesUtil.executaJavaScript("pararProcessamento()");
            }
        }

    }

    public void finalizarProcessamento() {
        assistenteBarraProgresso.setExecutando(false);
        FacesUtil.addOperacaoRealizada(assistenteBarraProgresso.getTotal() + " registros processados com sucesso.");
    }

    public void recalcularViagens() {
        try {
            validarCampos();
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setExecutando(true);
            assistenteBarraProgresso.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
            assistenteBarraProgresso.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
            future = facade.recalcularDiarias(dataInicial, dataFinal, assistenteBarraProgresso);
            FacesUtil.executaJavaScript("iniciarProcessamento()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (Exception ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro durante a busca das viagens: " + ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial deve ser inferior ou igual à data final.");
        }
        ve.lancarException();
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }
}
