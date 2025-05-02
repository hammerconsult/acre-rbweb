package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Equipamento;
import br.com.webpublico.entidades.ProgramaRevisaoEquipamento;
import br.com.webpublico.util.Util;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ProgramaRevisaoEquipamentoFacade extends AbstractFacade<ProgramaRevisaoEquipamento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ProgramaRevisaoEquipamentoFacade() {
        super(ProgramaRevisaoEquipamento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ProgramaRevisaoEquipamento> buscarProgramaRevisaoNaoRealizadosPorEquipamento(Equipamento equipamento, String parte) {
        if (equipamento == null || equipamento.getId() == null) {
            return new ArrayList();
        }
        String sql = " " +
            " select " +
            "   prog.* " +
            " from programarevisaoequipamento prog " +
            " where prog.equipamento_id = :idEquipamento " +
            "   and prog.sequencia like :param " +
            "   and prog.revisaoRealizada = :revisaoRealizada " +
            "   order by prog.datalancamento desc ";
        Query q = em.createNativeQuery(sql, ProgramaRevisaoEquipamento.class);
        q.setParameter("param", "%" + parte.trim() + "%");
        q.setParameter("idEquipamento", equipamento.getId());
        q.setParameter("revisaoRealizada", Boolean.FALSE);
        return q.getResultList();
    }

    public void movimentarProgramaRevisaoEquipamento(ProgramaRevisaoEquipamento entity, Boolean revisaoRealizada) {
        entity.setRevisaoRealizada(revisaoRealizada);
        em.merge(entity);
    }
}
