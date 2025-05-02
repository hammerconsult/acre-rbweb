/*
 * Codigo gerado automaticamente em Tue Feb 14 08:43:52 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Aposentadoria;
import br.com.webpublico.entidades.TipoAposentadoria;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class TipoAposentadoriaFacade extends AbstractFacade<TipoAposentadoria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoAposentadoriaFacade() {
        super(TipoAposentadoria.class);
    }

    public boolean existeCodigo(TipoAposentadoria tipoAposentadoria) {
        String hql = " from TipoAposentadoria t where t.codigo = :codigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigo", tipoAposentadoria.getCodigo());

        List<TipoAposentadoria> lista = new ArrayList<TipoAposentadoria>();
        lista = q.getResultList();

        if (lista.contains(tipoAposentadoria)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public TipoAposentadoria recuperaTipoAposentadoria(Aposentadoria aposentadoria) {
        String hql = "select tipo from Aposentadoria aposentadoria " +
                " inner join aposentadoria.tipoAposentadoria tipo " +
                " where aposentadoria = :aposentadoria";

        Query q =  em.createQuery(hql);
        q.setParameter("aposentadoria", aposentadoria);
        return (TipoAposentadoria) q.getSingleResult();
    }

    public Integer getMaxCodigo() {
        Query q= em.createQuery("select max(cast(codigo as integer))from TipoAposentadoria ");
        if(q.getResultList().isEmpty()){
            return 1;
        } else {
            Integer maxCod = (Integer) q.getResultList().get(0);
            maxCod++;
            return maxCod.intValue();
        }
    }
}
