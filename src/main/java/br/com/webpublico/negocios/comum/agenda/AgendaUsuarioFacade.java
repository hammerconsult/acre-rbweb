package br.com.webpublico.negocios.comum.agenda;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.comum.agenda.AgendaUsuario;
import br.com.webpublico.entidades.comum.agenda.EventoAgenda;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by tributario on 21/08/2015.
 */
@Stateless
public class AgendaUsuarioFacade extends AbstractFacade<AgendaUsuario> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public AgendaUsuarioFacade() {
        super(AgendaUsuario.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<EventoAgenda> retornaEventosDoUsuarioPorDia(UsuarioSistema usuarioSistema, Date data) {
        StringBuilder hql = new StringBuilder();
        hql.append(" select evento from AgendaUsuario agenda ");
        hql.append(" inner join agenda.eventos evento ");
        hql.append(" where agenda.proprietario = :user ");
        hql.append(" and day(evento.dataHoraInicio) = day(:data) ");
        hql.append(" and month(evento.dataHoraInicio) = month(:data) ");
        hql.append(" and year(evento.dataHoraInicio) = year(:data) ");
        hql.append(" order by evento.dataHoraInicio asc ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("user", usuarioSistema);
        q.setParameter("data", data);

        List<EventoAgenda> toReturn = q.getResultList();
        Collections.sort(toReturn);

        return toReturn;
    }

    public List<EventoAgenda> retornaEventosFuturos(AgendaUsuario agenda, Date dataAtual) {
        StringBuilder hql = new StringBuilder();
        hql.append(" from EventoAgenda evento ");
        hql.append(" where evento.agendaUsuario = :agenda and evento.dataHoraInicio >= :dataAtual ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("agenda", agenda);
        q.setParameter("dataAtual", dataAtual);
        List<EventoAgenda> toReturn = q.getResultList();
        Collections.sort(toReturn);
        return toReturn;
    }

    public AgendaUsuario retornaAgendaDoUsuario(UsuarioSistema usuarioSistema) {
        StringBuilder hql = new StringBuilder();
        hql.append(" from AgendaUsuario where proprietario = :usuarioLogado");

        Query q = em.createQuery(hql.toString());
        q.setParameter("usuarioLogado", usuarioSistema);

        List<AgendaUsuario> agendas = q.getResultList();

        if (!agendas.isEmpty()) {
            AgendaUsuario agenda = agendas.get(0);
            agenda.getCompartilhamentos().size();
            agenda.getEventos().size();
            return agenda;
        }

        return null;
    }

    public List<UsuarioSistema> retornaUsuariosComAgendasCompartilhadasComUsuario(UsuarioSistema usuario) {
        StringBuilder hql = new StringBuilder();
        hql.append(" select agenda.proprietario from AgendaUsuario agenda ");
        hql.append(" inner join agenda.compartilhamentos compartilhamento ");
        hql.append(" where compartilhamento.usuarioSistema = :usuario ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("usuario", usuario);

        return q.getResultList();
    }

    public AgendaUsuario retornaNovaAgenda(UsuarioSistema usuarioSistema) {
        AgendaUsuario agenda = new AgendaUsuario();
        agenda.setProprietario(usuarioSistema);
        return em.merge(agenda);
    }

    public AgendaUsuario salvarAgenda(AgendaUsuario agenda) {
        return em.merge(agenda);
    }
}
