/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoAdmissaoRAIS;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class TipoAdmissaoRAISFacade extends AbstractFacade<TipoAdmissaoRAIS> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoAdmissaoRAISFacade() {
        super(TipoAdmissaoRAIS.class);
    }

    public boolean existeCodigo(TipoAdmissaoRAIS tipoAdmissaoRAIS) {
        String hql = " from TipoAdmissaoRAIS tipo where tipo.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", tipoAdmissaoRAIS.getCodigo());

        List<TipoAdmissaoRAIS> lista = new ArrayList<TipoAdmissaoRAIS>();
        lista = q.getResultList();

        if (lista.contains(tipoAdmissaoRAIS)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }
}
