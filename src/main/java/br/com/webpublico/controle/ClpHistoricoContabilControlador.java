/*
 * Codigo gerado automaticamente em Tue May 29 13:56:45 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ClpHistoricoContabil;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ClpHistoricoContabilFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "clpHistoricoContabilControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoclphistoricocontabil", pattern = "/clphistoricocontabil/novo/", viewId = "/faces/financeiro/clp/clphistoricocontabil/edita.xhtml"),
        @URLMapping(id = "editaclphistoricocontabil", pattern = "/clphistoricocontabil/editar/#{clpHistoricoContabilControlador.id}/", viewId = "/faces/financeiro/clp/clphistoricocontabil/edita.xhtml"),
        @URLMapping(id = "listaclphistoricocontabil", pattern = "/clphistoricocontabil/listar/", viewId = "/faces/financeiro/clp/clphistoricocontabil/lista.xhtml"),
        @URLMapping(id = "verclphistoricocontabil", pattern = "/clphistoricocontabil/ver/#{clpHistoricoContabilControlador.id}/", viewId = "/faces/financeiro/clp/clphistoricocontabil/visualizar.xhtml")
})
public class ClpHistoricoContabilControlador extends PrettyControlador<ClpHistoricoContabil> implements Serializable, CRUD {

    @EJB
    private ClpHistoricoContabilFacade clpHistoricoContabilFacade;

    public ClpHistoricoContabilControlador() {
        super(ClpHistoricoContabil.class);
    }

    public ClpHistoricoContabilFacade getFacade() {
        return clpHistoricoContabilFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return clpHistoricoContabilFacade;
    }

    @URLAction(mappingId = "novoclphistoricocontabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editaclphistoricocontabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verclphistoricocontabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/clphistoricocontabil/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
