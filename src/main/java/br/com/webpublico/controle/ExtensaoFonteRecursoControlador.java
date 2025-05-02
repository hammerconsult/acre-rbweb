package br.com.webpublico.controle;

import br.com.webpublico.entidades.ExtensaoFonteRecurso;
import br.com.webpublico.enums.TipoContaCorrenteTCE;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExtensaoFonteRecursoFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edi on 19/04/2016.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-extensao-fonte-recurso", pattern = "/extensao-fonte-recurso/novo/", viewId = "/faces/financeiro/orcamentario/extensao-fonte-recurso/edita.xhtml"),
    @URLMapping(id = "editar-extensao-fonte-recurso", pattern = "/extensao-fonte-recurso/editar/#{extensaoFonteRecursoControlador.id}/", viewId = "/faces/financeiro/orcamentario/extensao-fonte-recurso/edita.xhtml"),
    @URLMapping(id = "ver-extensao-fonte-recurso", pattern = "/extensao-fonte-recurso/ver/#{extensaoFonteRecursoControlador.id}/", viewId = "/faces/financeiro/orcamentario/extensao-fonte-recurso/visualizar.xhtml"),
    @URLMapping(id = "listar-extensao-fonte-recurso", pattern = "/extensao-fonte-recurso/listar/", viewId = "/faces/financeiro/orcamentario/extensao-fonte-recurso/lista.xhtml")

})
public class ExtensaoFonteRecursoControlador extends PrettyControlador<ExtensaoFonteRecurso> implements Serializable, CRUD {

    @EJB
    private ExtensaoFonteRecursoFacade extensaoFonteRecursoFacade;

    public ExtensaoFonteRecursoControlador() {
        super(ExtensaoFonteRecurso.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/extensao-fonte-recurso/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return extensaoFonteRecursoFacade;
    }

    @Override
    @URLAction(mappingId = "novo-extensao-fonte-recurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-extensao-fonte-recurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-extensao-fonte-recurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<SelectItem> getTipoContaCorrentesTCE() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoContaCorrenteTCE tipo : TipoContaCorrenteTCE.values()) {
            toReturn.add(new SelectItem(tipo, tipo.toString()));
        }
        return toReturn;
    }
}
