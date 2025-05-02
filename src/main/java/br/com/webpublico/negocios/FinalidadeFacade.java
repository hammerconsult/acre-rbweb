/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Finalidade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;

/**
 * @author leonardo
 */
@Stateless
public class FinalidadeFacade extends AbstractFacade<Finalidade> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public FinalidadeFacade() {
        super(Finalidade.class);
    }

    public Long ultimoCodigoMaisUm() {
        Query q = em.createNativeQuery("SELECT coalesce(max(codigo), 0) + 1 AS codigo FROM Finalidade ti");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    public Long ultimoCodigo() {
        String sql = "SELECT max(codigo) FROM Finalidade";
        Query q = em.createNativeQuery(sql);
        String resultado = (String) q.getResultList().get(0);
        return resultado != null ? new BigDecimal(resultado).longValue() : 1;
    }

    public boolean existeCodigo(Long codigo) {
        String sql = "SELECT * FROM Finalidade WHERE codigo = :codigo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

    public boolean existeCodigoFinalidade(Finalidade finalidade) {
        String sql = "SELECT * FROM Finalidade WHERE codigo = :codigo AND id = :id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", finalidade.getCodigo());
        q.setParameter("id", finalidade.getId());
        return !q.getResultList().isEmpty();
    }

}
