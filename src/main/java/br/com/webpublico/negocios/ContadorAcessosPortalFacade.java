/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContadorAcessosPortal;
import br.com.webpublico.entidades.Cor;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
public class ContadorAcessosPortalFacade extends AbstractFacade<ContadorAcessosPortal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ContadorAcessosPortalFacade() {
        super(ContadorAcessosPortal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ContadorAcessosPortal> findByDataInicialAndFinal(Date inicio, Date fim) {
        String sql = "select contador " +
                "       from ContadorAcessosPortal contador" +
                "      where trunc(contador.acessoEm) between :inicio and :fim";
        Query q = em.createQuery(sql);
        q.setParameter("inicio", inicio);
        q.setParameter("fim", fim);

        return q.getResultList();
    }


    public Long countTotalAcessos(ContadorAcessosPortal.TipoAcesso tipoAcesso) {
        String sql = "select count(contador.id) " +
                "       from ContadorAcessosPortal contador" +
                "      where contador.tipoAcesso = :tipo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipo", tipoAcesso.name());
        return ((BigDecimal) q.getSingleResult()).longValue();
    }

    public Long countTotalContribuintesPortal() {
        String sql = "select count(usu.id) " +
                "       from USUARIOWEB usu";
        Query q = em.createNativeQuery(sql);
        return ((BigDecimal) q.getSingleResult()).longValue();
    }

}
