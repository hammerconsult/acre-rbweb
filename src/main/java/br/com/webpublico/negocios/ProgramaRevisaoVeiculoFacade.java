/*
 * Codigo gerado automaticamente em Thu May 26 15:37:42 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ProgramaRevisaoVeiculo;
import br.com.webpublico.entidades.Veiculo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ProgramaRevisaoVeiculoFacade extends AbstractFacade<ProgramaRevisaoVeiculo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ProgramaRevisaoVeiculoFacade() {
        super(ProgramaRevisaoVeiculo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ProgramaRevisaoVeiculo> buscarProgramaRevisaoNaoRealizadosPorVeiculo(Veiculo veiculo, String parte) {
        if (veiculo == null || veiculo.getId() == null) {
            return new ArrayList();
        }
        String sql = " " +
            " select " +
            "   prog.* " +
            " from programarevisaoveiculo prog " +
            " where prog.veiculo_id = :idVeiculo " +
            "   and prog.sequencia like :param " +
            "   and prog.revisaoRealizada = :revisaoRealizada " +
            "   order by prog.datalancamento desc ";
        Query q = em.createNativeQuery(sql, ProgramaRevisaoVeiculo.class);
        q.setParameter("param", "%" + parte.trim() + "%");
        q.setParameter("idVeiculo", veiculo.getId());
        q.setParameter("revisaoRealizada", Boolean.FALSE);
        return q.getResultList();
    }

    public void movimentarProgramaRevisaoVeiculo(ProgramaRevisaoVeiculo entity, Boolean revisaoRealizada) {
        entity.setRevisaoRealizada(revisaoRealizada);
        em.merge(entity);
    }
}
