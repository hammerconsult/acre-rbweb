package br.com.webpublico.controle;

import br.com.webpublico.entidades.ExceptionLog;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExceptionLogFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "exceptionLogControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaExceptionLog", pattern = "/exception-log/novo/", viewId = "/faces/admin/exceptionlog/edita.xhtml"),
    @URLMapping(id = "editarExceptionLog", pattern = "/exception-log/editar/#{exceptionLogControlador.id}/", viewId = "/faces/admin/exceptionlog/edita.xhtml"),
    @URLMapping(id = "listarExceptionLog", pattern = "/exception-log/listar/", viewId = "/faces/admin/exceptionlog/lista.xhtml"),
    @URLMapping(id = "verExceptionLog", pattern = "/exception-log/ver/#{exceptionLogControlador.id}/", viewId = "/faces/admin/exceptionlog/visualizar.xhtml")
})
public class ExceptionLogControlador extends PrettyControlador<ExceptionLog> implements Serializable, CRUD {

    @EJB
    private ExceptionLogFacade exceptionLogFacade;

    public ExceptionLogControlador() {
        super(ExceptionLog.class);
    }

    @URLAction(mappingId = "novaExceptionLog", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verExceptionLog", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarExceptionLog", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/exceptionLog/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return exceptionLogFacade;
    }

}
