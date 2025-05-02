package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.DemonstrativoReceitaItem;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoReceita;

import javax.ejb.Stateless;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 21/03/2017.
 */
@Stateless
public class RelatorioDemonstrativoReceitaSiopeFacade extends RelatorioContabilSuperFacade {

    public List<DemonstrativoReceitaItem> buscarDados(List<ParametrosRelatorios> parametros, Boolean pesquisouUg) {
        StringBuilder sql = new StringBuilder();
        sql.append(" WITH ITENS(ID,")
            .append("        CODIGO,")
            .append("        DESCRICAO,")
            .append("        SUPERIOR_ID,")
            .append("        tipoMovimento,")
            .append("        tipoOperacao,")
            .append("        ordemOperacao,")
            .append("        ORCADA_INICIAL,")
            .append("        ORCADA_ATUAL,")
            .append("        ARRECADADO_ATE_MES,")
            .append("        categoria) AS (")
            .append(" SELECT CONTA_ID,")
            .append("        CONTA_COD,")
            .append("        CONTA_DESC,")
            .append("        CONTA_SUPER,")
            .append("        tipoMovimento,")
            .append("        tipoOperacao,")
            .append("        ordemOperacao,")
            .append("        SUM(ORCADA_INICIAL) AS ORCADA_INICIAL,")
            .append("        SUM(ORCADA_ATUAL) as ORCADA_ATUAL,")
            .append("        SUM(ARRECADADO_ATE_MES) as ARRECADADO_ATE_MES,")
            .append("        categoria")
            .append(" FROM (")
            .append(" SELECT ")
            .append("     C.ID AS CONTA_ID,")
            .append("     C.CODIGO AS CONTA_COD,")
            .append("     C.DESCRICAO AS CONTA_DESC,")
            .append("     C.SUPERIOR_ID AS CONTA_SUPER,")
            .append("     case when ALT.operacaoreceita in ")
            .append(OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()))
            .append("        then '(-) DEDUÇÃO DA RECEITA (II)' ")
            .append("        else 'RECEITA BRUTA (I)' ")
            .append("     end AS tipoMovimento, ")
            .append("     ALT.operacaoreceita AS tipoOperacao, ")
            .append("     case ALT.operacaoreceita ")
            .append(adicionarWhenOperacaoReceitaOrdenacao())
            .append("     end AS ordemOperacao, ")
            .append("     0 as ORCADA_INICIAL, ")
            .append("     case when ALT.operacaoreceita in ")
            .append(OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()))
            .append("        then (COALESCE(DECODE(ALT.TIPOALTERACAOORC, 'PREVISAO', ALT.VALOR, ALT.VALOR * (-1)), 0)) * -1 ")
            .append("        else (COALESCE(DECODE(ALT.TIPOALTERACAOORC, 'PREVISAO', ALT.VALOR, ALT.VALOR * (-1)), 0)) ")
            .append("     end AS ORCADA_ATUAL, ")
            .append("     0 AS ARRECADADO_ATE_MES,")
            .append("     c.categoria as categoria ")
            .append("   FROM RECEITALOA REC ")
            .append("  INNER JOIN RECEITAALTERACAOORC ALT ON REC.ID = ALT.RECEITALOA_ID ")
            .append("  INNER join fontederecursos fr on alt.fontederecurso_id = fr.id ")
            .append("  INNER JOIN ALTERACAOORC ALTORC ON ALTORC.ID = ALT.ALTERACAOORC_ID ")
            .append("  INNER JOIN CONTA C ON REC.CONTADERECEITA_ID = C.ID ")
            .append("  INNER JOIN exercicio ex ON c.exercicio_id = ex.ID ")
            .append(adicionarJoinUnidadeAndOrgao("REC.ENTIDADE_ID"))
            .append(adicionarJoinUnidadeGestora(pesquisouUg))
            .append("  where ALTORC.DATAEFETIVACAO between TO_DATE('01/01/' || ex.ano, 'DD/MM/YYYY') AND TO_DATE(:DATAREFERENCIA, 'DD/MM/YYYY') ")
            .append("    and ALTORC.STATUS in ('DEFERIDO', 'ESTORNADA') ")
            .append("    and ALTORC.DATAEFETIVACAO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, ALTORC.DATAEFETIVACAO) ")
            .append("    and ALTORC.DATAEFETIVACAO BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, ALTORC.DATAEFETIVACAO) ")
            .append("    and ex.id = :exercicio ")
            .append(concatenaParametros(parametros, 1, " and "))
            .append(" UNION ALL ")
            .append(" SELECT ")
            .append("     C.ID AS CONTA_ID,")
            .append("     C.CODIGO AS CONTA_COD,")
            .append("     C.DESCRICAO AS CONTA_DESC,")
            .append("     C.SUPERIOR_ID AS CONTA_SUPER,")
            .append("     case when ALT.operacaoreceita in ")
            .append(OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()))
            .append("        then '(-) DEDUÇÃO DA RECEITA (II)' ")
            .append("        else 'RECEITA BRUTA (I)' ")
            .append("     end AS tipoMovimento, ")
            .append("     ALT.operacaoreceita AS tipoOperacao, ")
            .append("     case ALT.operacaoreceita ")
            .append(adicionarWhenOperacaoReceitaOrdenacao())
            .append("     end AS ordemOperacao, ")
            .append("     0 as ORCADA_INICIAL, ")
            .append("     case when ALT.operacaoreceita in ")
            .append(OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()))
            .append("        then (COALESCE(DECODE(ALT.TIPOALTERACAOORC, 'PREVISAO', ALT.VALOR * (-1), ALT.VALOR), 0)) *-1 ")
            .append("        else (COALESCE(DECODE(ALT.TIPOALTERACAOORC, 'PREVISAO', ALT.VALOR * (-1), ALT.VALOR), 0)) ")
            .append("     end AS ORCADA_ATUAL, ")
            .append("     0 AS ARRECADADO_NO_MES, ")
            .append("     c.categoria as categoria ")
            .append(" FROM RECEITALOA REC")
            .append(" INNER JOIN RECEITAALTERACAOORC ALT ON REC.ID = ALT.RECEITALOA_ID")
            .append(" INNER JOIN ALTERACAOORC ALTORC ON ALTORC.ID = ALT.ALTERACAOORC_ID")
            .append(" INNER JOIN ESTORNOALTERACAOORC EST ON EST.ALTERACAOORC_ID = ALTORC.ID ")
            .append(" INNER join fontederecursos fr on alt.fontederecurso_id = fr.id")
            .append(" INNER JOIN CONTA C ON REC.CONTADERECEITA_ID = C.ID ")
            .append(" INNER JOIN exercicio ex ON c.exercicio_id = ex.ID ")
            .append(adicionarJoinUnidadeAndOrgao("REC.ENTIDADE_ID"))
            .append(adicionarJoinUnidadeGestora(pesquisouUg))
            .append(" where est.dataestorno between TO_DATE('01/01/' || ex.ano, 'DD/MM/YYYY') AND TO_DATE(:DATAREFERENCIA, 'DD/MM/YYYY') ")
            .append("    and ex.id = :exercicio ")
            .append("    and est.dataestorno BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, est.dataestorno)")
            .append("    and est.dataestorno BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, est.dataestorno)")
            .append(concatenaParametros(parametros, 1, " and "))
            .append(" UNION ALL")
            .append(" SELECT")
            .append("      C.ID AS CONTA_ID,")
            .append("      C.CODIGO AS CONTA_COD,")
            .append("      C.DESCRICAO AS CONTA_DESC,")
            .append("      C.SUPERIOR_ID AS CONTA_SUPER,")
            .append("      case when RECLOA.operacaoreceita in ")
            .append(OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()))
            .append("         then '(-) DEDUÇÃO DA RECEITA (II)' ")
            .append("         else 'RECEITA BRUTA (I)' ")
            .append("      end AS tipoMovimento, ")
            .append("      RECLOA.operacaoreceita AS tipoOperacao, ")
            .append("      case RECLOA.operacaoreceita ")
            .append(adicionarWhenOperacaoReceitaOrdenacao())
            .append("      end AS ordemOperacao, ")
            .append("      case when RECLOA.operacaoreceita in ")
            .append(OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()))
            .append("         then COALESCE(SUM(RECLOAFONTE.VALOR), 0) * -1 ")
            .append("         else COALESCE(SUM(RECLOAFONTE.VALOR), 0) ")
            .append("      end as ORCADA_INICIAL, ")
            .append("      0 AS ORCADA_ATUAL, ")
            .append("      0 AS ARRECADADO_NO_MES, ")
            .append("      c.categoria as categoria ")
            .append(" FROM RECEITALOA RECLOA")
            .append(" INNER JOIN receitaloafonte RECLOAFONTE ON RECLOAFONTE.RECEITALOA_ID = RECLOA.ID")
            .append(" INNER join contadedestinacao cd on RECLOAFONTE.destinacaoderecursos_id = cd.id")
            .append(" INNER join fontederecursos fr on cd.fontederecursos_id = fr.id")
            .append(" INNER JOIN LOA ON LOA.ID = RECLOA.LOA_ID")
            .append(" INNER JOIN LDO ON LDO.ID = LOA.LDO_ID ")
            .append(" INNER JOIN CONTA C ON RECLOA.CONTADERECEITA_ID = C.ID")
            .append(adicionarJoinUnidadeAndOrgao("RECLOA.ENTIDADE_ID"))
            .append(adicionarJoinUnidadeGestora(pesquisouUg))
            .append(" WHERE LDO.EXERCICIO_ID = :exercicio ")
            .append("   AND TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:DATAREFERENCIA, 'DD/MM/YYYY'))")
            .append("   AND TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, TO_DATE(:DATAREFERENCIA, 'DD/MM/YYYY'))")
            .append(concatenaParametros(parametros, 1, " and "))
            .append(" GROUP BY C.ID, C.CODIGO, C.DESCRICAO, C.SUPERIOR_ID, c.categoria, RECLOA.operacaoreceita ")
            .append(" UNION ALL")
            .append(" SELECT")
            .append("      CONTA_ID,")
            .append("      CONTA_COD,")
            .append("      CONTA_DESC,")
            .append("      CONTA_SUPER,")
            .append("      tipoMovimento, ")
            .append("      tipoOperacao, ")
            .append("      ordemOperacao, ")
            .append("      0 AS ORCADA_INICIAL,")
            .append("      0 AS ORCADA_ATUAL,")
            .append("      COALESCE(SUM(ARRECADADO_ATE_MES), 0) AS ARRECADADO_ATE_MES,")
            .append("      categoria ")
            .append(" FROM (")
            .append("       SELECT C.ID AS CONTA_ID,")
            .append("              C.CODIGO AS CONTA_COD,")
            .append("              C.DESCRICAO AS CONTA_DESC,")
            .append("              C.SUPERIOR_ID AS CONTA_SUPER,")
            .append("              case when lanc.operacaoreceitarealizada in ")
            .append(OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()))
            .append("                 then '(-) DEDUÇÃO DA RECEITA (II)' ")
            .append("                 else 'RECEITA BRUTA (I)' ")
            .append("              end AS tipoMovimento, ")
            .append("              lanc.operacaoreceitarealizada AS tipoOperacao, ")
            .append("              case lanc.operacaoreceitarealizada ")
            .append(adicionarWhenOperacaoReceitaOrdenacao())
            .append("               end AS ordemOperacao, ")
            .append("               case when lanc.operacaoreceitarealizada in ")
            .append(OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()))
            .append("                  then COALESCE(lancrec.VALOR, 0)*-1")
            .append("                  else COALESCE(lancrec.VALOR, 0) ")
            .append("               end AS ARRECADADO_ATE_MES, ")
            .append("               c.categoria as categoria ")
            .append("       FROM CONTA C")
            .append("       INNER JOIN RECEITALOA RECLOA ON RECLOA.CONTADERECEITA_ID = C.ID")
            .append("       INNER JOIN LANCAMENTORECEITAORC LANC ON LANC.RECEITALOA_ID = RECLOA.ID")
            .append("       inner join lancReceitaFonte lancrec on lancrec.LANCRECEITAORC_ID = lanc.id")
            .append("       INNER JOIN receitaloafonte RECLOAFONTE ON RECLOAFONTE.ID = lancrec.RECEITALOAFONTE_ID")
            .append("       INNER join contadedestinacao cd on RECLOAFONTE.destinacaoderecursos_id = cd.id")
            .append("       INNER join fontederecursos fr on cd.fontederecursos_id = fr.id")
            .append(adicionarJoinUnidadeAndOrgao("RECLOA.ENTIDADE_ID"))
            .append(adicionarJoinUnidadeGestora(pesquisouUg))
            .append("  where LANC.DATALANCAMENTO between TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') AND TO_DATE(:DATAREFERENCIA, 'DD/MM/YYYY') ")
            .append("    AND LANC.DATALANCAMENTO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, LANC.DATALANCAMENTO) ")
            .append("    AND LANC.DATALANCAMENTO BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, LANC.DATALANCAMENTO) ")
            .append(concatenaParametros(parametros, 1, " and "))
            .append("   UNION ALL")
            .append("     SELECT C.ID AS CONTA_ID,")
            .append("          C.CODIGO AS CONTA_COD,")
            .append("          C.DESCRICAO AS CONTA_DESC,")
            .append("          C.SUPERIOR_ID AS CONTA_SUPER,")
            .append("          case when lanc.operacaoreceitarealizada in ")
            .append(OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()))
            .append("             then '(-) DEDUÇÃO DA RECEITA (II)' ")
            .append("             else 'RECEITA BRUTA (I)' ")
            .append("          end AS tipoMovimento, ")
            .append("          lanc.operacaoreceitarealizada AS tipoOperacao, ")
            .append("          case lanc.operacaoreceitarealizada ")
            .append(adicionarWhenOperacaoReceitaOrdenacao())
            .append("          end AS ordemOperacao, ")
            .append("          case when lanc.operacaoreceitarealizada in ")
            .append(OperacaoReceita.montarClausulaIn(OperacaoReceita.retornarOperacoesReceitaDeducao()))
            .append("             then (COALESCE(lancrecF.VALOR ,0) * -1) * -1 ")
            .append("             else COALESCE(lancrecF.VALOR ,0) * -1 ")
            .append("          end AS ARRECADADO_ATE_MES, ")
            .append("          c.categoria as categoria ")
            .append("   FROM CONTA C")
            .append("   INNER JOIN RECEITALOA RECLOA ON RECLOA.CONTADERECEITA_ID = C.ID")
            .append("   INNER JOIN RECEITAORCESTORNO LANC ON LANC.RECEITALOA_ID = RECLOA.ID ")
            .append("   INNER JOIN ReceitaORCFonteEstorno lancrecF      ON lancrecf.receitaorcestorno_id = LANC.id  ")
            .append("   INNER JOIN receitaloafonte RECLOAFONTE      ON RECLOAFONTE.ID = lancrecF.RECEITALOAFONTE_ID ")
            .append("   INNER join contadedestinacao cd on RECLOAFONTE.destinacaoderecursos_id = cd.id")
            .append("   INNER join fontederecursos fr on cd.fontederecursos_id = fr.id")
            .append(adicionarJoinUnidadeAndOrgao("RECLOA.ENTIDADE_ID"))
            .append(adicionarJoinUnidadeGestora(pesquisouUg))
            .append("   where LANC.DATAESTORNO between TO_DATE(:DATAINICIAL, 'DD/MM/YYYY') AND TO_DATE(:DATAREFERENCIA, 'DD/MM/YYYY') ")
            .append("     AND LANC.DATAESTORNO BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, LANC.DATAESTORNO) ")
            .append("     AND LANC.DATAESTORNO BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, LANC.DATAESTORNO) ")
            .append(concatenaParametros(parametros, 1, " and "))
            .append("  )ARRECADADO_ATE_MES")
            .append("  GROUP BY CONTA_ID, CONTA_COD, CONTA_DESC, CONTA_SUPER, tipoMovimento, tipoOperacao, ordemOperacao, categoria ")
            .append("  ) DADOS")
            .append("  GROUP BY CONTA_ID,")
            .append("        CONTA_COD,")
            .append("        CONTA_DESC,")
            .append("        CONTA_SUPER,")
            .append("        tipoMovimento,")
            .append("        tipoOperacao,")
            .append("        ordemOperacao, ")
            .append("        categoria ")
            .append("  UNION ALL")
            .append(" SELECT SUP.ID,")
            .append("        SUP.CODIGO,")
            .append("        SUP.DESCRICAO,")
            .append("        SUP.SUPERIOR_ID,")
            .append("        IT.tipoMovimento,")
            .append("        IT.tipoOperacao,")
            .append("        IT.ordemOperacao,")
            .append("        IT.ORCADA_INICIAL,")
            .append("        IT.ORCADA_ATUAL,")
            .append("        IT.ARRECADADO_ATE_MES,")
            .append("        SUP.categoria")
            .append("   FROM ITENS IT")
            .append("  INNER JOIN CONTA SUP ON IT.SUPERIOR_ID = SUP.ID")
            .append("   ) ")
            .append("   SELECT codigo,")
            .append("        descricao,")
            .append("        tipoMovimento,")
            .append("        tipoOperacao,")
            .append("        COALESCE(SUM(ORCADA_INICIAL), 0) AS orcadaInicial,")
            .append("        COALESCE(SUM(ORCADA_INICIAL), 0) + COALESCE(SUM(ORCADA_ATUAL), 0 ) AS orcadaAtual,")
            .append("        COALESCE(sum(ARRECADADO_ATE_MES), 0) AS arrecadadoAteMes,")
            .append("        NIVELESTRUTURA(CODIGO, '.') as nivel,")
            .append("        categoria")
            .append("    FROM ITENS")
            .append("    GROUP BY CODIGO, ")
            .append("             DESCRICAO, ")
            .append("             categoria, ")
            .append("             tipoMovimento, ")
            .append("             tipoOperacao, ")
            .append("             ordemOperacao ")
            .append("   ORDER BY ordemOperacao, ")
            .append("            CODIGO,  ")
            .append("            DESCRICAO ");

        Query q = getEm().createNativeQuery(sql.toString());
        adicionaParametros(parametros, q);
        List<Object[]> resultado = (List<Object[]>) q.getResultList();
        List<DemonstrativoReceitaItem> retorno = new ArrayList<>();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                DemonstrativoReceitaItem demonstrativoReceitaItem = new DemonstrativoReceitaItem();
                demonstrativoReceitaItem.setCodigo((String) obj[0]);
                demonstrativoReceitaItem.setDescricao((String) obj[1]);
                demonstrativoReceitaItem.setTipoMovimento((String) obj[2]);
                if (obj[3] != null) {
                    demonstrativoReceitaItem.setTipoOperacao(OperacaoReceita.valueOf((String) obj[3]).getDescricao());
                }
                demonstrativoReceitaItem.setOrcadaInicial((BigDecimal) obj[4]);
                demonstrativoReceitaItem.setOrcadaAtual((BigDecimal) obj[5]);
                demonstrativoReceitaItem.setArrecadadoAteMes((BigDecimal) obj[6]);
                demonstrativoReceitaItem.setNivel((BigDecimal) obj[7]);
                demonstrativoReceitaItem.setCategoria((String) obj[8]);
                if (hasValorParaExibir(demonstrativoReceitaItem)) {
                    retorno.add(demonstrativoReceitaItem);
                }
            }
        }
        return retorno;
    }

    private StringBuilder adicionarWhenOperacaoReceitaOrdenacao() {
        StringBuilder sb = new StringBuilder();
        sb.append("   WHEN 'RECEITA_CREDITOS_RECEBER_BRUTA' then 1 ")
            .append(" WHEN 'RECEITA_DIRETAMENTE_ARRECADADA_BRUTA' then 2 ")
            .append(" WHEN 'RECEITA_DIVIDA_ATIVA_BRUTA' then 3 ")
            .append(" WHEN 'DEDUCAO_RECEITA_RENUNCIA' then 4 ")
            .append(" WHEN 'DEDUCAO_RECEITA_RESTITUICAO' then 5 ")
            .append(" WHEN 'DEDUCAO_RECEITA_DESCONTO' then 6 ")
            .append(" WHEN 'DEDUCAO_RECEITA_FUNDEB' then 7 ")
            .append(" WHEN 'DEDUCAO_RECEITA_OUTRAS' then 8 ");
        return sb;
    }

    private String adicionarJoinUnidadeAndOrgao(String campoUnidade) {
        return "  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = " + campoUnidade +
            "  INNER JOIN VWHIERARQUIAORCAMENTARIA VWORG ON VWORG.SUBORDINADA_ID = VW.SUPERIOR_ID ";
    }

    private String adicionarJoinUnidadeGestora(Boolean pesquisouUg) {
        return pesquisouUg ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "";
    }

    private boolean hasValorParaExibir(DemonstrativoReceitaItem item) {
        return item.getOrcadaInicial().compareTo(BigDecimal.ZERO) != 0 ||
            item.getOrcadaAtual().compareTo(BigDecimal.ZERO) != 0 ||
            item.getArrecadadoAteMes().compareTo(BigDecimal.ZERO) != 0;
    }
}
