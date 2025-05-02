/*
 * Codigo gerado automaticamente em Thu May 19 14:54:48 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CotaMaterialCentroDeCusto;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class CotaMaterialCentroDeCustoFacade extends AbstractFacade<CotaMaterialCentroDeCusto> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CotaMaterialCentroDeCustoFacade() {
        super(CotaMaterialCentroDeCusto.class);
    }

    public Boolean jaExisteMaterialCadastradoParametros(CotaMaterialCentroDeCusto cmcc) {
        String hql = "from CotaMaterialCentroDeCusto as c where"
                + " c.centroDeCusto = :centroDeCusto and"
                + " c.material = :material and"
                + " c.mes = :mes and"
                + " c.ano = :ano";
        Query q = em.createQuery(hql);
        q.setParameter("centroDeCusto", cmcc.getCentroDeCusto());
        q.setParameter("material", cmcc.getMaterial());
        q.setParameter("mes", cmcc.getMes());
        q.setParameter("ano", cmcc.getAno());
        List<CotaMaterialCentroDeCusto> lista = q.getResultList();
        if (lista.contains(cmcc)) {
            return Boolean.FALSE;
        } else {
            return !q.getResultList().isEmpty();
        }
    }
}
