/*
 * Codigo gerado automaticamente em Mon Apr 09 21:04:03 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.AcoesBeneficiario;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AcoesBeneficiarioFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "acoesBeneficiarioControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-acoes-beneficiarias", pattern = "/acoes-beneficiarias/novo/", viewId = "/faces/financeiro/convenios/despesa/acoes/edita.xhtml"),
        @URLMapping(id = "editar-acoes-beneficiarias", pattern = "/acoes-beneficiarias/editar/#{acoesBeneficiarioControlador.id}/", viewId = "/faces/financeiro/convenios/despesa/acoes/edita.xhtml"),
        @URLMapping(id = "ver-acoes-beneficiarias", pattern = "/acoes-beneficiarias/ver/#{acoesBeneficiarioControlador.id}/", viewId = "/faces/financeiro/convenios/despesa/acoes/visualizar.xhtml"),
        @URLMapping(id = "listar-acoes-beneficiarias", pattern = "/acoes-beneficiarias/listar/", viewId = "/faces/financeiro/convenios/despesa/acoes/lista.xhtml")
})
public class AcoesBeneficiarioControlador extends PrettyControlador<AcoesBeneficiario> implements Serializable, CRUD {

    @EJB
    private AcoesBeneficiarioFacade acoesBeneficiarioFacade;

    public AcoesBeneficiarioControlador() {
        super(AcoesBeneficiario.class);
    }

    public AcoesBeneficiarioFacade getFacade() {
        return acoesBeneficiarioFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return acoesBeneficiarioFacade;
    }

    @URLAction(mappingId = "novo-acoes-beneficiarias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }


    @URLAction(mappingId = "ver-acoes-beneficiarias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-acoes-beneficiarias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/acoes-beneficiarias/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
