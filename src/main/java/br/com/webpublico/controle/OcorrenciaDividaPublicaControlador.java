/*
 * Codigo gerado automaticamente em Mon Mar 26 11:55:39 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.OcorrenciaDividaPublica;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.OcorrenciaDividaPublicaFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="ocorrenciaDividaPublicaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-ocorrenceia-divida-publica", pattern = "/ocorrenceia-divida-publica/novo/", viewId = "/faces/financeiro/orcamentario/dividapublica/ocorrencia/edita.xhtml"),
    @URLMapping(id = "editar-ocorrenceia-divida-publica", pattern = "/ocorrenceia-divida-publica/editar/#{ocorrenciaDividaPublicaControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/ocorrencia/edita.xhtml"),
    @URLMapping(id = "ver-ocorrenceia-divida-publica", pattern = "/ocorrenceia-divida-publica/ver/#{ocorrenciaDividaPublicaControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/ocorrencia/visualizar.xhtml"),
    @URLMapping(id = "listar-ocorrenceia-divida-publica", pattern = "/ocorrenceia-divida-publica/listar/", viewId = "/faces/financeiro/orcamentario/dividapublica/ocorrencia/lista.xhtml")
})
public class OcorrenciaDividaPublicaControlador extends PrettyControlador<OcorrenciaDividaPublica> implements Serializable, CRUD {

    @EJB
    private OcorrenciaDividaPublicaFacade ocorrenciaDividaPublicaFacade;

    public OcorrenciaDividaPublicaControlador() {
        super(OcorrenciaDividaPublica.class);
    }

    public OcorrenciaDividaPublicaFacade getFacade() {
        return ocorrenciaDividaPublicaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return ocorrenciaDividaPublicaFacade;
    }

    @URLAction(mappingId = "novo-ocorrenceia-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-ocorrenceia-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-ocorrenceia-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/ocorrenceia-divida-publica/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
