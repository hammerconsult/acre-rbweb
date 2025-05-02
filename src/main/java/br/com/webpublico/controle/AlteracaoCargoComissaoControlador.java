package br.com.webpublico.controle;

import br.com.webpublico.enums.TipoPCS;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by Buzatto on 30/07/2015.
 */
@ManagedBean
@ViewScoped

@URLMappings(mappings = {
    @URLMapping(id = "nova-alteracao-cargo-comissao", pattern = "/alteracao-cargo-comissao/novo/", viewId = "/faces/rh/administracaodepagamento/alteracaocargocomissao/edita.xhtml"),
    @URLMapping(id = "editar-alteracao-cargo-comissao", pattern = "/alteracao-cargo-comissao/editar/#{alteracaoCargoComissaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/alteracaocargocomissao/edita.xhtml"),
    @URLMapping(id = "ver-alteracao-cargo-comissao", pattern = "/alteracao-cargo-comissao/ver/#{alteracaoCargoComissaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/alteracaocargocomissao/visualizar.xhtml"),
    @URLMapping(id = "listar-alteracao-cargo-comissao", pattern = "/alteracao-cargo-comissao/listar/", viewId = "/faces/rh/administracaodepagamento/alteracaocargocomissao/lista.xhtml")
})
public class AlteracaoCargoComissaoControlador extends AlteracaoCargoControlador {

    @Override
    public String getCaminhoPadrao() {
        return "/alteracao-cargo-comissao/";
    }

    @Override
    @URLAction(mappingId = "nova-alteracao-cargo-comissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.getProvimentoFP().setTipoProvimento(this.getAlteracaoCargoFacade().getTipoProvimentoFacade().recuperaTipoProvimentoPorCodigo(15));
    }

    @Override
    @URLAction(mappingId = "editar-alteracao-cargo-comissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "ver-alteracao-cargo-comissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public void atribuirTipoPCS(TipoPCS tipoPCS) {
        setTipoPCS(tipoPCS);
    }
}
