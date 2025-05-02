package br.com.webpublico.relatoriofacade;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.FormulaItemDemonstrativoDTO;
import br.com.webpublico.contabil.relatorioitemdemonstrativo.dto.ItemDemonstrativoFiltrosDTO;
import br.com.webpublico.contabil.relatorioitemdemonstrativo.dto.TipoRestosProcessadoDTO;
import br.com.webpublico.contabil.relatorioitemdemonstrativo.util.UtilRelatorioContabil;
import br.com.webpublico.negocios.contabil.RelatorioItemDemonstrativoCalculador;
import br.com.webpublico.webreportdto.dto.contabil.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.ejb.Stateless;
import java.math.BigDecimal;

/**
 * Created by mateus on 24/07/17.
 */
@Stateless
public class RelatorioRREOAnexo08Facade extends RelatorioItemDemonstrativoCalculador {

    @Override
    protected BigDecimal buscarDespesasPagasAteOBimestrePorCategoria(FormulaItemDemonstrativoDTO formulaItemDemonstrativo, ItemDemonstrativoFiltrosDTO filtros, CategoriaOrcamentariaDTO categoriaOrcamentaria, TipoRestosProcessadoDTO tipoRestosProcessado) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT sum(valor) from ( ")
            .append(" select COALESCE(sum(pag.valorfinal), 0) AS valor FROM pagamento pag ")
            .append("  inner join liquidacao liq on pag.liquidacao_id = liq.id ")
            .append("  inner join empenho emp on liq.empenho_id = emp.id  ");
        sql.append(buscarJoinsComunsParaEmpenhoLiquidacaoPagamento(formulaItemDemonstrativo, filtros, "pag", categoriaOrcamentaria));
        sql.append(" inner join subconta sub on pag.subconta_id = sub.id ")
            .append(adicionarSubContaPelaFormula(formulaItemDemonstrativo, filtros))
            .append(" WHERE trunc(pag.datapagamento) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(getAndVigenciaVwUnidade("trunc(pag.datapagamento)"))
            .append(CategoriaOrcamentariaDTO.NORMAL.equals(categoriaOrcamentaria) ? " and desp.EXERCICIO_ID = :EXERCICIO " : "")
            .append(tipoRestosProcessado != null ? " and emp.tiporestosprocessados  = :tipoResto " : "")
            .append(categoriaOrcamentaria != null ? " and emp.categoriaorcamentaria = :categoria and liq.categoriaorcamentaria = :categoria and pag.categoriaorcamentaria = :categoria " : "")
            .append(" and pag.status <> :aberto ")
            .append(adicionarParametros(filtros))
            .append(" union all ")
            .append(" SELECT COALESCE(sum(est.valorfinal), 0) * - 1 AS valor FROM pagamentoestorno est ")
            .append(" inner join pagamento pag on est.pagamento_id = pag.id")
            .append(" inner join liquidacao liq on pag.liquidacao_id = liq.id")
            .append(" inner join empenho emp on liq.empenho_id = emp.id ");
        sql.append(buscarJoinsComunsParaEmpenhoLiquidacaoPagamento(formulaItemDemonstrativo, filtros, "est", categoriaOrcamentaria));
        sql.append(" inner join subconta sub on pag.subconta_id = sub.id ")
            .append(adicionarSubContaPelaFormula(formulaItemDemonstrativo, filtros))
            .append(" WHERE trunc(est.dataestorno)  between to_date(:DATAINICIAL, 'dd/MM/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(CategoriaOrcamentariaDTO.NORMAL.equals(categoriaOrcamentaria) ? " and desp.EXERCICIO_ID = :EXERCICIO " : "")
            .append(getAndVigenciaVwUnidade("trunc(est.dataestorno)"))
            .append(tipoRestosProcessado != null ? " and emp.tiporestosprocessados  = :tipoResto " : "")
            .append(categoriaOrcamentaria != null ? " and emp.categoriaorcamentaria = :categoria and liq.categoriaorcamentaria = :categoria and pag.categoriaorcamentaria = :categoria " : "")
            .append("   and pag.status <> :aberto ")
            .append(adicionarParametros(filtros))
            .append(" union all ")
            .append(" select COALESCE(sum(rec.valor), 0) AS valor FROM receitaextra rec ")
            .append(" inner join RETENCAOPGTO ret on rec.RETENCAOPGTO_ID = ret.id ")
            .append(" inner join pagamento pag on ret.PAGAMENTO_ID = pag.id  ")
            .append(" inner join liquidacao liq on pag.liquidacao_id = liq.id ")
            .append(" inner join empenho emp on liq.empenho_id = emp.id  ");
        sql.append(buscarJoinsComunsParaEmpenhoLiquidacaoPagamento(formulaItemDemonstrativo, filtros, "rec", categoriaOrcamentaria));
        sql.append(" inner join subconta sub on pag.subconta_id = sub.id ")
            .append(adicionarSubContaPelaFormula(formulaItemDemonstrativo, filtros))
            .append(" WHERE trunc(rec.DATARECEITA) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy')")
            .append(CategoriaOrcamentariaDTO.NORMAL.equals(categoriaOrcamentaria) ? " and desp.EXERCICIO_ID = :EXERCICIO " : "")
            .append(getAndVigenciaVwUnidade("trunc(rec.DATARECEITA)"))
            .append(tipoRestosProcessado != null ? " and emp.tiporestosprocessados  = :tipoResto " : "")
            .append(categoriaOrcamentaria != null ? " and emp.categoriaorcamentaria = :categoria and liq.categoriaorcamentaria = :categoria and pag.categoriaorcamentaria = :categoria " : "")
            .append(" and pag.status <> :aberto ")
            .append(adicionarParametros(filtros))
            .append(" union all ")
            .append(" select COALESCE(sum(est.valor), 0) * - 1 AS valor FROM receitaextraestorno est ")
            .append(" inner join receitaextra rec on est.RECEITAEXTRA_ID = rec.id ")
            .append(" inner join RETENCAOPGTO ret on rec.RETENCAOPGTO_ID = ret.id ")
            .append(" inner join pagamento pag on ret.PAGAMENTO_ID = pag.id ")
            .append(" inner join liquidacao liq on pag.liquidacao_id = liq.id ")
            .append(" inner join empenho emp on liq.empenho_id = emp.id  ");
        sql.append(buscarJoinsComunsParaEmpenhoLiquidacaoPagamento(formulaItemDemonstrativo, filtros, "est", categoriaOrcamentaria));
        sql.append(" inner join subconta sub on pag.subconta_id = sub.id ")
            .append(adicionarSubContaPelaFormula(formulaItemDemonstrativo, filtros))
            .append(" WHERE trunc(est.DATAESTORNO) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(CategoriaOrcamentariaDTO.NORMAL.equals(categoriaOrcamentaria) ? " and desp.EXERCICIO_ID = :EXERCICIO " : "")
            .append(getAndVigenciaVwUnidade("trunc(est.DATAESTORNO)"))
            .append(tipoRestosProcessado != null ? " and emp.tiporestosprocessados  = :tipoResto " : "")
            .append(categoriaOrcamentaria != null ? " and emp.categoriaorcamentaria = :categoria and liq.categoriaorcamentaria = :categoria and pag.categoriaorcamentaria = :categoria " : "")
            .append(" and pag.status <> :aberto ")
            .append(adicionarParametros(filtros))
            .append("  UNION ALL  ")
            .append(" select COALESCE(sum(pagext.valor), 0) * -1 AS valor FROM receitaextra rec ")
            .append(" inner join RETENCAOPGTO ret on rec.RETENCAOPGTO_ID = ret.id ")
            .append(" inner join pagamento pag on ret.PAGAMENTO_ID = pag.id  ")
            .append(" inner join PagamentoReceitaExtra pr on rec.id = pr.receitaextra_id ")
            .append(" inner join pagamentoextra pagext on pr.PAGAMENTOEXTRA_ID = pagext.id  ")
            .append(" inner join liquidacao liq on pag.liquidacao_id = liq.id ")
            .append(" inner join empenho emp on liq.empenho_id = emp.id  ");
        sql.append(buscarJoinsComunsParaEmpenhoLiquidacaoPagamento(formulaItemDemonstrativo, filtros, "rec", categoriaOrcamentaria));
        sql.append(" inner join subconta sub on pag.subconta_id = sub.id ")
            .append(adicionarSubContaPelaFormula(formulaItemDemonstrativo, filtros))
            .append(" WHERE trunc(pagext.DATAPAGTO) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy')")
            .append(CategoriaOrcamentariaDTO.NORMAL.equals(categoriaOrcamentaria) ? " and desp.EXERCICIO_ID = :EXERCICIO " : "")
            .append(getAndVigenciaVwUnidade("trunc(pagext.DATAPAGTO)"))
            .append(tipoRestosProcessado != null ? " and emp.tiporestosprocessados  = :tipoResto " : "")
            .append(categoriaOrcamentaria != null ? " and emp.categoriaorcamentaria = :categoria and liq.categoriaorcamentaria = :categoria and pag.categoriaorcamentaria = :categoria " : "")
            .append(" and pag.status <> :aberto ")
            .append(" and pagExt.status <> :aberto ")
            .append(adicionarParametros(filtros))
            .append(" union all ")
            .append(" select COALESCE(sum(est.valor), 0) AS valor FROM receitaextra rec ")
            .append(" inner join RETENCAOPGTO ret on rec.RETENCAOPGTO_ID = ret.id ")
            .append(" INNER JOIN PagamentoReceitaExtra pr ON rec.id = pr.receitaextra_id ")
            .append(" INNER JOIN pagamentoextra pagext ON pr.PAGAMENTOEXTRA_ID = pagext.id ")
            .append(" INNER JOIN PAGAMENTOEXTRAESTORNO est ON est.PAGAMENTOEXTRA_ID = pagext.id ")
            .append(" inner join pagamento pag on ret.PAGAMENTO_ID = pag.id ")
            .append(" inner join liquidacao liq on pag.liquidacao_id = liq.id ")
            .append(" inner join empenho emp on liq.empenho_id = emp.id  ");
        sql.append(buscarJoinsComunsParaEmpenhoLiquidacaoPagamento(formulaItemDemonstrativo, filtros, "est", categoriaOrcamentaria));
        sql.append(" inner join subconta sub on pag.subconta_id = sub.id ")
            .append(adicionarSubContaPelaFormula(formulaItemDemonstrativo, filtros))
            .append(" WHERE trunc(est.DATAESTORNO) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy') ")
            .append(CategoriaOrcamentariaDTO.NORMAL.equals(categoriaOrcamentaria) ? " and desp.EXERCICIO_ID = :EXERCICIO " : "")
            .append(getAndVigenciaVwUnidade("trunc(est.DATAESTORNO)"))
            .append(tipoRestosProcessado != null ? " and emp.tiporestosprocessados  = :tipoResto " : "")
            .append(categoriaOrcamentaria != null ? " and emp.categoriaorcamentaria = :categoria and liq.categoriaorcamentaria = :categoria and pag.categoriaorcamentaria = :categoria " : "")
            .append(" and pag.status <> :aberto ")
            .append(adicionarParametros(filtros))
            .append("  UNION ALL  ")
            .append("  SELECT COALESCE(SUM(rec.valor), 0) * - 1 AS valor         ")
            .append("  FROM receitaextra rec  ")
            .append("  INNER JOIN UNIDADEORGANIZACIONAL und ON rec.UNIDADEORGANIZACIONAL_ID = und.id  ")
            .append(adicionarUnidadesPelaFormula(formulaItemDemonstrativo, filtros))
            .append("  inner join CONTAEXTRAORCAMENTARIA ce on rec.CONTAEXTRAORCAMENTARIA_ID = ce.id and ce.TIPOCONTAEXTRAORCAMENTARIA = :consignacoes   ")
            .append("  INNER JOIN subconta sub ON rec.subconta_id = sub.id  ")
            .append(adicionarSubContaPelaFormula(formulaItemDemonstrativo, filtros))
            .append(" WHERE trunc(rec.datareceita) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy')")
            .append("  and rec.transportado = 0 ")
            .append("  UNION ALL  ")
            .append("  SELECT COALESCE(SUM(est.valor), 0)  AS valor  ")
            .append("  FROM receitaextra rec  ")
            .append("  inner join CONTAEXTRAORCAMENTARIA ce on rec.CONTAEXTRAORCAMENTARIA_ID = ce.id and ce.TIPOCONTAEXTRAORCAMENTARIA = :consignacoes   ")
            .append("  INNER JOIN UNIDADEORGANIZACIONAL und ON rec.UNIDADEORGANIZACIONAL_ID = und.id  ")
            .append(adicionarUnidadesPelaFormula(formulaItemDemonstrativo, filtros))
            .append("  INNER JOIN receitaextraestorno est ON est.receitaextra_id = rec.id  ")
            .append("  INNER JOIN subconta sub ON rec.subconta_id = sub.id  ")
            .append(adicionarSubContaPelaFormula(formulaItemDemonstrativo, filtros))
            .append("  WHERE trunc(est.dataestorno) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy')")
            .append("  and rec.transportado = 0 ")
            .append("  union all  ")
            .append("  SELECT COALESCE(SUM(pagext.valor), 0)  AS valor  ")
            .append("  FROM receitaextra rec  ")
            .append("  INNER JOIN PagamentoReceitaExtra pr ON rec.id = pr.receitaextra_id  ")
            .append("  INNER JOIN pagamentoextra pagext ON pr.PAGAMENTOEXTRA_ID = pagext.id  ")
            .append("  INNER JOIN UNIDADEORGANIZACIONAL und ON pagext.UNIDADEORGANIZACIONAL_ID = und.id  ")
            .append(adicionarUnidadesPelaFormula(formulaItemDemonstrativo, filtros))
            .append("  inner join CONTAEXTRAORCAMENTARIA ce on rec.CONTAEXTRAORCAMENTARIA_ID = ce.id and ce.TIPOCONTAEXTRAORCAMENTARIA = :consignacoes   ")
            .append("  INNER JOIN subconta sub ON pagext.subconta_id = sub.id  ")
            .append(adicionarSubContaPelaFormula(formulaItemDemonstrativo, filtros))
            .append("  WHERE trunc(pagext.DATAPAGTO) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy')")
            .append("  AND pagext.status   <> :aberto  ")
            .append("  and rec.transportado = 0 ")
            .append("  UNION ALL  ")
            .append("  SELECT COALESCE(SUM(est.valor), 0) * - 1 AS valor  ")
            .append("  FROM receitaextra rec  ")
            .append("  INNER JOIN PagamentoReceitaExtra pr ON rec.id = pr.receitaextra_id  ")
            .append("  INNER JOIN pagamentoextra pagext ON pr.PAGAMENTOEXTRA_ID = pagext.id  ")
            .append("  INNER JOIN UNIDADEORGANIZACIONAL und ON pagext.UNIDADEORGANIZACIONAL_ID = und.id  ")
            .append(adicionarUnidadesPelaFormula(formulaItemDemonstrativo, filtros))
            .append("  inner join CONTAEXTRAORCAMENTARIA ce on rec.CONTAEXTRAORCAMENTARIA_ID = ce.id and ce.TIPOCONTAEXTRAORCAMENTARIA = :consignacoes   ")
            .append("  INNER JOIN PAGAMENTOEXTRAESTORNO est ON est.PAGAMENTOEXTRA_ID = pagext.id  ")
            .append("  INNER JOIN subconta sub ON pagext.subconta_id = sub.id  ")
            .append(adicionarSubContaPelaFormula(formulaItemDemonstrativo, filtros))
            .append("  WHERE trunc(est.dataestorno) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and TO_DATE(:DATAFINAL, 'dd/mm/yyyy')")
            .append("  AND pagext.status   <> :aberto  ")
            .append("  and rec.transportado = 0 ")
            .append(" ) dados ");
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        if (CategoriaOrcamentariaDTO.NORMAL.equals(categoriaOrcamentaria)) {
            parameters.addValue("EXERCICIO", filtros.getExercicioId());
        }
        parameters.addValue("DATAINICIAL", filtros.getDataInicial());
        parameters.addValue("DATAFINAL", filtros.getDataFinal());
        if (categoriaOrcamentaria != null) {
            parameters.addValue("categoria", categoriaOrcamentaria.name());
        }
        parameters.addValue("aberto", StatusPagamentoDTO.ABERTO.name());
        if (tipoRestosProcessado != null) {
            parameters.addValue("tipoResto", tipoRestosProcessado.name());
        }
        parameters.addValue("consignacoes", TipoContaExtraorcamentariaDTO.DEPOSITOS_CONSIGNACOES.name());

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
