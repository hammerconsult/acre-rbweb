/*
 * Codigo gerado automaticamente em Thu Apr 05 11:31:08 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ClausulaBenificiario;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ClausulaBenificiarioFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "clausulaBenificiarioControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-clausula-benificiario",   pattern = "/clausula-benificiaria/novo/",                                         viewId = "/faces/financeiro/convenios/despesa/clausulabenefeciario/edita.xhtml"),
        @URLMapping(id = "editar-clausula-benificiario", pattern = "/clausula-benificiaria/editar/#{clausulaBenificiarioControlador.id}/", viewId = "/faces/financeiro/convenios/despesa/clausulabenefeciario/edita.xhtml"),
        @URLMapping(id = "ver-clausula-benificiario",    pattern = "/clausula-benificiaria/ver/#{clausulaBenificiarioControlador.id}/",    viewId = "/faces/financeiro/convenios/despesa/clausulabenefeciario/visualizar.xhtml"),
        @URLMapping(id = "listar-clausula-benificiario", pattern = "/clausula-benificiaria/listar/",                                       viewId = "/faces/financeiro/convenios/despesa/clausulabenefeciario/lista.xhtml")
})
public class ClausulaBenificiarioControlador extends PrettyControlador<ClausulaBenificiario> implements Serializable, CRUD {

    @EJB
    private ClausulaBenificiarioFacade clausulaBenificiarioFacade;

    public ClausulaBenificiarioControlador() {
        super(ClausulaBenificiario.class);
    }

    public ClausulaBenificiarioFacade getFacade() {
        return clausulaBenificiarioFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return clausulaBenificiarioFacade;
    }


    @URLAction(mappingId = "novo-clausula-benificiario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-clausula-benificiario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-clausula-benificiario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/clausula-benificiaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
