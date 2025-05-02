/*
 * Codigo gerado automaticamente em Thu Nov 10 15:02:00 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.FormulaPadraoFP;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FormulaPadraoFPFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "formulaPadraoFPControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoFormulaFP", pattern = "/rh/formula-padrao-da-folha-de-pagamento/novo/", viewId = "/faces/rh/administracaodepagamento/formulapadraofp/edita.xhtml"),
        @URLMapping(id = "editarFormulaFP", pattern = "/rh/formula-padrao-da-folha-de-pagamento/editar/#{formulaPadraoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/formulapadraofp/edita.xhtml"),
        @URLMapping(id = "listarFormulaFP", pattern = "/rh/formula-padrao-da-folha-de-pagamento/listar/", viewId = "/faces/rh/administracaodepagamento/formulapadraofp/lista.xhtml"),
        @URLMapping(id = "verFormulaFP", pattern = "/rh/formula-padrao-da-folha-de-pagamento/ver/#{formulaPadraoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/formulapadraofp/visualizar.xhtml")
})
public class FormulaPadraoFPControlador extends PrettyControlador<FormulaPadraoFP> implements Serializable, CRUD {

    @EJB
    private FormulaPadraoFPFacade formulaPadraoFPFacade;

    public FormulaPadraoFPControlador() {
        super(FormulaPadraoFP.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return formulaPadraoFPFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/formula-padrao-da-folha-de-pagamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "verFormulaFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarFormulaFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "novoFormulaFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }
}
