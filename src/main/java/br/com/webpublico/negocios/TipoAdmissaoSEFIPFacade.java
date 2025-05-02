/*
 * Codigo gerado automaticamente em Tue Feb 07 08:11:08 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoAdmissaoSEFIP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TipoAdmissaoSEFIPFacade extends AbstractFacade<TipoAdmissaoSEFIP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoAdmissaoSEFIPFacade() {
        super(TipoAdmissaoSEFIP.class);
    }

    public boolean existeCodigo(TipoAdmissaoSEFIP tipoAdmissaoSEFIP) {
        String hql = " from TipoAdmissaoSEFIP tipo where tipo.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", tipoAdmissaoSEFIP.getCodigo());
        if (((List<TipoAdmissaoSEFIP>) q.getResultList()).contains(tipoAdmissaoSEFIP)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }
}
