/*
 * Codigo gerado automaticamente em Thu Apr 05 08:46:51 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CategoriaDespesa;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CategoriaDespesaFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "categoriaDespesaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-categoria-despesa", pattern = "/categoria-despesa/novo/", viewId = "/faces/financeiro/convenios/despesa/categoriadespesa/edita.xhtml"),
        @URLMapping(id = "editar-categoria-despesa", pattern = "/categoria-despesa/editar/#{categoriaDespesaControlador.id}/", viewId = "/faces/financeiro/convenios/despesa/categoriadespesa/edita.xhtml"),
        @URLMapping(id = "ver-categoria-despesa", pattern = "/categoria-despesa/ver/#{categoriaDespesaControlador.id}/", viewId = "/faces/financeiro/convenios/despesa/categoriadespesa/visualizar.xhtml"),
        @URLMapping(id = "listar-categoria-despesa", pattern = "/categoria-despesa/listar/", viewId = "/faces/financeiro/convenios/despesa/categoriadespesa/lista.xhtml")
})
public class CategoriaDespesaControlador extends PrettyControlador<CategoriaDespesa> implements Serializable, CRUD {

    @EJB
    private CategoriaDespesaFacade categoriaDespesaFacade;

    public CategoriaDespesaControlador() {
        super(CategoriaDespesa.class);
    }

    public CategoriaDespesaFacade getFacade() {
        return categoriaDespesaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return categoriaDespesaFacade;
    }

    @URLAction(mappingId = "novo-categoria-despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }


    @URLAction(mappingId = "ver-categoria-despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-categoria-despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/categoria-despesa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
