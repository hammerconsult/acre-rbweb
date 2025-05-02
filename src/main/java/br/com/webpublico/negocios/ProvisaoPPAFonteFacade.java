/*
 * Codigo gerado automaticamente em Fri May 06 09:14:43 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.DataUtil;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class ProvisaoPPAFonteFacade extends AbstractFacade<ProvisaoPPAFonte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ProvisaoPPAFonte> listaPorDespesa(ProvisaoPPADespesa p) {
        String hql = "from ProvisaoPPAFonte p where p.provisaoPPADespesa = :parametro ";
        Query q = em.createQuery(hql);
        q.setParameter("parametro", p);
        return q.getResultList();

    }

    public BigDecimal recuperarSaldoPorFonteUnidadeExercicioContaDespesa(Exercicio exercicio, UnidadeOrganizacional unidade, Conta conta, SubAcaoPPA subAcaoPPA, Date data) {
        BigDecimal saldo = BigDecimal.ZERO;

        String sql = "select sum(f.valor) " +
            "  from provisaoppafonte f " +
            "  inner join provisaoppadespesa a on f.provisaoppadespesa_id = a.id " +
            "  inner join conta b on a.contadedespesa_id = b.id " +
            "  inner join vwhierarquiaorcamentaria vw on a.unidadeorganizacional_id = vw.subordinada_id " +
            "  inner join subacaoppa i on a.subacaoppa_id = i.id " +
            "  inner join acaoppa j on i.acaoppa_id = j.id " +
            "  where i.exercicio_id = :idExercicio " +
            "  and to_date(:data,'dd/mm/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia,to_date(:data,'dd/mm/yyyy')) " +
            "  and i.id = :idSubacao" +
            "  and b.codigo = :codigoConta" +
            "  and a.unidadeorganizacional_id = :idUnidade ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("idSubacao", subAcaoPPA.getId());
        q.setParameter("codigoConta", conta.getCodigo());
        q.setParameter("idUnidade", unidade.getId());
        if (!q.getResultList().isEmpty()) {
            saldo = (BigDecimal) q.getSingleResult();
        }
        return saldo;
    }

    public ProvisaoPPAFonteFacade() {
        super(ProvisaoPPAFonte.class);
    }

    public List<ProvisaoPPAFonte> listaProvisoesFonte(ProvisaoPPADespesa p) {
        Query q = em.createQuery("from ProvisaoPPAFonte a where a.provisaoPPADespesa = :p");
        q.setParameter("p", p);
        return q.getResultList();
    }

    /**
     * Metodo para utilizado para retornar todas as provisõesppafonte listando
     * pelas contas de destinacao pela descrica e pelo codigo.
     *
     * @param s
     * @return todas as provisão ppa fonte
     */
    public List<ProvisaoPPAFonte> recuperaProvisaoPPAFontes(Exercicio e, String s) {
        String hql = "from ProvisaoPPAFonte obj where obj.provisaoPPADespesa.provisaoPPA.exercicio = :ex and";
        hql += " ((lower(obj.destinacaoDeRecursos.codigo) like :filtro) OR (lower(obj.destinacaoDeRecursos.descricao) like :filtro))";

        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + s + "%");
        q.setParameter("ex", e);

        return q.getResultList();
    }

