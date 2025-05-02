package br.com.webpublico.controle;

import br.com.webpublico.controle.contabil.reprocessamento.AbstractReprocessamentoHistoricoControlador;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.enums.TipoReprocessamentoHistorico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ReprocessamentoSaldoDividaAtivaContabilFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by Edi on 21/05/2015.
 */
@ManagedBean(name = "reprocessamentoSaldoDividaAtivaContabilControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "reprocessamento-saldo-divida-ativa", pattern = "/reprocessamento-saldo-divida-ativa/novo/", viewId = "/faces/financeiro/orcamentario/reprocessamentosaldodividaativa/edita.xhtml"),
    @URLMapping(id = "listar-reprocessamento-saldo-divida-ativa", pattern = "/reprocessamento-saldo-divida-ativa/listar/", viewId = "/faces/financeiro/orcamentario/reprocessamentosaldodividaativa/lista.xhtml"),
    @URLMapping(id = "ver-reprocessamento-saldo-divida-ativa", pattern = "/reprocessamento-saldo-divida-ativa/ver/#{reprocessamentoSaldoDividaAtivaContabilControlador.id}/", viewId = "/faces/financeiro/orcamentario/reprocessamentosaldodividaativa/visualizar.xhtml")
})
public class ReprocessamentoSaldoDividaAtivaContabilControlador extends AbstractReprocessamentoHistoricoControlador implements Serializable {
    @EJB
    private ReprocessamentoSaldoDividaAtivaContabilFacade reprocessamentoSaldoDividaAtivaContabilFacade;

    public ReprocessamentoSaldoDividaAtivaContabilControlador() {
    }

    @Override
    public String getCaminhoPadrao() {
        return "/reprocessamento-saldo-divida-ativa/";
    }

    @Override
    protected TipoReprocessamentoHistorico getTipo() {
        return TipoReprocessamentoHistorico.SALDO_DIVIDA_ATIVA;
    }

    @URLAction(mappingId = "reprocessamento-saldo-divida-ativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        assistente = new AssistenteReprocessamento();
    }

    @URLAction(mappingId = "ver-reprocessamento-saldo-divida-ativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.recuperarObjeto();
    }

    private void validarDataInicialDiaUmDoUm() {
        ValidacaoException ve = new ValidacaoException();
        if (DataUtil.getDataFormatada(assistente.getDataInicial()).startsWith("01/01")) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível reprocessar na data 01/01. Esta data possui lançamentos de Transporte de Saldo.");
        }
        ve.lancarException();
    }

    public void reprocessar() {
        try {
            inicializarAssistente();
            validarDataInicialDiaUmDoUm();
            future = reprocessamentoSaldoDividaAtivaContabilFacade.reprocessar(assistente);
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
