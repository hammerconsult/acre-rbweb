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
    @URLMapping(id = "novo-aditivo-ata", pattern = "/aditivo-ata-registro-preco/novo/", viewId = "/faces/administrativo/licitacao/alteracao-contratual-ata/edita.xhtml"),
    @URLMapping(id = "ver-aditivo-ata", pattern = "/aditivo-ata-registro-preco/ver/#{alteracaoContratualAtaControlador.id}/", viewId = "/faces/administrativo/licitacao/alteracao-contratual-ata/visualizar.xhtml"),
    @URLMapping(id = "editar-aditivo-ata", pattern = "/aditivo-ata-registro-preco/editar/#{alteracaoContratualAtaControlador.id}/", viewId = "/faces/administrativo/licitacao/alteracao-contratual-ata/edita.xhtml"),
    @URLMapping(id = "listar-aditivo-ata", pattern = "/aditivo-ata-registro-preco/listar/", viewId = "/faces/administrativo/licitacao/alteracao-contratual-ata/lista-aditivo.xhtml"),

    @URLMapping(id = "novo-apostilamento-ata", pattern = "/apostilamento-ata-registro-preco/novo/", viewId = "/faces/administrativo/licitacao/alteracao-contratual-ata/edita.xhtml"),
    @URLMapping(id = "ver-apostilamento-ata", pattern = "/apostilamento-ata-registro-preco/ver/#{alteracaoContratualAtaControlador.id}/", viewId = "/faces/administrativo/licitacao/alteracao-contratual-ata/visualizar.xhtml"),
    @URLMapping(id = "editar-apostilamento-ata", pattern = "/apostilamento-ata-registro-preco/editar/#{alteracaoContratualAtaControlador.id}/", viewId = "/faces/administrativo/licitacao/alteracao-contratual-ata/edita.xhtml"),
    @URLMapping(id = "listar-apostilamento-ata", pattern = "/apostilamento-ata-registro-preco/listar/", viewId = "/faces/administrativo/licitacao/alteracao-contratual-ata/lista-apostilamento.xhtml")
})
public class AlteracaoContratualAtaControlador extends AlteracaoContratualControlador {

    @URLAction(mappingId = "novo-aditivo-ata", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoAditivoAta() {
        super.novo();
        selecionado.setTipoCadastro(TipoCadastroAlteracaoContratual.ATA_REGISTRO_PRECO);
        selecionado.setTipoAlteracaoContratual(TipoAlteracaoContratual.ADITIVO);
    }

    @URLAction(mappingId = "novo-apostilamento-ata", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoApostilamentoAta() {
        super.novo();
        selecionado.setTipoCadastro(TipoCadastroAlteracaoContratual.ATA_REGISTRO_PRECO);
        selecionado.setTipoAlteracaoContratual(TipoAlteracaoContratual.APOSTILAMENTO);
    }

    @URLAction(mappingId = "editar-aditivo-ata", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarAditivoAta() {
        editar();
    }

    @URLAction(mappingId = "editar-apostilamento-ata", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarApostilamentoAta() {
        editar();
    }

    @URLAction(mappingId = "ver-aditivo-ata", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verAditivoAta() {
        visualizar();
    }

    @URLAction(mappingId = "ver-apostilamento-ata", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verApostilamentoAta() {
        visualizar();
    }

    @Override
    public String getCaminhoPadrao() {
        if (selecionado.getTipoAlteracaoContratual().isAditivo()) {
            return "/aditivo-ata-registro-preco/";
        }
        return "/apostilamento-ata-registro-preco/";
    }

    public String getCaminhoListar() {
        if (selecionado.getTipoAlteracaoContratual().isAditivo()) {
            return "lista-aditivo";
        }
        return "lista-apostilamento";
    }
}
