/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MotivoDesligamentoRAIS;
import br.com.webpublico.entidades.TipoAdmissaoRAIS;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class MotivoDesligamentoRAISFacade extends AbstractFacade<MotivoDesligamentoRAIS> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MotivoDesligamentoRAISFacade() {
        super(MotivoDesligamentoRAIS.class);
    }

    public boolean existeCodigo(MotivoDesligamentoRAIS motivoDesligamentoRAIS) {
        String hql = " from MotivoDesligamentoRAIS tipo where tipo.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", motivoDesligamentoRAIS.getCodigo());

        List<TipoAdmissaoRAIS> lista = new ArrayList<TipoAdmissaoRAIS>();
        lista = q.getResultList();

        if (lista.contains(motivoDesligamentoRAIS)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public MotivoDesligamentoRAIS recuperaMotivoDesligamentoRAISPorCodigo(int codigo) {
        Query q = getEntityManager().createQuery("from MotivoDesligamentoRAIS tipo where tipo.codigo = :parametroCodigo");
        q.setParameter("parametroCodigo", codigo);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (MotivoDesligamentoRAIS) q.getSingleResult();
    }
}
