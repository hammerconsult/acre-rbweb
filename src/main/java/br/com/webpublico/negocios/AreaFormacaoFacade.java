package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AreaFormacao;
import br.com.webpublico.singletons.SingletonGeradorCodigoRH;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by AndreGustavo on 29/09/2014.
 */
@Stateless
public class AreaFormacaoFacade extends AbstractFacade<AreaFormacao> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SingletonGeradorCodigoRH singletonGeradorCodigoRH;


    public AreaFormacaoFacade() {
        super(AreaFormacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<AreaFormacao> completaAreaFormacao(String filtro) {
        String hql = "from AreaFormacao where lower(trim(codigo)) like :filtro" +
                " or lower(trim(descricao)) like :filtro";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public boolean buscarCadastroEmFormacao(AreaFormacao filtro){
        String sql = " select distinct F.AREAFORMACAO_ID from FORMACAO F " +
            "          inner join AREAFORMACAO A on F.AREAFORMACAO_ID = A.ID" +
            "          where A.ID = :filtro";
        Query q = em.createNativeQuery(sql);
        q.setParameter("filtro", filtro.getId());
        return !q.getResultList().isEmpty();
    }

    public SingletonGeradorCodigoRH getSingletonGeradorCodigoRH() {
        return singletonGeradorCodigoRH;
    }
}
