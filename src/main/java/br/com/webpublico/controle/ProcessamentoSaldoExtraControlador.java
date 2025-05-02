package br.com.webpublico.controle;

import br.com.webpublico.controle.contabil.reprocessamento.AbstractReprocessamentoHistoricoControlador;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.entidadesauxiliares.contabil.ReprocessamentoSaldoExtraOrcamentario;
import br.com.webpublico.enums.TipoReprocessamentoHistorico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoSaldoExtraOrcamentarioFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 20/01/15
 * Time: 09:58
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "processamento-saldo-extra", pattern = "/processamento-saldo-extra/novo/", viewId = "/faces/financeiro/orcamentario/processamentosaldoextra/processamento-saldo-extra.xhtml"),
    @URLMapping(id = "listar-processamento-saldo-extra", pattern = "/processamento-saldo-extra/listar/", viewId = "/faces/financeiro/orcamentario/processamentosaldoextra/lista.xhtml"),
    @URLMapping(id = "ver-processamento-saldo-extra", pattern = "/processamento-saldo-extra/ver/#{processamentoSaldoExtraControlador.id}/", viewId = "/faces/financeiro/orcamentario/processamentosaldoextra/visualizar.xhtml")
})
public class ProcessamentoSaldoExtraControlador extends AbstractReprocessamentoHistoricoControlador implements Serializable {

    @EJB
    private ReprocessamentoSaldoExtraOrcamentarioFacade facade;

    public ProcessamentoSaldoExtraControlador() {
    }

    @URLAction(mappingId = "processamento-saldo-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        assistente = new AssistenteReprocessamento();
        assistente.setReprocessamentoSaldoExtraOrcamentario(new ReprocessamentoSaldoExtraOrcamentario());
    }

    @URLAction(mappingId = "ver-processamento-saldo-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.recuperarObjeto();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/processamento-saldo-extra/";
    }

    @Override
    protected TipoReprocessamentoHistorico getTipo() {
        return TipoReprocessamentoHistorico.SALDO_EXTRAORCAMENTARIO;
    }

    public void reprocessar() {
        try {
            validarReprocessamentoSaldoInicial();
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

    private void validarReprocessamentoSaldoInicial() {
        ValidacaoException ve = new ValidacaoException();
        if (assistente.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo data inicial é obrigatório.");
        }
        if (assistente.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Campo data final é obrigatório.");
        }
        ve.lancarException();
        if (assistente.getDataFinal().before(assistente.getDataInicial())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser maior que a Data Inicial.");
        }
        ve.lancarException();
    }
}
