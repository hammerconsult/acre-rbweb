package br.com.webpublico.controle;

import br.com.webpublico.controle.contabil.reprocessamento.AbstractReprocessamentoHistoricoControlador;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.enums.TipoReprocessamentoHistorico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ReprocessamentoSaldoCreditoReceberFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by Mateus on 07/05/2015.
 */
@ManagedBean(name = "reprocessamentoSaldoCreditoReceber")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "reprocessamento-saldo-credito-receber", pattern = "/reprocessamento-saldo-credito-receber/novo/", viewId = "/faces/financeiro/orcamentario/reprocessamentosaldocreditoreceber/edita.xhtml"),
    @URLMapping(id = "listar-reprocessamento-saldo-credito-receber", pattern = "/reprocessamento-saldo-credito-receber/listar/", viewId = "/faces/financeiro/orcamentario/reprocessamentosaldocreditoreceber/lista.xhtml"),
    @URLMapping(id = "ver-reprocessamento-saldo-credito-receber", pattern = "/reprocessamento-saldo-credito-receber/ver/#{reprocessamentoSaldoCreditoReceber.id}/", viewId = "/faces/financeiro/orcamentario/reprocessamentosaldocreditoreceber/visualizar.xhtml")
})
public class ReprocessamentoSaldoCreditoReceberControlador extends AbstractReprocessamentoHistoricoControlador implements Serializable {
    @EJB
    private ReprocessamentoSaldoCreditoReceberFacade facade;

    public ReprocessamentoSaldoCreditoReceberControlador() {
    }

    @URLAction(mappingId = "reprocessamento-saldo-credito-receber", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        assistente = new AssistenteReprocessamento();
    }

    @URLAction(mappingId = "ver-reprocessamento-saldo-credito-receber", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.recuperarObjeto();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/reprocessamento-saldo-credito-receber/";
    }

    @Override
    protected TipoReprocessamentoHistorico getTipo() {
        return TipoReprocessamentoHistorico.SALDO_CREDITO_RECEBER;
    }

    public void reprocessar() {
        try {
            inicializarAssistente();
            future = facade.reprocessar(assistente);
            FacesUtil.executaJavaScript("dialogProgressBar.show()");
            FacesUtil.executaJavaScript("poll.start()");
            FacesUtil.atualizarComponente("formDialogProgressBar");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (Exception ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addErrorGenerico(ex);
        }
    }
}
