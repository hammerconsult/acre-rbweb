/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MotivoDesligamentoFGTS;
import br.com.webpublico.entidades.TipoAdmissaoFGTS;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class MotivoDesligamentoFGTSFacade extends AbstractFacade<MotivoDesligamentoFGTS> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MotivoDesligamentoFGTSFacade() {
        super(MotivoDesligamentoFGTS.class);
    }

    public boolean existeCodigo(MotivoDesligamentoFGTS motivoDesligamentoFGTS) {
        String hql = " from MotivoDesligamentoFGTS tipo where tipo.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", motivoDesligamentoFGTS.getCodigo());

        List<TipoAdmissaoFGTS> lista = new ArrayList<TipoAdmissaoFGTS>();
        lista = q.getResultList();

        if (lista.contains(motivoDesligamentoFGTS)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public MotivoDesligamentoFGTS recuperaMotivoDesligamentoFGTSPorCodigo(int codigo) {
        Query q = getEntityManager().createQuery("from MotivoDesligamentoFGTS tipo where tipo.codigo = :parametroCodigo");
        q.setParameter("parametroCodigo", codigo);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (MotivoDesligamentoFGTS) q.getSingleResult();
    }
}
