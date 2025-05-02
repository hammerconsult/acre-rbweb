package br.com.webpublico.controle;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.negocios.AcessoSubsidioFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "alteracaoAcessoSubsidioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-alteracao-acesso-subsidio", pattern = "/alteracao-acesso-subsidio/novo/", viewId = "/faces/rh/administracaodepagamento/alteracaoacessosubsidio/edita.xhtml"),
    @URLMapping(id = "editar-alteracao-acesso-subsidio", pattern = "/alteracao-acesso-subsidio/editar/#{alteracaoAcessoSubsidioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/alteracaoacessosubsidio/edita.xhtml"),
    @URLMapping(id = "ver-alteracao-acesso-subsidio", pattern = "/alteracao-acesso-subsidio/ver/#{alteracaoAcessoSubsidioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/alteracaoacessosubsidio/visualizar.xhtml"),
    @URLMapping(id = "listar-alteracao-acesso-subsidio", pattern = "/alteracao-acesso-subsidio/listar/", viewId = "/faces/rh/administracaodepagamento/alteracaoacessosubsidio/lista.xhtml")
})
public class AlteracaoAcessoSubsidioControlador extends AlteracaoCargoControlador {

    @EJB
    private AcessoSubsidioFacade acessoSubsidioFacade;

    @Override
    public String getCaminhoPadrao() {
        return "/alteracao-acesso-subsidio/";
    }

    @Override
    @URLAction(mappingId = "nova-alteracao-acesso-subsidio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.getProvimentoFP().setTipoProvimento(this.getAlteracaoCargoFacade().getTipoProvimentoFacade().recuperaTipoProvimentoPorCodigo(50));
    }

    @Override
    @URLAction(mappingId = "editar-alteracao-acesso-subsidio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "ver-alteracao-acesso-subsidio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public void atribuirTipoPCS(TipoPCS tipoPCS) {
        setTipoPCS(tipoPCS);
    }

    public List<ContratoFP> getContratoVigentePorAcessoSubsidio(String parte) {
        List<Long> codigos = new ArrayList<>();
        codigos.add(Long.valueOf(3));
        return acessoSubsidioFacade.getContratoFPFacade().recuperaContratoVigentePorModalidades(parte.trim(), codigos);
    }

}
