/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.FormulaItemDemonstrativoDTO;
import br.com.webpublico.contabil.relatorioitemdemonstrativo.dto.ItemDemonstrativoFiltrosDTO;
import br.com.webpublico.contabil.relatorioitemdemonstrativo.dto.TipoAlteracaoORCDTO;
import br.com.webpublico.contabil.relatorioitemdemonstrativo.dto.TipoRestosProcessadoDTO;
import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.contabil.relatorioitemdemonstrativo.util.UtilRelatorioContabil;
import br.com.webpublico.negocios.contabil.RelatorioItemDemonstrativoCalculador;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.contabil.CategoriaOrcamentariaDTO;
import br.com.webpublico.webreportdto.dto.contabil.OperacaoReceitaDTO;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.ejb.Stateless;
import java.math.BigDecimal;

/**
 * @author major
 */
@Stateless
public class RelatorioAnexoDozeCalculator extends RelatorioItemDemonstrativoCalculador {

    @Override
    protected BigDecimal buscarValor(FormulaItemDemonstrativoDTO formulaItemDemonstrativoDTO, ItemDemonstrativoFiltrosDTO filtros, TipoCalculoItemDemonstrativo tipoCalculo) {
        switch (tipoCalculo) {
            case RESTOS_A_PAGAR_CANCELADOS:
                return buscarRestosAPagarCancelados(formulaItemDemonstrativoDTO, filtros, CategoriaOrcamentariaDTO.RESTO, TipoRestosProcessadoDTO.NAO_PROCESSADOS);
            case RESTOS_PAGOS_ATE_BIMESTRE:
                return buscarDespesasPagasAteOBimestrePorCategoria(formulaItemDemonstrativoDTO, filtros, CategoriaOrcamentariaDTO.RESTO, TipoRestosProcessadoDTO.PROCESSADOS);
            case RESTOS_A_PAGAR_NAO_PROCESSADOS:
                return buscarRestosAPagarPorCategoriaOrcamentaria(formulaItemDemonstrativoDTO, filtros, CategoriaOrcamentariaDTO.RESTO, TipoRestosProcessadoDTO.NAO_PROCESSADOS);
            case RESTOS_A_PAGAR_PROCESSADOS:
                return buscarRestosAPagarPorCategoriaOrcamentaria(formulaItemDemonstrativoDTO, filtros, CategoriaOrcamentariaDTO.RESTO, TipoRestosProcessadoDTO.PROCESSADOS);
            default:
                return super.buscarValor(formulaItemDemonstrativoDTO, filtros, tipoCalculo);
        }
    }

