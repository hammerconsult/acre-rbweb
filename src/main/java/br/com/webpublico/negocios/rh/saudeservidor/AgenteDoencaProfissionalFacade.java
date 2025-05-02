package br.com.webpublico.negocios.rh.saudeservidor;

import br.com.webpublico.entidades.rh.saudeservidor.AgenteDoencaProfissional;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author alex on 22/09/2016  15:19.
 */
@Stateless
public class AgenteDoencaProfissionalFacade extends AbstractFacade<AgenteDoencaProfissional> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public AgenteDoencaProfissionalFacade() {
        super(AgenteDoencaProfissional.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<AgenteDoencaProfissional> buscarFiltrandoPorCodigoAndDescricao(String parte) {
        String sql = " select * " +
            "          from AGENTEDOENCAPROFISSIONAL " +
            "          where (codigo like :parte or descricao like :parte) ";
        Query q = em.createNativeQuery(sql, AgenteDoencaProfissional.class);
        q.setParameter("parte", "%" + parte + "%");
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }
}
