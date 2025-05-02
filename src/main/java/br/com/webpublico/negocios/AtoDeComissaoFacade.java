/*
 * Codigo gerado automaticamente em Thu Nov 10 14:09:53 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AtoDeComissao;
import br.com.webpublico.entidades.Comissao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class AtoDeComissaoFacade extends AbstractFacade<AtoDeComissao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ComissaoFacade comissaoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AtoDeComissaoFacade() {
        super(AtoDeComissao.class);
    }

    @Override
    public AtoDeComissao recuperar(Object id) {
        AtoDeComissao atoDeComissao = super.recuperar(id);
        atoDeComissao.getComissoes().size();
        for (Comissao comissao : atoDeComissao.getComissoes()) {
            comissaoFacade.recuperar(comissao.getId());
        }
        return atoDeComissao;
    }

    public Comissao recuperaComissao(AtoDeComissao atoDeComissao) {
        String hql = "from " + Comissao.class.getSimpleName() + " a  where a.atoDeComissao=:param";
        if (atoDeComissao.getId() != null) {
            Query q = getEntityManager().createQuery(hql);
            q.setParameter("param", atoDeComissao);
            q.setMaxResults(1);
            if (q.getSingleResult() != null) {
                return (Comissao) q.getSingleResult();
            }
            return new Comissao();
        } else {
            return new Comissao();
        }
    }
}
