/*
 * Codigo gerado automaticamente em Fri Mar 11 11:12:58 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.SituacaoTramite;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SituacaoTramiteFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;

@ManagedBean(name = "situacaoTramiteControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoSituacaoTramite", pattern = "/situacaoparecer/novo/", viewId = "/faces/tributario/cadastromunicipal/situacaotramite/edita.xhtml"),
        @URLMapping(id = "editarSituacaoTramite", pattern = "/situacaoparecer/editar/#{situacaoTramiteControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/situacaotramite/edita.xhtml"),
        @URLMapping(id = "listarSituacaoTramite", pattern = "/situacaoparecer/listar/", viewId = "/faces/tributario/cadastromunicipal/situacaotramite/lista.xhtml"),
        @URLMapping(id = "verSituacaoTramite", pattern = "/situacaoparecer/ver/#{situacaoTramiteControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/situacaotramite/visualizar.xhtml")
})
public class SituacaoTramiteControlador extends PrettyControlador<SituacaoTramite> implements Serializable, CRUD {

    @EJB
    private SituacaoTramiteFacade situacaoTramiteFacade;

    public SituacaoTramiteControlador() {
        super(SituacaoTramite.class);
    }

    public SituacaoTramiteFacade getFacade() {
        return situacaoTramiteFacade;
    }

    public void validaEnvio(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        Boolean enviaEmails = selecionado.getEnviaEmail();
        if (enviaEmails == true) {
            String validaEmail = (String) value;
            if (validaEmail.isEmpty()) {
                ((UIInput) component).setValid(false);
                FacesUtil.addFatal("Conteúdo não detectado!", "Deve existir um conteúdo!");
            }
        }
    }

    public void valueChange(ValueChangeEvent ev){
        selecionado.setEnviaEmail((Boolean) ev.getNewValue());
    }

    public void valueChangeParaPrazo(ValueChangeEvent ev){
        selecionado.setEnviaEmail((Boolean) ev.getNewValue());
    }

    @Override
    public AbstractFacade getFacede() {
        return situacaoTramiteFacade;
    }

    @URLAction(mappingId = "novoSituacaoTramite", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verSituacaoTramite", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarSituacaoTramite", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/situacaoparecer/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
