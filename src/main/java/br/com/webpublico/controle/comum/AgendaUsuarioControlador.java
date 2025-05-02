package br.com.webpublico.controle.comum;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.comum.agenda.AgendaUsuario;
import br.com.webpublico.entidades.comum.agenda.CompartilhamentoAgenda;
import br.com.webpublico.entidades.comum.agenda.EventoAgenda;
import br.com.webpublico.enums.Sexo;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.comum.agenda.AgendaUsuarioFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by tributario on 21/08/2015.
 */
@ManagedBean(name = "agendaUsuarioControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(
                id = "listarAgenda",
                pattern = "/agenda/",
                viewId = "/faces/comum/agenda/lista.xhtml")
})
public class AgendaUsuarioControlador extends PrettyControlador<AgendaUsuario> {
    @EJB
    private AgendaUsuarioFacade agendaUsuarioFacade;
    private List<EventoAgenda> eventosDeHoje;
    private List<EventoAgenda> eventosDeAmanha;
    private EventoAgenda eventoAgenda;
    private CompartilhamentoAgenda compartilhamento;
    private UsuarioSistema usuarioSelecionado;

    @Override
    public AbstractFacade getFacede() {
        return agendaUsuarioFacade;
    }

    @URLAction(mappingId = "listarAgenda", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        usuarioSelecionado = getUsuarioLogado();
        selecionado = agendaUsuarioFacade.retornaAgendaDoUsuario(usuarioSelecionado);
        if (selecionado != null) {
            novoEvento();
        }

    }

    public void alteraAgenda(){
        selecionado = agendaUsuarioFacade.retornaAgendaDoUsuario(usuarioSelecionado);
    }

    public List<EventoAgenda> getEventosFuturos(){
        return agendaUsuarioFacade.retornaEventosFuturos(selecionado,getSistemaControlador().getDataOperacao());
    }

