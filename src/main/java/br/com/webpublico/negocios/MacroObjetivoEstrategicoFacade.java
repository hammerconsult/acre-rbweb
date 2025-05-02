/*
 * Codigo gerado automaticamente em Wed Apr 13 14:44:51 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItemPlanejamentoEstrategico;
import br.com.webpublico.entidades.MacroObjetivoEstrategico;
import br.com.webpublico.entidades.PlanejamentoEstrategico;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class MacroObjetivoEstrategicoFacade extends AbstractFacade<MacroObjetivoEstrategico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<MacroObjetivoEstrategico> listaPorPlanejemnto(PlanejamentoEstrategico p) {
        String hql = "from MacroObjetivoEstrategico m where m.planejamentoEstrategico = :p order by m.descricao";
        Query q = em.createQuery(hql).setParameter("p", p);
        return q.getResultList();
    }

    public List<MacroObjetivoEstrategico> buscarEixosPorPlanejamentoFiltrando(String parte, PlanejamentoEstrategico pe){
        String sql = " select moe.* from MACROOBJETIVOESTRATEGICO moe where moe.PLANEJAMENTOESTRATEGICO_ID = :pe and moe.DESCRICAO like :parte ";
        Query q = em.createNativeQuery(sql, MacroObjetivoEstrategico.class);
        q.setParameter("pe", pe.getId());
        q.setParameter("parte", "%" + parte.trim() + "%");
        return q.getResultList();
    }

    public MacroObjetivoEstrategicoFacade() {
        super(MacroObjetivoEstrategico.class);
    }
}
