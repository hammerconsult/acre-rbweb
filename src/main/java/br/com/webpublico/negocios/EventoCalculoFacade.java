/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EventoCalculo;
import br.com.webpublico.entidadesauxiliares.GrupoEvento;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless

public class EventoCalculoFacade extends AbstractFacade<EventoCalculo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private NovoCalculoIPTUFacade novoCalculoIPTUFacada;


    public EventoCalculoFacade() {
        super(EventoCalculo.class);
    }

    public NovoCalculoIPTUFacade getNovoCalculoIPTUFacada() {
        return novoCalculoIPTUFacada;
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;
    }

    public TributoFacade getTributoFacade() {
        return tributoFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoCalculo merge(EventoCalculo eventoCalculo) {
        return em.merge(eventoCalculo);
    }

    public EventoCalculo recuperarPorCodigo(Integer codigdo) {
        try {
            return (EventoCalculo) em.createQuery("select e from EventoCalculo  e where e.codigo = :codigo").setParameter("codigo", codigdo).getResultList().get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public List<EventoCalculo> listaEventosParaCalculo() {
        try {
            return em.createQuery("select e from EventoCalculo  e where e.tipoLancamento <> 'CALCULO_COMPLEMENTAR'").getResultList();
        } catch (Exception e) {
            return null;
        }
    }
    public List<EventoCalculo> listaEventosVigentes() {
        try {
            return em.createQuery("select e from EventoCalculo  e where coalesce(e.finalVigencia, current_date ) >= current_date ").getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean getTemCalculoParaEvento(EventoCalculo eventoCalculo) {
        String hql = "SELECT * FROM memoriacalculoiptu m WHERE m.evento_id = :evento";
        Query q = em.createNativeQuery(hql);
        q.setParameter("evento", eventoCalculo.getId());
        q.setMaxResults(1);
        //System.out.println("tem calculo p esse evento? " + q.getResultList().isEmpty());
        return !q.getResultList().isEmpty();
    }

    public EventoCalculo validaVigencia(EventoCalculo e) {
        String hql = "SELECT e " +
                "FROM EventoCalculo e " +
                "where e.identificacao = :identificacao " +
                "and ( " +
                "(e.finalVigencia is null ) " +
                "or (e.inicioVigencia >= :inicio and e.inicioVigencia <= :fim and e.finalVigencia >= :fim) " +
                "or (e.inicioVigencia <= :inicio and e.inicioVigencia <= :fim and e.finalVigencia >= :inicio and e.finalVigencia <= :fim) " +
                ")";
        if (e.getId() != null) {
            hql += " and e.id != :id ";
        }
        Query q = em.createQuery(hql)
                .setParameter("identificacao", e.getIdentificacao())
                .setParameter("inicio", e.getInicioVigencia())
                .setParameter("fim", e.getFinalVigencia() != null ? e.getFinalVigencia() : new Date());
        if (e.getId() != null) {
            q.setParameter("id", e.getId());
        }
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (EventoCalculo) q.getResultList().get(0);
        }
        return null;
    }

    public void salvar(GrupoEvento grupo) {
        for (EventoCalculo eventoCalculo : grupo.getEventos()) {
            if (validaVigencia(eventoCalculo) == null) {
                em.persist(eventoCalculo);
            }
        }
    }

    public List<EventoCalculo> listaEventosVigentes(String parte) {
        try {
            return em.createQuery("select distinct e from EventoCalculo e where " +
                    " coalesce(e.finalVigencia, current_date ) >= current_date " +
                    " and (lower(e.descricao) like :filtro" +
                    " or lower(e.identificacao) like :filtro )").setParameter("filtro", "%" +parte.toLowerCase()+"%").getResultList();
        } catch (Exception e) {
            return null;
        }
    }
}
