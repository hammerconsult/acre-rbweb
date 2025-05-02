/*
 * Codigo gerado automaticamente em Thu Apr 05 09:14:46 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.OcorrenciaConvenioDesp;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.OcorrenciaConvenioDespFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-ocorrencia-convenio-despesa", pattern = "/ocorrencia-convenio-despesa/novo/", viewId = "/faces/financeiro/convenios/despesa/ocorrenciaconveniodesp/edita.xhtml"),
    @URLMapping(id = "editar-ocorrencia-convenio-despesa", pattern = "/ocorrencia-convenio-despesa/editar/#{ocorrenciaConvenioDespControlador.id}/", viewId = "/faces/financeiro/convenios/despesa/ocorrenciaconveniodesp/edita.xhtml"),
    @URLMapping(id = "ver-ocorrencia-convenio-despesa", pattern = "/ocorrencia-convenio-despesa/ver/#{ocorrenciaConvenioDespControlador.id}/", viewId = "/faces/financeiro/convenios/despesa/ocorrenciaconveniodesp/visualizar.xhtml"),
    @URLMapping(id = "listar-ocorrencia-convenio-despesa", pattern = "/ocorrencia-convenio-despesa/listar/", viewId = "/faces/financeiro/convenios/despesa/ocorrenciaconveniodesp/lista.xhtml")
})
public class OcorrenciaConvenioDespControlador extends PrettyControlador<OcorrenciaConvenioDesp> implements Serializable, CRUD {

    @EJB
    private OcorrenciaConvenioDespFacade ocorrenciaConvenioDespFacade;

    public OcorrenciaConvenioDespControlador() {
        super(OcorrenciaConvenioDesp.class);
    }

    public OcorrenciaConvenioDespFacade getFacade() {
        return ocorrenciaConvenioDespFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return ocorrenciaConvenioDespFacade;
    }

    @URLAction(mappingId = "ver-ocorrencia-convenio-despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "novo-ocorrencia-convenio-despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editar-ocorrencia-convenio-despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        super.salvar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/ocorrencia-convenio-despesa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
