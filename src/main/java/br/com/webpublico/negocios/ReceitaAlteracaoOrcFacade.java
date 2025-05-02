/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ReceitaAlteracaoORC;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author claudio
 */
@Stateless
public class ReceitaAlteracaoOrcFacade extends AbstractFacade<ReceitaAlteracaoORC> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ReceitaAlteracaoOrcFacade() {
        super(ReceitaAlteracaoORC.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Object[]> buscarOrcamentoReceita(Exercicio exercicio) {
        String sql = " select ano, contaCodigo, fonteCodigo, sum(orcamento) as orcamento from ( " +
            " select ex.ano, " +
            "        C.CODIGO as contaCodigo, " +
            "        fr.codigo as fonteCodigo, " +
            "        case when RECLOA.operacaoreceita in ('DEDUCAO_RECEITA_RENUNCIA', 'DEDUCAO_RECEITA_RESTITUICAO', 'DEDUCAO_RECEITA_DESCONTO', 'DEDUCAO_RECEITA_FUNDEB', 'DEDUCAO_RECEITA_OUTRAS') " +
            "           then COALESCE(SUM(RECLOAFONTE.VALOR), 0) * -1 " +
            "           else COALESCE(SUM(RECLOAFONTE.VALOR), 0) " +
            "        end as orcamento " +
            "   FROM RECEITALOA RECLOA " +
            "  INNER JOIN receitaloafonte RECLOAFONTE ON RECLOAFONTE.RECEITALOA_ID = RECLOA.ID " +
            "  INNER join contadedestinacao cd on RECLOAFONTE.destinacaoderecursos_id = cd.id " +
            "  INNER join fontederecursos fr on cd.fontederecursos_id = fr.id " +
            "  INNER JOIN LOA ON LOA.ID = RECLOA.LOA_ID " +
            "  INNER JOIN LDO ON LDO.ID = LOA.LDO_ID " +
            "  INNER JOIN CONTA C ON RECLOA.CONTADERECEITA_ID = C.ID " +
            "  inner join exercicio ex on LDO.EXERCICIO_ID = ex.id " +
            "  WHERE ex.id = :exercicio " +
            "  group by ex.ano, c.codigo, fr.codigo, recloa.operacaoreceita " +
            "  union all " +
            " select ex.ano, " +
            "        c.codigo as contaCodigo, " +
            "        fr.codigo as fonteCodigo, " +
            "        case when ALT.operacaoreceita in ('DEDUCAO_RECEITA_RENUNCIA', 'DEDUCAO_RECEITA_RESTITUICAO', 'DEDUCAO_RECEITA_DESCONTO', 'DEDUCAO_RECEITA_FUNDEB', 'DEDUCAO_RECEITA_OUTRAS') " +
            "           then (COALESCE(DECODE(ALT.TIPOALTERACAOORC, 'PREVISAO', ALT.VALOR, ALT.VALOR * (-1)), 0)) * -1 " +
            "           else (COALESCE(DECODE(ALT.TIPOALTERACAOORC, 'PREVISAO', ALT.VALOR, ALT.VALOR * (-1)), 0)) " +
            "        end AS orcamento " +
            "   FROM RECEITALOA REC " +
            "  INNER JOIN RECEITAALTERACAOORC ALT ON REC.ID = ALT.RECEITALOA_ID " +
            "  INNER join fontederecursos fr on alt.fontederecurso_id = fr.id " +
            "  INNER join contadedestinacao cd on cd.fontederecursos_id = fr.id  " +
            "  INNER join conta cdest on cd.id = cdest.id  and cdest.permitirDesdobramento = 0  " +
            "  INNER JOIN ALTERACAOORC ALTORC ON ALTORC.ID = ALT.ALTERACAOORC_ID " +
            "  INNER JOIN CONTA C ON REC.CONTADERECEITA_ID = C.ID " +
            "  INNER JOIN exercicio ex ON c.exercicio_id = ex.ID " +
            "  WHERE ex.id = :exercicio " +
            "  ) " +
            "  group by ano, contaCodigo, fonteCodigo " +
            "  order by ano, contaCodigo, fonteCodigo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }
}
