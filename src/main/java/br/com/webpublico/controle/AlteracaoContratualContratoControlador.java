package br.com.webpublico.controle;

import br.com.webpublico.enums.TipoAlteracaoContratual;
import br.com.webpublico.enums.TipoCadastroAlteracaoContratual;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-aditivo-cont", pattern = "/aditivo-contrato/novo/", viewId = "/faces/administrativo/contrato/alteracao-contratual/edita.xhtml"),
    @URLMapping(id = "ver-aditivo-cont", pattern = "/aditivo-contrato/ver/#{alteracaoContratualContratoControlador.id}/", viewId = "/faces/administrativo/contrato/alteracao-contratual/visualizar.xhtml"),
    @URLMapping(id = "editar-aditivo-cont", pattern = "/aditivo-contrato/editar/#{alteracaoContratualContratoControlador.id}/", viewId = "/faces/administrativo/contrato/alteracao-contratual/edita.xhtml"),
    @URLMapping(id = "listar-aditivo-cont", pattern = "/aditivo-contrato/listar/", viewId = "/faces/administrativo/contrato/alteracao-contratual/lista-aditivo.xhtml"),

    @URLMapping(id = "novo-apostilamento-cont", pattern = "/apostilamento-contrato/novo/", viewId = "/faces/administrativo/contrato/alteracao-contratual/edita.xhtml"),
    @URLMapping(id = "ver-apostilamento-cont", pattern = "/apostilamento-contrato/ver/#{alteracaoContratualContratoControlador.id}/", viewId = "/faces/administrativo/contrato/alteracao-contratual/visualizar.xhtml"),
    @URLMapping(id = "editar-apostilamento-cont", pattern = "/apostilamento-contrato/editar/#{alteracaoContratualContratoControlador.id}/", viewId = "/faces/administrativo/contrato/alteracao-contratual/edita.xhtml"),
    @URLMapping(id = "listar-apostilamento-cont", pattern = "/apostilamento-contrato/listar/", viewId = "/faces/administrativo/contrato/alteracao-contratual/lista-apostilamento.xhtml")
})
public class AlteracaoContratualContratoControlador extends AlteracaoContratualControlador {

    @URLAction(mappingId = "novo-aditivo-cont", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoAditivoCont() {
        super.novo();
        selecionado.setTipoCadastro(TipoCadastroAlteracaoContratual.CONTRATO);
        selecionado.setTipoAlteracaoContratual(TipoAlteracaoContratual.ADITIVO);
    }

    @URLAction(mappingId = "novo-apostilamento-cont", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoApostilamentoCont() {
        super.novo();
        selecionado.setTipoCadastro(TipoCadastroAlteracaoContratual.CONTRATO);
        selecionado.setTipoAlteracaoContratual(TipoAlteracaoContratual.APOSTILAMENTO);
    }

    @URLAction(mappingId = "editar-aditivo-cont", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarAditivoCont() {
        editar();
    }

    @URLAction(mappingId = "editar-apostilamento-cont", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarApostilamentoCont() {
        editar();
    }

    @URLAction(mappingId = "ver-aditivo-cont", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verAditivoCont() {
        visualizar();
    }

    @URLAction(mappingId = "ver-apostilamento-cont", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verApostilamentoCont() {
        visualizar();
    }

    @Override
    public String getCaminhoPadrao() {
        if (selecionado.getTipoAlteracaoContratual().isAditivo()) {
            return "/aditivo-contrato/";
        }
        return "/apostilamento-contrato/";
    }

    public String getCaminhoListar() {
        if (selecionado.getTipoAlteracaoContratual().isAditivo()) {
            return "lista-aditivo";
        }
        return "lista-apostilamento";
    }
}
