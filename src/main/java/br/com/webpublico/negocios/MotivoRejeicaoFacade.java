/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MotivoRejeicao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class MotivoRejeicaoFacade extends AbstractFacade<MotivoRejeicao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MotivoRejeicaoFacade() {
        super(MotivoRejeicao.class);
    }

    public boolean existeCodigo(MotivoRejeicao motivoRejeicao) {
        String hql = " from MotivoRejeicao motivo where motivo.codigo = :parametroCodigo and id != :id ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", motivoRejeicao.getCodigo());
        if (motivoRejeicao.getId() != null) {
            q.setParameter("id", motivoRejeicao.getId());
        } else {
            q.setParameter("id", 0L);
        }
        return (!q.getResultList().isEmpty());
    }

    public MotivoRejeicao recuperaMotivoRejeicaoPorCodigo(int codigo) {
        Query q = getEntityManager().createQuery("from MotivoRejeicao motivo where motivo.codigo = :parametroCodigo");
        q.setParameter("parametroCodigo", codigo);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (MotivoRejeicao) q.getSingleResult();
    }
}
