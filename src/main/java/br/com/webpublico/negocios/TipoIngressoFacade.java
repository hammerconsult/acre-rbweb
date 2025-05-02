/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoBaixaBens;
import br.com.webpublico.enums.TipoIngressoBaixa;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.entidades.TipoIngresso;

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
public class TipoIngressoFacade extends AbstractFacade<TipoIngresso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoIngressoFacade() {
        super(TipoIngresso.class);
    }

    public List<TipoIngresso> listaFiltrandoPorTipoBem(String parte, TipoBem tipo) {
        String hql = "from TipoIngresso ti where ti.tipoBem = :tipoBem and lower(ti.descricao) like :parte";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("tipoBem", tipo);
        return q.getResultList();
    }

    public List<TipoIngresso> buscarFiltrandoPorTipoIngressoAndTipoBem(String parte, TipoIngressoBaixa tipoIngressoBaixa, TipoBem tipoBem) {
        String sql = "select tp.* " +
            "       from tipoingresso tp" +
            "      where tp.tipoIngressoBaixa = :tipoIngresso" +
            "        and tp.tipoBem = :tipoBem" +
            "        and lower(tp.descricao) like :parte";

        Query q = em.createNativeQuery(sql, TipoIngresso.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("tipoIngresso", tipoIngressoBaixa.name());
        q.setParameter("tipoBem", tipoBem.name());

        return q.getResultList();
    }

    public TipoIngresso recuperarTipoIngresso(TipoBem tipoBem, TipoIngressoBaixa tipoIngressoBaixa) {
        String sql = "select tp.* " +
            "       from tipoingresso tp" +
            "      where tp.tipoIngressoBaixa = :tipoIngresso" +
            "        and tp.tipoBem = :tipoBem ";
        Query q = em.createNativeQuery(sql, TipoIngresso.class);
        q.setParameter("tipoIngresso", tipoIngressoBaixa.name());
        q.setParameter("tipoBem", tipoBem.name());
        try {
            return (TipoIngresso) q.getResultList().get(0);
        } catch (NoResultException e) {
            return null;
        }
    }
}
