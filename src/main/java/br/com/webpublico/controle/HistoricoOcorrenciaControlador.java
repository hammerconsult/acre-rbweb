/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Ocorrencia;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.HistoricoOcorrenciaFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "historicoOcorrenciaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "listarHistoricoOcorrencia", pattern = "/historico-de-ocorrencia/listar/", viewId = "/faces/tributario/historicoocorrencia/lista.xhtml"),
        @URLMapping(id = "verHistoricoOcorrencia", pattern = "/historico-de-ocorrencia/ver/#{historicoOcorrenciaControlador.id}/", viewId = "/faces/tributario/historicoocorrencia/visualizar.xhtml")
})
public class HistoricoOcorrenciaControlador extends PrettyControlador<Ocorrencia> implements Serializable, CRUD {

    @EJB
    private HistoricoOcorrenciaFacade historicoOcorrenciaFacade;

    @Override
    public AbstractFacade getFacede() {
        return historicoOcorrenciaFacade;
    }

    public HistoricoOcorrenciaControlador() {
        super(Ocorrencia.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/historico-de-ocorrencia/";
    }

    @Override
    public Object getUrlKeyValue() {
        return historicoOcorrenciaFacade;
    }

    @URLAction(mappingId = "verHistoricoOcorrencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }
}
