package br.com.webpublico.controle;

import br.com.webpublico.entidades.AgendamentoLaudoMedicoSaud;
import br.com.webpublico.entidades.UsuarioSaud;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AgendamentoLaudoMedicoSaudFacade;
import br.com.webpublico.util.DataUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author Pedro
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarAgendamentoPericiaMedicSaud",
        pattern = "/rbtrans/saud/agendamento-pericia-medica-saud/listar/",
        viewId = "/faces/tributario/saud/agendamentopericiamedicasaud/lista.xhtml"),
    @URLMapping(id = "novoAgendamentoPericiaMedicSaud",
        pattern = "/rbtrans/saud/agendamento-pericia-medica-saud/novo/",
        viewId = "/faces/tributario/saud/agendamentopericiamedicasaud/edita.xhtml"),
    @URLMapping(id = "editarAgendamentoPericiaMedicSaud",
        pattern = "/rbtrans/saud/agendamento-pericia-medica-saud/editar/#{agendamentoLaudoMedicoSaudControlador.id}/",
        viewId = "/faces/tributario/saud/agendamentopericiamedicasaud/edita.xhtml"),
    @URLMapping(id = "verAgendamentoPericiaMedicSaud",
        pattern = "/rbtrans/saud/agendamento-pericia-medica-saud/ver/#{agendamentoLaudoMedicoSaudControlador.id}/",
        viewId = "/faces/tributario/saud/agendamentopericiamedicasaud/visualizar.xhtml")
})
public class AgendamentoLaudoMedicoSaudControlador extends PrettyControlador<AgendamentoLaudoMedicoSaud> implements Serializable, CRUD {

    @EJB
    private AgendamentoLaudoMedicoSaudFacade facade;

    public AgendamentoLaudoMedicoSaudControlador() {
        super(AgendamentoLaudoMedicoSaud.class);
    }

    @Override
    public AgendamentoLaudoMedicoSaudFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rbtrans/saud/agendamento-pericia-medica-saud/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoAgendamentoPericiaMedicSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }


    @URLAction(mappingId = "editarAgendamentoPericiaMedicSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verAgendamentoPericiaMedicSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<UsuarioSaud> completarUsuarioSAUD(String parte) {
        return facade.getUsuarioSaudFacade().buscarFiltrando(parte);
    }

    public String formatarHoraMinuto() {
        return DataUtil.formatarHoraMinuto(selecionado.getHoraExame(), selecionado.getMinutoExame());
    }
}