    @Override
    public BigDecimal buscarPrevisaoInicial(FormulaItemDemonstrativoDTO formulaItemDemonstrativo, ItemDemonstrativoFiltrosDTO filtros) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT COALESCE(SUM(")
            .append(" case ")
            .append("    when rec.operacaoReceita in ")
            .append(OperacaoReceitaDTO.montarClausulaIn(OperacaoReceitaDTO.retornarOperacoesReceitaDeducao()))
            .append(" then COALESCE(").append(filtros.getRelatorio().getUsaFonteRecurso() ? "rlf.VALOR" : "rec.VALOR").append(", 0) * - 1  ")
            .append(" else COALESCE(").append(filtros.getRelatorio().getUsaFonteRecurso() ? "rlf.VALOR" : "rec.VALOR").append(", 0) ")
            .append(" end ")
            .append(" ),0) ")
            .append(" FROM RECEITALOA rec ");
        if (filtros.getRelatorio().getUsaFonteRecurso()) {
            sql.append(" INNER JOIN ReceitaLOAFonte rlf ON rec.ID = RLF.RECEITALOA_ID ")
                .append(" INNER JOIN ContaDeDestinacao cd ON cd.ID = RLF.DESTINACAODERECURSOS_ID ")
                .append(" INNER JOIN FONTEDERECURSOS font ON cd.fonteDeRecursos_id = font.id ")
                .append(adicionarFontesPelaFormula(formulaItemDemonstrativo, filtros));
        }
        sql.append(" inner join unidadeorganizacional und on rec.entidade_id = und.id ")
            .append(adicionarUnidadesPelaFormula(formulaItemDemonstrativo, filtros))
            .append(adicionarUnidadeGestora(filtros))
            .append(" INNER JOIN CONTA c ON rec.CONTADERECEITA_ID = c.ID ")
            .append(adicionarContaPelaFormula(formulaItemDemonstrativo, filtros, "ContaReceita"))
            .append(" INNER JOIN LOA loa ON rec.LOA_ID = loa.ID ")
            .append(" INNER JOIN LDO ldo ON loa.LDO_ID = ldo.ID ")
            .append(" INNER JOIN EXERCICIO E ON ldo.EXERCICIO_ID = E.ID ")
            .append(" WHERE e.id = :exercicio ")
            .append(adicionarParametros(filtros));
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("exercicio", filtros.getExercicioId());
        if (filtros.getParametros() != null) {
            UtilRelatorioContabil.adicionarParametros(filtros.getParametros(), parameters);
        }
        return retornarValor(sql.toString(), parameters);
    }

    @Override
    protected BigDecimal buscarPrevisaoAtualizada(FormulaItemDemonstrativoDTO formulaItemDemonstrativoDTO, ItemDemonstrativoFiltrosDTO filtros) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT coalesce(SUM(VALOR),0) AS VALOR FROM ( ")
            .append(" SELECT case ")
            .append("    when re.operacaoReceita in ")
            .append(OperacaoReceitaDTO.montarClausulaIn(OperacaoReceitaDTO.retornarOperacoesReceitaDeducao()))
            .append(" then COALESCE(").append(filtros.getRelatorio().getUsaFonteRecurso() ? "RE.VALOR" : "RE.VALOR").append(", 0) * - 1  ")
            .append(" else COALESCE(").append(filtros.getRelatorio().getUsaFonteRecurso() ? "RE.VALOR" : "RE.VALOR").append(", 0) ")
            .append(" end AS valor ")
            .append(" FROM ALTERACAOORC ALT  ")
            .append(" INNER JOIN RECEITAALTERACAOORC RE ON ALT.ID = RE.ALTERACAOORC_ID  ")
            .append(" INNER JOIN Conta cd ON cd.ID = re.contaDeDestinacao_id ")
            .append(adicionarContaPelaFormula(formulaItemDemonstrativoDTO, filtros, "ContaDeDestinacao", "cd"))
            .append(" INNER JOIN RECEITALOA RLOA ON RE.RECEITALOA_ID = RLOA.ID  ")
            .append(" INNER JOIN FONTEDERECURSOS font ON RE.FONTEDERECURSO_ID = font.id ")
            .append(adicionarFontesPelaFormula(formulaItemDemonstrativoDTO, filtros))
            .append(" inner join unidadeorganizacional und on RLOA.entidade_id = und.id ")
            .append(adicionarUnidadesPelaFormula(formulaItemDemonstrativoDTO, filtros))
            .append(adicionarUnidadeGestora(filtros))
            .append(" INNER JOIN CONTA c ON RLOA.CONTADERECEITA_ID = c.ID ")
            .append(adicionarContaPelaFormula(formulaItemDemonstrativoDTO, filtros, "ContaReceita"))
            .append(" INNER JOIN LOA L ON RLOA.LOA_ID = L.ID  ")
            .append(" INNER JOIN LDO LD ON L.LDO_ID = LD.ID  ")
            .append(" INNER JOIN EXERCICIO E ON LD.EXERCICIO_ID = E.ID  ")
            .append(" WHERE e.id = :exercicio  ")
            .append(" AND RE.TIPOALTERACAOORC = :previsao ")
            .append(" and trunc(ALT.DATAEFETIVACAO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(adicionarParametros(filtros))
            .append(" union all ")
            .append(" SELECT case ")
            .append("    when re.operacaoReceita in ")
            .append(OperacaoReceitaDTO.montarClausulaIn(OperacaoReceitaDTO.retornarOperacoesReceitaDeducao()))
            .append(" then COALESCE(").append(filtros.getRelatorio().getUsaFonteRecurso() ? "RE.VALOR" : "RE.VALOR").append(", 0) ")
            .append(" else COALESCE(").append(filtros.getRelatorio().getUsaFonteRecurso() ? "RE.VALOR" : "RE.VALOR").append(", 0) * - 1")
            .append(" end AS valor ")
            .append(" FROM ALTERACAOORC ALT  ")
            .append(" INNER JOIN RECEITAALTERACAOORC RE ON ALT.ID = RE.ALTERACAOORC_ID  ")
            .append(" INNER JOIN Conta cd ON cd.ID = re.contaDeDestinacao_id ")
            .append(adicionarContaPelaFormula(formulaItemDemonstrativoDTO, filtros, "ContaDeDestinacao", "cd"))
            .append(" INNER JOIN RECEITALOA RLOA ON RE.RECEITALOA_ID = RLOA.ID  ")
            .append(" INNER JOIN FONTEDERECURSOS font ON RE.FONTEDERECURSO_ID = font.id ")
            .append(adicionarFontesPelaFormula(formulaItemDemonstrativoDTO, filtros))
            .append(" inner join unidadeorganizacional und on RLOA.entidade_id = und.id ")
            .append(adicionarUnidadesPelaFormula(formulaItemDemonstrativoDTO, filtros))
            .append(adicionarUnidadeGestora(filtros))
            .append(" INNER JOIN CONTA c ON RLOA.CONTADERECEITA_ID = c.ID ")
            .append(adicionarContaPelaFormula(formulaItemDemonstrativoDTO, filtros, "ContaReceita"))
            .append(" INNER JOIN LOA L ON RLOA.LOA_ID = L.ID  ")
            .append(" INNER JOIN LDO LD ON L.LDO_ID = LD.ID  ")
            .append(" INNER JOIN EXERCICIO E ON LD.EXERCICIO_ID = E.ID  ")
            .append(" WHERE e.id = :exercicio  ")
            .append("  AND RE.TIPOALTERACAOORC = :anulacao ")
            .append("  and trunc(ALT.DATAEFETIVACAO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(adicionarParametros(filtros))
            .append(" union all ")
            .append(" SELECT case ")
            .append("    when re.operacaoReceita in ")
            .append(OperacaoReceitaDTO.montarClausulaIn(OperacaoReceitaDTO.retornarOperacoesReceitaDeducao()))
            .append(" then COALESCE(").append(filtros.getRelatorio().getUsaFonteRecurso() ? "RE.VALOR" : "RE.VALOR").append(", 0) ")
            .append(" else COALESCE(").append(filtros.getRelatorio().getUsaFonteRecurso() ? "RE.VALOR" : "RE.VALOR").append(", 0) * - 1")
            .append(" end AS valor ")
            .append("  FROM EstornoAlteracaoOrc est ")
            .append(" INNER JOIN ALTERACAOORC ALT ON EST.alteracaoORC_ID = ALT.ID ")
            .append(" INNER JOIN RECEITAALTERACAOORC RE ON ALT.ID = RE.ALTERACAOORC_ID  ")
            .append(" INNER JOIN Conta cd ON cd.ID = re.contaDeDestinacao_id ")
            .append(adicionarContaPelaFormula(formulaItemDemonstrativoDTO, filtros, "ContaDeDestinacao", "cd"))
            .append(" INNER JOIN RECEITALOA RLOA ON RE.RECEITALOA_ID = RLOA.ID  ")
            .append(" INNER JOIN FONTEDERECURSOS font ON RE.FONTEDERECURSO_ID = font.id ")
            .append(adicionarFontesPelaFormula(formulaItemDemonstrativoDTO, filtros))
            .append(" inner join unidadeorganizacional und on RLOA.entidade_id = und.id ")
            .append(adicionarUnidadesPelaFormula(formulaItemDemonstrativoDTO, filtros))
            .append(adicionarUnidadeGestora(filtros))
            .append(" INNER JOIN CONTA c ON RLOA.CONTADERECEITA_ID = c.ID ")
            .append(adicionarContaPelaFormula(formulaItemDemonstrativoDTO, filtros, "ContaReceita"))
            .append(" INNER JOIN LOA L ON RLOA.LOA_ID = L.ID  ")
            .append(" INNER JOIN LDO LD ON L.LDO_ID = LD.ID  ")
            .append(" INNER JOIN EXERCICIO E ON LD.EXERCICIO_ID = E.ID  ")
            .append(" WHERE e.id = :exercicio  ")
            .append("  AND RE.TIPOALTERACAOORC = :previsao ")
            .append("  and trunc(ALT.DATAEFETIVACAO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(adicionarParametros(filtros))
            .append(" union all ")
            .append(" SELECT case ")
            .append("    when re.operacaoReceita in ")
            .append(OperacaoReceitaDTO.montarClausulaIn(OperacaoReceitaDTO.retornarOperacoesReceitaDeducao()))
            .append(" then COALESCE(").append(filtros.getRelatorio().getUsaFonteRecurso() ? "RE.VALOR" : "RE.VALOR").append(", 0) * - 1  ")
            .append(" else COALESCE(").append(filtros.getRelatorio().getUsaFonteRecurso() ? "RE.VALOR" : "RE.VALOR").append(", 0) ")
            .append(" end AS valor ")
            .append("  FROM EstornoAlteracaoOrc est ")
            .append(" INNER JOIN ALTERACAOORC ALT ON EST.alteracaoORC_ID = ALT.ID ")
            .append(" INNER JOIN RECEITAALTERACAOORC RE ON ALT.ID = RE.ALTERACAOORC_ID  ")
            .append(" INNER JOIN Conta cd ON cd.ID = re.contaDeDestinacao_id ")
            .append(adicionarContaPelaFormula(formulaItemDemonstrativoDTO, filtros, "ContaDeDestinacao", "cd"))
            .append(" INNER JOIN RECEITALOA RLOA ON RE.RECEITALOA_ID = RLOA.ID  ")
            .append(" INNER JOIN FONTEDERECURSOS font ON RE.FONTEDERECURSO_ID = font.id ")
            .append(adicionarFontesPelaFormula(formulaItemDemonstrativoDTO, filtros))
            .append(" inner join unidadeorganizacional und on RLOA.entidade_id = und.id ")
            .append(adicionarUnidadesPelaFormula(formulaItemDemonstrativoDTO, filtros))
            .append(adicionarUnidadeGestora(filtros))
            .append(" INNER JOIN CONTA c ON RLOA.CONTADERECEITA_ID = c.ID ")
            .append(adicionarContaPelaFormula(formulaItemDemonstrativoDTO, filtros, "ContaReceita"))
            .append(" INNER JOIN LOA L ON RLOA.LOA_ID = L.ID  ")
            .append(" INNER JOIN LDO LD ON L.LDO_ID = LD.ID  ")
            .append(" INNER JOIN EXERCICIO E ON LD.EXERCICIO_ID = E.ID  ")
            .append(" WHERE e.id = :exercicio  ")
            .append("  AND RE.TIPOALTERACAOORC = :anulacao ")
            .append("  and trunc(ALT.DATAEFETIVACAO) BETWEEN TO_DATE(:DATAINICIAL, 'dd/MM/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append(adicionarParametros(filtros))
            .append(" ) dados ");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("exercicio", filtros.getExercicioId());
        parameters.addValue("DATAINICIAL", filtros.getDataInicial());
        parameters.addValue("DATAFINAL", filtros.getDataFinal());
        parameters.addValue("previsao", TipoAlteracaoORCDTO.PREVISAO.name());
        parameters.addValue("anulacao", TipoAlteracaoORCDTO.ANULACAO.name());
        if (filtros.getParametros() != null) {
            UtilRelatorioContabil.adicionarParametros(filtros.getParametros(), parameters);
        }
        return retornarValor(sql.toString(), parameters);
    }

    @Override
    protected BigDecimal buscarReceitaRealizadaAteOBimestre(FormulaItemDemonstrativoDTO formulaItemDemonstrativo, ItemDemonstrativoFiltrosDTO filtros) {
        StringBuilder sql = new StringBuilder();
        sql.append("  SELECT coalesce(sum(VALOR), 0) FROM ( ")
            .append(" select case  ")
            .append("    when lr.operacaoReceitaRealizada in ")
            .append(OperacaoReceitaDTO.montarClausulaIn(OperacaoReceitaDTO.retornarOperacoesReceitaDeducao()))
            .append(" then COALESCE(").append(filtros.getRelatorio().getUsaFonteRecurso() ? "lrf.VALOR" : "lr.VALOR").append(", 0) * - 1  ")
            .append(" else COALESCE(").append(filtros.getRelatorio().getUsaFonteRecurso() ? "lrf.VALOR" : "lr.VALOR").append(", 0) ")
            .append(" end AS valor ")
            .append("   FROM LANCAMENTORECEITAORC LR ")
            .append("  INNER JOIN RECEITALOA RE ON LR.RECEITALOA_ID = RE.ID ")
            .append("  INNER JOIN LOA L ON RE.LOA_ID = L.ID ")
            .append("  INNER JOIN LDO LD ON L.LDO_ID = LD.ID ")
            .append("  INNER JOIN EXERCICIO e ON LD.EXERCICIO_ID = E.ID ");
        if (filtros.getRelatorio().getUsaFonteRecurso()) {
            sql.append("  inner join lancreceitafonte lrf on lrf.lancreceitaorc_id = lr.id ")
                .append(" INNER JOIN ReceitaLOAFonte rlf ON lrf.receitaloafonte_id = RLF.id ")
                .append(" INNER JOIN ContaDeDestinacao cdd ON cdd.ID = RLF.DESTINACAODERECURSOS_ID ")
                .append(" INNER JOIN Conta cd ON cd.ID = cdd.id ")
                .append(adicionarContaPelaFormula(formulaItemDemonstrativo, filtros, "ContaDeDestinacao", "cd"))
                .append(" INNER JOIN FONTEDERECURSOS font ON cdd.fonteDeRecursos_id = font.id ")
                .append(adicionarFontesPelaFormula(formulaItemDemonstrativo, filtros));
        }
        sql.append(" inner join unidadeorganizacional und on RE.entidade_id = und.id ")
            .append(adicionarUnidadesPelaFormula(formulaItemDemonstrativo, filtros))
            .append(buscarJoinVwUnidade("RE.entidade_id"))
            .append(adicionarUnidadeGestora(filtros))
            .append(" INNER JOIN CONTA c ON  RE.CONTADERECEITA_ID = c.ID ")
            .append(adicionarContaPelaFormula(formulaItemDemonstrativo, filtros, "ContaReceita"))
            .append(" inner join subconta sub on lr.subconta_id = sub.id ")
            .append(adicionarSubContaPelaFormula(formulaItemDemonstrativo, filtros))
            .append(" WHERE trunc(LR.DATALANCAMENTO) BETWEEN to_date('01/01/' || e.ano, 'dd/mm/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy')  ")
            .append("   AND e.id = :exercicio ")
            .append(getAndVigenciaVwUnidade("trunc(LR.DATALANCAMENTO)"))
            .append(adicionarParametros(filtros))
            .append(" union all ")
            .append(" SELECT ")
            .append(" case  ")
            .append("    when LRE.operacaoReceitaRealizada in ")
            .append(OperacaoReceitaDTO.montarClausulaIn(OperacaoReceitaDTO.retornarOperacoesReceitaDeducao()))
            .append(" then COALESCE(").append(filtros.getRelatorio().getUsaFonteRecurso() ? "recorcfonte.VALOR" : "lre.VALOR").append(", 0) ")
            .append(" else COALESCE(").append(filtros.getRelatorio().getUsaFonteRecurso() ? "recorcfonte.VALOR" : "lre.VALOR").append(", 0) * - 1 ")
            .append(" end as valor ")
            .append("FROM RECEITAORCESTORNO LRE ")
            .append(" INNER JOIN RECEITALOA RE ON LRE.RECEITALOA_ID = RE.ID ")
            .append(" INNER JOIN LOA L ON RE.LOA_ID = L.ID ")
            .append(" INNER JOIN LDO LD ON L.LDO_ID = LD.ID ")
            .append(" INNER JOIN EXERCICIO e ON LD.EXERCICIO_ID = E.ID ");
        if (filtros.getRelatorio().getUsaFonteRecurso()) {
            sql.append("  INNER JOIN ReceitaORCFonteEstorno recorcfonte ON recorcfonte.receitaorcestorno_id = LRE.id ")
                .append(" INNER JOIN ReceitaLOAFonte lr ON recorcfonte.receitaloafonte_id = lr.ID ")
                .append(" INNER JOIN ContaDeDestinacao cdd ON cdd.ID = lr.DESTINACAODERECURSOS_ID ")
                .append(" INNER JOIN Conta cd ON cd.ID = cdd.id ")
                .append(adicionarContaPelaFormula(formulaItemDemonstrativo, filtros, "ContaDeDestinacao", "cd"))
                .append(" INNER JOIN FONTEDERECURSOS font ON cdd.fonteDeRecursos_id = font.id ")
                .append(adicionarFontesPelaFormula(formulaItemDemonstrativo, filtros));
        }
        sql.append(" inner join unidadeorganizacional und on RE.entidade_id = und.id ")
            .append(adicionarUnidadesPelaFormula(formulaItemDemonstrativo, filtros))
            .append(buscarJoinVwUnidade("RE.entidade_id"))
            .append(adicionarUnidadeGestora(filtros))
            .append(" INNER JOIN CONTA c ON  RE.CONTADERECEITA_ID = c.ID ")
            .append(adicionarContaPelaFormula(formulaItemDemonstrativo, filtros, "ContaReceita"))
            .append(" inner join subconta sub on LRE.contafinanceira_id = sub.id ")
            .append(adicionarSubContaPelaFormula(formulaItemDemonstrativo, filtros))
            .append(" WHERE trunc(LRE.DATAESTORNO) BETWEEN to_date('01/01/' || e.ano, 'dd/mm/yyyy') AND TO_DATE(:DATAFINAL, 'dd/MM/yyyy') ")
            .append(adicionarParametros(filtros))
            .append(getAndVigenciaVwUnidade("trunc(LRE.DATAESTORNO)"))
            .append(" AND e.id = :exercicio) dados ");
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("exercicio", filtros.getExercicioId());
        parameters.addValue("DATAFINAL", filtros.getDataFinal());
        if (filtros.getParametros() != null) {
            UtilRelatorioContabil.adicionarParametros(filtros.getParametros(), parameters);
        }
        return retornarValor(sql.toString(), parameters);
    }
}
