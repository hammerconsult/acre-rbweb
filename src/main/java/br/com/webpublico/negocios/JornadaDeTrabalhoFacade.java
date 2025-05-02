/*
 * Codigo gerado automaticamente em Fri Aug 05 12:00:12 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.JornadaDeTrabalho;
import br.com.webpublico.util.UtilRH;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;

@Stateless
public class JornadaDeTrabalhoFacade extends AbstractFacade<JornadaDeTrabalho> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public JornadaDeTrabalhoFacade() {
        super(JornadaDeTrabalho.class);
    }

    @Override
    public void salvarNovo(JornadaDeTrabalho entity) {
        entity.setCodigo(lista().size() + 1);
        super.salvarNovo(entity);
    }

    public BigDecimal retornaHorasSemanaisSomadas(ContratoFP contrato) {
        String sql = "SELECT sum(jt.horassemanal)"
                + "           FROM contratofp c                 "
                + "                JOIN vinculofp v ON v.id = c.id"
                + "                JOIN jornadadetrabalho jt ON c.jornadadetrabalho_id = jt.id"
                + "                           WHERE :data BETWEEN v.iniciovigencia AND coalesce(v.finalvigencia,:data) "
                + "                                     AND v.matriculafp_id = :matricula ";
        if (contrato.getId() != null) {
            sql += "AND C.id <> :contrato";
        }
        Query q = this.em.createNativeQuery(sql);
        q.setParameter("data", UtilRH.getDataOperacao());
        q.setParameter("matricula", contrato.getMatriculaFP().getId());
        if (contrato.getId() != null) {
            q.setParameter("contrato", contrato.getId());
        }
        q.setMaxResults(1);
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }
}