    public void criarNovaAgenda() {
        try {
            selecionado = agendaUsuarioFacade.retornaNovaAgenda(getSistemaControlador().getUsuarioCorrente());
            FacesUtil.addOperacaoRealizada(getMensagemDeBoasVindas());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public void novoEvento() {
        eventoAgenda = new EventoAgenda();
        eventoAgenda.setAgendaUsuario(selecionado);
    }

    public void editarEvento(EventoAgenda evento){
        eventoAgenda = evento;
    }

    public void removerEvento(EventoAgenda evt) {
        selecionado.removeEvento(evt);
        selecionado = agendaUsuarioFacade.salvarAgenda(selecionado);
    }

    public boolean validaEvento() {
        boolean valida = true;
        if (eventoAgenda.getAssunto() == null
                || eventoAgenda.getAssunto().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O Assunto e um campo obrigatório.");
            valida = false;
        }
        if (eventoAgenda.getDataHoraInicio() == null) {
            FacesUtil.addCampoObrigatorio("A data de Início é um campo obrigatório.");
            valida = false;
        } else {
            if (eventoAgenda.getDataHoraFim() != null
                    && eventoAgenda.getDataHoraFim().before(eventoAgenda.getDataHoraInicio())) {
                FacesUtil.addOperacaoNaoPermitida("Não é possível inserir uma data de término menor que a data de início.");
                valida = false;
            }
        }

        return valida;
    }

    public void salvarEvento() {
        if (validaEvento()) {
            selecionado.addEvento(eventoAgenda);
            selecionado = agendaUsuarioFacade.salvarAgenda(selecionado);
            FacesUtil.executaJavaScript("dialogNovoEvento.hide()");
        }
    }

    public void novoCompartilhamento() {
        compartilhamento = new CompartilhamentoAgenda();
        compartilhamento.setAgendaUsuario(selecionado);
    }

    public void adicionaCompartilhamento() {
        if (validaCompartilhamento()) {
            selecionado.addCompartilhamento(compartilhamento);
            selecionado = agendaUsuarioFacade.salvarAgenda(selecionado);
            novoCompartilhamento();
        }
    }

    public void removerCompartilhamento(CompartilhamentoAgenda compartilhamento) {
        selecionado.removeCompartilhamento(compartilhamento);
        selecionado = agendaUsuarioFacade.salvarAgenda(selecionado);
    }


    public void salvarAgenda() {
        selecionado = agendaUsuarioFacade.salvarAgenda(selecionado);
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public List<EventoAgenda> getEventosDeHoje() {
        return eventosDeHoje;
    }

    public List<EventoAgenda> getEventosDeAmanha() {
        return eventosDeAmanha;
    }

    public boolean getTemEventoHoje() {
        eventosDeHoje = agendaUsuarioFacade.retornaEventosDoUsuarioPorDia(getSistemaControlador().getUsuarioCorrente(), SistemaFacade.getDataCorrente());
        return !eventosDeHoje.isEmpty();
    }

    public boolean getTemEventoAmanha() {
        Calendar c = Calendar.getInstance();
        c.setTime(SistemaFacade.getDataCorrente());
        c.add(Calendar.DAY_OF_YEAR, 1);
        eventosDeAmanha = agendaUsuarioFacade.retornaEventosDoUsuarioPorDia(getSistemaControlador().getUsuarioCorrente(), c.getTime());
        return !eventosDeAmanha.isEmpty();
    }

    public AgendaUsuario getAgenda() {
        return agendaUsuarioFacade.retornaAgendaDoUsuario(getSistemaControlador().getUsuarioCorrente());
    }

    public UsuarioSistema getUsuarioLogado() {
        return getSistemaControlador().getUsuarioCorrente();
    }

    public String getMensagemDeBoasVindas() {
        if (Sexo.MASCULINO.equals(getSistemaControlador().getUsuarioCorrente().getPessoaFisica().getSexo())) {
            return "Bem-vindo à sua agenda!";
        } else {
            return "Bem-vinda à sua agenda!";
        }
    }

    public List<UsuarioSistema> completaUsuarioSistemaPeloLogin(String parte) {
        return agendaUsuarioFacade.getUsuarioSistemaFacade().completaUsuarioSistemaAtivoPeloLogin(parte.trim());
    }

    public boolean validaCompartilhamento() {
        boolean valida = true;
        if (compartilhamento.getUsuarioSistema() == null) {
            FacesUtil.addCampoObrigatorio("O Usuário é um campo obrigatório.");
            valida = false;
        } else {
            for (CompartilhamentoAgenda obj : selecionado.getCompartilhamentos()) {
                if (obj.getUsuarioSistema().equals(compartilhamento.getUsuarioSistema())) {
                    FacesUtil.addOperacaoNaoPermitida("O Usuário já está na lista de compartilhamentos.");
                    valida = false;
                }
            }
        }

        return valida;
    }

    public List<SelectItem> getUsuariosCompartilhados() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(getSistemaControlador().getUsuarioCorrente(), getSistemaControlador().getUsuarioCorrente().getLogin()));

        for(UsuarioSistema user : agendaUsuarioFacade.retornaUsuariosComAgendasCompartilhadasComUsuario(getSistemaControlador().getUsuarioCorrente())){
            toReturn.add(new SelectItem(user,user.getLogin()));
        }
        return toReturn;
    }


    public EventoAgenda getEventoAgenda() {
        return eventoAgenda;
    }

    public void setEventoAgenda(EventoAgenda eventoAgenda) {
        this.eventoAgenda = eventoAgenda;
    }


    public CompartilhamentoAgenda getCompartilhamento() {
        return compartilhamento;
    }

    public void setCompartilhamento(CompartilhamentoAgenda compartilhamento) {
        this.compartilhamento = compartilhamento;
    }

    public UsuarioSistema getUsuarioSelecionado() {
        return usuarioSelecionado;
    }

    public void setUsuarioSelecionado(UsuarioSistema usuarioSelecionado) {
        this.usuarioSelecionado = usuarioSelecionado;
    }
}
