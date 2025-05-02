/*
 * Codigo gerado automaticamente em Tue Jan 03 15:23:30 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.OcorrenciaSEFIP;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.OcorrenciaSEFIPFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "ocorrenciaSEFIPControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoOcorrenciaSEFIP", pattern = "/rh/ocorrencia-da-sefip/novo/", viewId = "/faces/rh/administracaodepagamento/ocorrenciasefip/edita.xhtml"),
        @URLMapping(id = "listaOcorrenciaSEFIP", pattern = "/rh/ocorrencia-da-sefip/listar/", viewId = "/faces/rh/administracaodepagamento/ocorrenciasefip/lista.xhtml"),
        @URLMapping(id = "verOcorrenciaSEFIP", pattern = "/rh/ocorrencia-da-sefip/ver/#{ocorrenciaSEFIPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/ocorrenciasefip/visualizar.xhtml"),
        @URLMapping(id = "editarOcorrenciaSEFIP", pattern = "/rh/ocorrencia-da-sefip/editar/#{ocorrenciaSEFIPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/ocorrenciasefip/edita.xhtml"),
})
public class OcorrenciaSEFIPControlador extends PrettyControlador<OcorrenciaSEFIP> implements Serializable, CRUD {

    @EJB
    private OcorrenciaSEFIPFacade ocorrenciaSEFIPFacade;

    public OcorrenciaSEFIPControlador() {
        super(OcorrenciaSEFIP.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return ocorrenciaSEFIPFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/ocorrencia-da-sefip/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoOcorrenciaSEFIP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verOcorrenciaSEFIP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarOcorrenciaSEFIP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }
}
