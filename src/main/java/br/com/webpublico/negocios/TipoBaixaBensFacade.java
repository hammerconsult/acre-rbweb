/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoBaixaBens;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoIngressoBaixa;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Claudio
 */
@Stateless
public class TipoBaixaBensFacade extends AbstractFacade<TipoBaixaBens> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoBaixaBensFacade() {
        super(TipoBaixaBens.class);
    }

    public List<TipoBaixaBens> buscarFiltrandoPorTipoBemAndTipoIngresso(String filtro, TipoBem tipoBem, TipoIngressoBaixa tipoIngressoBaixa) {
        String hql = "from TipoBaixaBens tbb " +
            "           where tbb.tipoBem = :tipoBem "
            + "         and tbb.tipoIngressoBaixa = :tipoIngresso "
            + "         and lower(tbb.descricao) like :filtro ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("tipoBem", tipoBem);
        q.setParameter("tipoIngresso", tipoIngressoBaixa);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<TipoBaixaBens> listaFiltrandoPorTipoBem(String parte, TipoBem tipo) {
        String hql = "from TipoBaixaBens tbb where tbb.tipoBem = :tipoBem and lower(tbb.descricao) like :parte";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("tipoBem", tipo);
        return q.getResultList();
    }

    public TipoBaixaBens recuperarTipoIngressoBaixa(TipoBem tipoBem, TipoIngressoBaixa tipoIngressoBaixa) {
        String sql = "select tbb.* from tipobaixabens tbb " +
            "           where tbb.tipobem = :tipoBem "
            + "         and tbb.tipoingressobaixa = :tipoBaixa " +
            "         order by tbb.id desc fetch first 1 rows only ";
        Query q = getEntityManager().createNativeQuery(sql, TipoBaixaBens.class);
        q.setParameter("tipoBem", tipoBem.name());
        q.setParameter("tipoBaixa", tipoIngressoBaixa.name());
        q.setMaxResults(1);
        try {
            return (TipoBaixaBens) q.getResultList().get(0);
        } catch (NoResultException e) {
            return null;
        }
    }
}
