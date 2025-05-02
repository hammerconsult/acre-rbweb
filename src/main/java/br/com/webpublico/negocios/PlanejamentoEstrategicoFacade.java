/*
 * Codigo gerado automaticamente em Thu Mar 31 17:31:18 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.PlanejamentoEstrategico;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class PlanejamentoEstrategicoFacade extends AbstractFacade<PlanejamentoEstrategico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PlanejamentoEstrategicoFacade() {
        super(PlanejamentoEstrategico.class);
    }

    @Override
    public PlanejamentoEstrategico recuperar(Object id) {
        PlanejamentoEstrategico plan = em.find(PlanejamentoEstrategico.class, id);
        plan.getExerciciosPlanoEstrategico().size();
        plan.getItens().size();
        plan.getMacros().size();
        //System.out.println("retornaaa...:" + plan);
        return plan;
    }

    public List<PlanejamentoEstrategico> listaDisponiveis() {
        String sql = "SELECT pe.* FROM planejamentoestrategico pe WHERE pe.id NOT IN (SELECT p.planejamentoestrategico_id FROM ppa p)"
                + " UNION ALL "
                + "SELECT pe.* FROM planejamentoestrategico pe, ppa p WHERE pe.id = p.planejamentoestrategico_id AND p.somenteleitura = 0 AND p.aprovacao IS NOT null";
        Query q = em.createNativeQuery(sql, PlanejamentoEstrategico.class);
        return q.getResultList();
    }
}
