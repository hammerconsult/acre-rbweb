/*
 * Codigo gerado automaticamente em Tue Apr 03 16:26:00 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoPenalidadeFP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class TipoPenalidadeFacade extends AbstractFacade<TipoPenalidadeFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoPenalidadeFacade() {
        super(TipoPenalidadeFP.class);
    }

    @Override
    public TipoPenalidadeFP recuperar(Object id) {
        TipoPenalidadeFP tipo = super.recuperar(id);
        forcarRegistroInicialNaAuditoria(tipo);
        return tipo;
    }


    public boolean podeSalvar(String codigo) {
        Query q = em.createQuery("from TipoPenalidadeFP tipo where tipo.codigo = :codigo");
        q.setParameter("codigo", codigo);
        return q.getResultList().isEmpty();
    }

    public boolean existeCodigo(TipoPenalidadeFP tipoPenalidade) {
        String hql = " from TipoPenalidadeFP t where t.codigo = :codigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigo", tipoPenalidade.getCodigo());
        List<TipoPenalidadeFP> lista = new ArrayList<TipoPenalidadeFP>();
        lista = q.getResultList();
        if (lista.contains(tipoPenalidade)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }
}
