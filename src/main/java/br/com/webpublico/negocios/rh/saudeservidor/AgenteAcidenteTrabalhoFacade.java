package br.com.webpublico.negocios.rh.saudeservidor;

import br.com.webpublico.entidades.rh.saudeservidor.AgenteAcidenteTrabalho;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author alex on 22/09/2016  14:41.
 */
@Stateless
public class AgenteAcidenteTrabalhoFacade extends AbstractFacade<AgenteAcidenteTrabalho> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public AgenteAcidenteTrabalhoFacade() {
        super(AgenteAcidenteTrabalho.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<AgenteAcidenteTrabalho> buscarFiltrandoPorCodigoAndDescricao(String parte) {
        String sql = " select * " +
            "          from AGENTEACIDENTETRABALHO " +
            "          where (codigo like :parte or descricao like :parte) ";
        Query q = em.createNativeQuery(sql, AgenteAcidenteTrabalho.class);
        q.setParameter("parte", "%" + parte + "%");
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }
}
