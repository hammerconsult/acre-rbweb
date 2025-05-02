/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoAdmissaoFGTS;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class TipoAdmissaoFGTSFacade extends AbstractFacade<TipoAdmissaoFGTS> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoAdmissaoFGTSFacade() {
        super(TipoAdmissaoFGTS.class);
    }

    public boolean existeCodigo(TipoAdmissaoFGTS tipoAdmissaoFGTS) {
        String hql = " from TipoAdmissaoFGTS tipo where tipo.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", tipoAdmissaoFGTS.getCodigo());

        List<TipoAdmissaoFGTS> lista = new ArrayList<TipoAdmissaoFGTS>();
        lista = q.getResultList();

        if (lista.contains(tipoAdmissaoFGTS)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }
}
