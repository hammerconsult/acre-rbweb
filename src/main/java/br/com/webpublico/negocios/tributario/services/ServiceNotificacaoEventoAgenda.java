package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.comum.agenda.EventoAgenda;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.EmailService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by tributario on 03/09/2015.
 */
@Service
public class ServiceNotificacaoEventoAgenda {
    @PersistenceContext
    protected transient EntityManager em;

    public Map recuperaEventosAgendaDeAmanha() {

        StringBuilder hql = new StringBuilder();
        hql.append(" select evento from AgendaUsuario agenda ");
        hql.append(" inner join agenda.eventos evento ");
        hql.append(" inner join agenda.proprietario usuario ");
        hql.append(" where day(evento.dataHoraInicio) = day(:data) ");
        hql.append(" and month(evento.dataHoraInicio) = month(:data) ");
        hql.append(" and year(evento.dataHoraInicio) = year(:data) ");
        hql.append(" order by evento.dataHoraInicio asc ");

        Query q = em.createQuery(hql.toString());
        Calendar c = Calendar.getInstance();
        c.setTime(SistemaFacade.getDataCorrente());
        c.add(Calendar.DAY_OF_YEAR, 1);
        q.setParameter("data", c.getTime());

        Map<UsuarioSistema, List<EventoAgenda>> eventosUsuario = new HashMap<>();
        List<EventoAgenda> eventos = q.getResultList();

        for (EventoAgenda obj : eventos) {
            if (!eventosUsuario.containsKey(obj.getAgendaUsuario().getProprietario())) {
                eventosUsuario.put(obj.getAgendaUsuario().getProprietario(), new ArrayList<EventoAgenda>());
            }
            eventosUsuario.get(obj.getAgendaUsuario().getProprietario()).add(obj);
        }

        return eventosUsuario;
    }

    public void enviaEmailNotificacaoEvento() {
        String tituloEmail = "WebPúblico - Compromissos para Amanhã";
        Map eventos = recuperaEventosAgendaDeAmanha();

        for (Object usuario : eventos.keySet()) {

            String emailUsuario = ((UsuarioSistema) usuario).getPessoaFisica().getEmail();

            if (emailUsuario != null
                && !emailUsuario.isEmpty()) {
                EmailService.getInstance().enviarEmail(emailUsuario, tituloEmail,
                    montaConteudoEmail((UsuarioSistema) usuario, (List<EventoAgenda>) eventos.get(usuario)));
            }
        }
    }

    public String montaConteudoEmail(UsuarioSistema usuario, List<EventoAgenda> eventos) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<meta charset='UTF-8'></meta>");
        sb.append("<div id='root' style='width:1000px'>");
        sb.append("<div style ='position: absolute; top: 100;right: 0;left: 0;margin: auto;width:90%; text-align:center;border-style: solid;border-color: #497692;border-width: 5px;'>");
        sb.append("<div style='font-style: italic; font-weight: bold !important; color: #497692;'>");
        sb.append("<h1>Olá " + usuario.getPessoaFisica().getPrimeiroNome() + "</h1>");
        sb.append("WebPúblico informa que você tem os seguintes compromissos para amanhã:");
        sb.append("</div>");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("<center>");
        sb.append("<table style='width:70%'>");
        sb.append("<thead style='text-align:left'>");
        sb.append("<th>#</th>");
        sb.append("<th>Data/Hora de Início</th>");
        sb.append("<th>Assunto</th>");
        sb.append("<th style='width:40%!important'>Observação</th>");
        sb.append("</thead>");
        for (EventoAgenda evento : eventos) {
            sb.append("<tr>");
            sb.append("<td><div style='background-color:#" + evento.getCorEvento() + ";width:10px; height:10px'></div></td>");
            sb.append("<td>" + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(evento.getDataHoraInicio()) + "</td>");
            sb.append("<td>" + evento.getAssunto() + "</td>");
            sb.append("<td>" + evento.getObservacao() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        sb.append("</center>");
        sb.append("<br/>");
        sb.append("<br/>");
        sb.append("</div>");
        sb.append("</div>");
        sb.append("</html>");

        return sb.toString();
    }
}
