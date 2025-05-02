package br.com.webpublico.negocios.rh.saudeservidor;

import br.com.webpublico.entidades.rh.saudeservidor.ParteCorpo;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author alex on 22/09/2016  10:43.
 */
@Stateless
public class ParteCorpoFacade extends AbstractFacade<ParteCorpo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ParteCorpoFacade() {
        super(ParteCorpo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ParteCorpo> buscarFiltrandoPorCodigoAndDescricao(String parte) {
        String sql = " select * " +
            "          from PARTECORPO " +
            "          where (codigo like :parte or descricao like :parte) ";
        Query q = em.createNativeQuery(sql, ParteCorpo.class);
        q.setParameter("parte", "%" + parte + "%");
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }
}
