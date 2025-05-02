package br.com.webpublico.controle;

import br.com.webpublico.controle.contabil.reprocessamento.AbstractReprocessamentoHistoricoControlador;
import br.com.webpublico.entidades.ConfiguracaoContabil;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.enums.TipoReprocessamentoHistorico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ReprocessamentoSaldoDividaPublicaFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by Mateus on 02/02/2015.
 */

@ManagedBean(name = "reprocessamentoSaldoDividaPublicaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-reprocessamento-divida-publica", pattern = "/reprocessamento-divida-publica/novo/", viewId = "/faces/financeiro/orcamentario/reprocessamentosaldodividapublica/edita.xhtml"),
    @URLMapping(id = "listar-reprocessamento-divida-publica", pattern = "/reprocessamento-divida-publica/listar/", viewId = "/faces/financeiro/orcamentario/reprocessamentosaldodividapublica/lista.xhtml"),
    @URLMapping(id = "ver-reprocessamento-divida-publica", pattern = "/reprocessamento-divida-publica/ver/#{reprocessamentoSaldoDividaPublicaControlador.id}/", viewId = "/faces/financeiro/orcamentario/reprocessamentosaldodividapublica/visualizar.xhtml")
})
public class ReprocessamentoSaldoDividaPublicaControlador extends AbstractReprocessamentoHistoricoControlador implements Serializable {
    @EJB
    private ReprocessamentoSaldoDividaPublicaFacade facade;

    public ReprocessamentoSaldoDividaPublicaControlador() {
    }

    @Override
    public String getCaminhoPadrao() {
        return "/reprocessamento-divida-publica/";
    }

    @Override
    protected TipoReprocessamentoHistorico getTipo() {
        return TipoReprocessamentoHistorico.SALDO_DIVIDA_PUBLICA;
    }

    @URLAction(mappingId = "novo-reprocessamento-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        assistente = new AssistenteReprocessamento();
    }

    @URLAction(mappingId = "ver-reprocessamento-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.recuperarObjeto();
    }

    private void validarContasAntesDeReprocessar() {
        ValidacaoException ve = new ValidacaoException();
        ConfiguracaoContabil configuracaoContabil = facade.getConfiguracaoContabilFacade().configuracaoContabilVigente();
        if (configuracaoContabil.getContasReceita().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("É necessário informar as Contas de Receita para geração de Saldo da Dívida Pública na Configuração Contábil.");
        }
        if (configuracaoContabil.getContasDespesa().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("É necessário informar as Contas de Despesa para geração de Saldo da Dívida Pública na Configuração Contábil.");
        }
        ve.lancarException();
    }

    public void reprocessar() {
        try {
            validarContasAntesDeReprocessar();
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
