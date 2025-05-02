/*
 * Codigo gerado automaticamente em Thu Sep 15 16:18:48 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.TipoPrevidenciaFP;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TipoPrevidenciaFPFacade extends AbstractFacade<TipoPrevidenciaFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoFPFacade eventoFPFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoFPFacade getEventoFPFacade() {
        return eventoFPFacade;
    }

    @Override
    public TipoPrevidenciaFP recuperar(Object id) {
        TipoPrevidenciaFP t = em.find(TipoPrevidenciaFP.class, id);
        t.getEventosFP().size();
        forcarRegistroInicialNaAuditoria(t);
        return t;
    }

    @Override
    public void salvarNovo(TipoPrevidenciaFP entity) {
        atualizaEventos(entity);
        super.salvarNovo(entity);
    }

    public void atualizaEventos(TipoPrevidenciaFP entity) {
        for (EventoFP evento : entity.getEventosFP()) {
            evento.setTipoPrevidenciaFP(null);
            em.merge(evento);
        }
    }


    public void salvar(TipoPrevidenciaFP entity, List<EventoFP> excluidos) {
        entity = em.merge(entity);

        if (entity.getEventosFP() != null) {
            for (EventoFP evento : entity.getEventosFP()) {
                evento.setTipoPrevidenciaFP(entity);
                evento = em.merge(evento);
            }
        }
        for (EventoFP excluido : excluidos) {
            excluido.setTipoPrevidenciaFP(null);
            em.merge(excluido);
        }

    }

    public TipoPrevidenciaFPFacade() {
        super(TipoPrevidenciaFP.class);
    }

    public boolean verificaCodigoSalvo(String codigo) {
        Query q = em.createQuery("from TipoPrevidenciaFP e where e.codigo = :codigo");
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

    /**
     * Método utilizando para editar, casa o codigo ja esteja em utilização não salva.
     *
     * @param tipoPrevidencia
     * @return valor booleano
     */
    public boolean verificaCodigoEditar(TipoPrevidenciaFP tipoPrevidencia) {
        Query q = em.createQuery("from TipoPrevidenciaFP e where (e.codigo = :codigo and e = :tipo)");
        q.setParameter("tipo", tipoPrevidencia);
        q.setParameter("codigo", tipoPrevidencia.getCodigo());
        return !q.getResultList().isEmpty();
    }

    public List<TipoPrevidenciaFP> filtrandoTipoPrevidencia(String parte) {
        Query q = em.createQuery("from TipoPrevidenciaFP e where (lower(e.descricao) like :filtro or e.codigo like :filtro) ");
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public void salvarEvento(EventoFP evento) {
        try {
            em.merge(evento);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
