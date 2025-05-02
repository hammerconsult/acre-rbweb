/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.GrupoDoctoOficial;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;

/**
 * @author fabio
 */
@Stateless
public class GrupoDoctoOficialFacade extends AbstractFacade<GrupoDoctoOficial> {

    public GrupoDoctoOficialFacade() {
        super(GrupoDoctoOficial.class);
    }

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Long ultimoCodigoMaisUm() {
        Query q = em.createNativeQuery("SELECT coalesce(max(codigo), 0) + 1 AS codigo FROM GrupoDoctoOficial grp");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    public Long ultimoCodigo() {
        String sql = "SELECT max(codigo) FROM GrupoDoctoOficial";
        Query q = em.createNativeQuery(sql);
        String resultado = (String) q.getResultList().get(0);
        return resultado != null ? new BigDecimal(resultado).longValue() : 1;
    }

    public boolean existeCodigo(Long codigo) {
        String sql = "SELECT * FROM GrupoDoctoOficial WHERE codigo = :codigo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

    public boolean existeCodigoGrupoDoctoOficial(GrupoDoctoOficial grupo) {
        String sql = "SELECT * FROM GrupoDoctoOficial WHERE codigo = :codigo AND id = :id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", grupo.getCodigo());
        q.setParameter("id", grupo.getId());
        return !q.getResultList().isEmpty();
    }

}
