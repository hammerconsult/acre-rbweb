package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Capacitacao;
import br.com.webpublico.entidades.Habilidade;
import br.com.webpublico.singletons.SingletonGeradorCodigoRH;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by AndreGustavo on 23/09/2014.
 */
@Stateless
public class HabilidadeFacade extends AbstractFacade<Habilidade> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SingletonGeradorCodigoRH singletonGeradorCodigoRH;


    public HabilidadeFacade() {
        super(Habilidade.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean existeHabilidadePorCodigo(String codigo) {
        String hql = "from Habilidade where codigo = :cod";
        Query q = em.createQuery(hql);
        q.setParameter("cod", codigo);

        //System.out.println(q.getResultList().isEmpty());

        return !q.getResultList().isEmpty();
    }

    public List<Habilidade> completaHabilidade(String filtro) {
        String hql = "from Habilidade where lower(trim(codigo)) like :filtro" +
            " or lower(trim(descricao)) like :filtro";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Habilidade> buscarHabilidadesPorCapacitacao(String filtro, Capacitacao capacitacao) {
        String sql = " " +
            "   select hab.* " +
            "    from capacitacaohabilidade ch " +
            "    inner join habilidade hab on ch.habilidade_id = hab.id " +
            "   where ch.capacitacao_id = :idCapacitacao " +
            "     and (lower(trim(codigo)) like :filtro " +
            "     or lower(trim(descricao)) like :filtro) ";
        Query q = em.createNativeQuery(sql, Habilidade.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("idCapacitacao", capacitacao.getId());
        return q.getResultList();
    }

    public SingletonGeradorCodigoRH getSingletonGeradorCodigoRH() {
        return singletonGeradorCodigoRH;
    }
}
