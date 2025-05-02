/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoDocumento;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @author peixe
 */
@Stateless
public class TipoDocumentoFacade extends AbstractFacade<TipoDocumento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoDocumentoFacade() {
        super(TipoDocumento.class);
    }

    public boolean existeCodigo(TipoDocumento tipoDocumento) {
        String hql = " from TipoDocumento tipo where tipo.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", tipoDocumento.getCodigo());

        List<TipoDocumento> lista = new ArrayList<TipoDocumento>();
        lista = q.getResultList();

        if (lista.contains(tipoDocumento)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public List<TipoDocumento> listaTipoDocumentosMarcadosComoAverbacao() {
        String hql = " from TipoDocumento tipo where tipo.utilizarTempoContribuicao = :parametroCodigo order by tipo.codigo asc";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", Boolean.TRUE);
        return q.getResultList();
    }

    public List<TipoDocumento> getTiposDocumentosOrdemCrescente(){
        String sql = "select t.* from TipoDocumento t order by t.codigo";
        Query q = em.createNativeQuery(sql, TipoDocumento.class);
        return q.getResultList();
    }
}