//    public List<ProvisaoPPAFonte> recuperaProvisaoPPAFontePorUnidade(UnidadeOrganizacional uni) {
//        String sql = "SELECT PPAFONTE.*"
//                + " FROM PROVISAOPPAFONTE PPAFONTE"
//                + " INNER JOIN PROVISAOPPADESPESA PPADESPESA ON PPAFONTE.PROVISAOPPADESPESA_ID = PPAFONTE.ID"
//                + " INNER JOIN UNIDADEORGANIZACIONAL UNI ON PPADESPESA.UNIDADEORGANIZACIONAL_ID = UNI.ID"
//                + " WHERE UNI.ID =:unidade";
//        Query q = em.createNativeQuery(sql, ProvisaoPPAFonte.class);
//        q.setParameter("unidade", uni.getId());
//        return q.getResultList();
//    }


    public ProvisaoPPAFonte recuperarProvisaoPPAFontePorDotacaoFechamentoExercicio(DotacaoFechamentoExercicio dotacaoFechamentoExercicio) {
        String sql = " select pfont.* from dotacaofechamentoexercicio dot " +
            "inner join subacaoppa sub on dot.SUBPROJETOATIVIDADE_ID = sub.id " +
            "inner join PROVISAOPPADESPESA prov on  sub.id = prov.SUBACAOPPA_ID and prov.UNIDADEORGANIZACIONAL_ID = dot.UNIDADEORGANIZACIONAL_ID " +
            "inner join conta c on prov.CONTADEDESPESA_ID = c.id and c.id = dot.CONTA_ID " +
            "inner join PROVISAOPPAFONTE pfont on prov.id = pfont.PROVISAOPPADESPESA_ID " +
            "inner join CONTADEDESTINACAO cd on pfont.DESTINACAODERECURSOS_ID = cd.id " +
            "inner join FONTEDERECURSOS fonte on cd.FONTEDERECURSOS_ID = fonte.id and fonte.id = dot.FONTEDERECURSOS_ID and fonte.id = dot.FONTEDERECURSOS_ID " +
            "where dot.id = :idDotacao ";
        Query q = em.createNativeQuery(sql, ProvisaoPPAFonte.class);
        q.setParameter("idDotacao", dotacaoFechamentoExercicio.getId());
        if (!q.getResultList().isEmpty()) {
            return (ProvisaoPPAFonte) q.getSingleResult();
        }
        throw new ExcecaoNegocioGenerica("Provisão ppa fonte não encontrada para o fechamento do exercício.");
    }


    public ProvisaoPPAFonte recuperarProvisaoPPAFontePorDespesaFechamentoExercicio(DespesaFechamentoExercicio despeasaFechamentoExercicio) {
        String sql = " select pfont.* from despesafechamentoexercicio dot " +
            "inner join subacaoppa sub on dot.SUBPROJETOATIVIDADE_ID = sub.id " +
            "inner join PROVISAOPPADESPESA prov on  sub.id = prov.SUBACAOPPA_ID and prov.UNIDADEORGANIZACIONAL_ID = dot.UNIDADEORGANIZACIONAL_ID " +
            "inner join conta c on prov.CONTADEDESPESA_ID = c.id and c.id = dot.CONTA_ID " +
            "inner join PROVISAOPPAFONTE pfont on prov.id = pfont.PROVISAOPPADESPESA_ID " +
            "inner join CONTADEDESTINACAO cd on pfont.DESTINACAODERECURSOS_ID = cd.id " +
            "inner join FONTEDERECURSOS fonte on cd.FONTEDERECURSOS_ID = fonte.id and fonte.id = dot.FONTEDERECURSOS_ID and fonte.id = dot.FONTEDERECURSOS_ID " +
            "where dot.id = :idDespesa";
        Query q = em.createNativeQuery(sql, ProvisaoPPAFonte.class);
        q.setParameter("idDespesa", despeasaFechamentoExercicio.getId());
        if (!q.getResultList().isEmpty()) {
            return (ProvisaoPPAFonte) q.getSingleResult();
        }
        throw new ExcecaoNegocioGenerica("Provisão ppa fonte não encontrada para o fechamento do exercício.");
    }


    public List<Object[]> buscarOrcamentoInicial(Exercicio exercicio, Date dataOperacao) {
        String sql = " select ex.ano, " +
            "        substr(c.codigo, 0, 12) as contaCodigo, " +
            "        vw.codigo as orgao, " +
            "        f.codigo as funcaoCodigo, " +
            "        prog.codigo as programaCodigo, " +
            "        sf.codigo as subFuncaoCodigo, " +
            "        tpa.codigo as tipoAcaoCodigo, " +
            "        a.codigo as acaoCodigo, " +
            "        fr.codigo as fonteCodigo, " +
            "        sum(pff.valor) as valor " +
            "   FROM PROVISAOPPAFONTE PFF  " +
            "  INNER JOIN provisaoppadespesa provdesp ON PFF.PROVISAOPPADESPESA_ID = PROVDESP.ID  " +
            "  INNER join contadedestinacao cd on pff.destinacaoderecursos_id = cd.id  " +
            "  INNER join fontederecursos fr on cd.fontederecursos_id = fr.id  " +
            "  INNER JOIN CONTA C ON provdesp.CONTADEDESPESA_ID = C.ID " +
            "  INNER JOIN PLANODECONTASEXERCICIO PCE ON pce.planodedespesas_id = C.PLANODECONTAS_ID " +
            "  inner join exercicio ex on pce.exercicio_id = ex.id " +
            "  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = PROVDESP.UNIDADEORGANIZACIONAL_ID  " +
            "  INNER JOIN SUBACAOPPA SUB ON SUB.ID = provdesp.subacaoppa_id " +
            "  INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            "  INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            "  INNER JOIN programappa PROG ON PROG.ID = A.programa_id " +
            "  INNER JOIN FUNCAO F ON F.ID = A.funcao_id " +
            "  INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            "  where ex.id = :exercicio " +
            "    AND TO_DATE(:dataOperacao, 'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataOperacao, 'DD/MM/YYYY')) " +
            "  group by ex.ano, " +
            "           c.codigo, " +
            "           vw.codigo, " +
            "           f.codigo, " +
            "           prog.codigo, " +
            "           sf.codigo, " +
            "           tpa.codigo, " +
            "           a.codigo, " +
            "           fr.codigo " +
            "  order by ex.ano, " +
            "           c.codigo, " +
            "           vw.codigo, " +
            "           f.codigo, " +
            "           prog.codigo, " +
            "           sf.codigo, " +
            "           tpa.codigo, " +
            "           a.codigo, " +
            "           fr.codigo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        return q.getResultList();
    }
}
