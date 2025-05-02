package br.com.webpublico.relatoriofacade;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.FormulaItemDemonstrativoDTO;
import br.com.webpublico.contabil.relatorioitemdemonstrativo.dto.ItemDemonstrativoFiltrosDTO;
import br.com.webpublico.contabil.relatorioitemdemonstrativo.dto.TipoRestosProcessadoDTO;
import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.contabil.RelatorioItemDemonstrativoCalculador;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.webreportdto.dto.contabil.CategoriaOrcamentariaDTO;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mateus on 23/04/18.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class RelatorioRGFAnexo01Facade extends RelatorioItemDemonstrativoCalculador {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private RelatorioRREOAnexo03Calculator relatorioRREOAnexo03Calculator;
    @EJB
    private ItemDemonstrativoCalculator itemDemonstrativoCalculator;

    public BigDecimal calcularRestos(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, String dataInicial, String dataFinal, String tipoAdministracao, String poder, RelatoriosItemDemonst relatoriosItemDemonst) {
        return calcularRestosImpl(itemDemonstrativo, exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private BigDecimal calcularRestosImpl(ItemDemonstrativo itDemonstrativo, Exercicio exercicioCorrente, String dataInicial, String dataFinal, String tipoAdministracao, String poder, RelatoriosItemDemonst relatoriosItemDemonst) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalAdicao = BigDecimal.ZERO;
        BigDecimal totalSubtracao = BigDecimal.ZERO;
        itDemonstrativo = getItemDemonstrativoFacade().recuperar(itDemonstrativo.getId());
        List<FormulaItemDemonstrativo> formulas = itDemonstrativo.getFormulas();
        for (FormulaItemDemonstrativo formula : formulas) {
            List<ComponenteFormula> componentes = formula.getComponenteFormula();
            if (formula.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.add(calcularRestosImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst));
                    } else {
                        if (totalAdicao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalAdicao = totalAdicao.add(calculaRestosSql(exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst, formula)).multiply(new BigDecimal(-1));
                            } else {
                                totalAdicao = totalAdicao.add(calculaRestosSql(exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst, formula));
                            }
                        }
                    }
                }
            } else {
                for (ComponenteFormula componente : componentes) {
                    if (componente instanceof ComponenteFormulaItem) {
                        total = total.subtract(calcularRestosImpl(((ComponenteFormulaItem) componente).getItemDemonstrativo(), exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst));
                    } else {
                        if (totalSubtracao.compareTo(BigDecimal.ZERO) == 0) {
                            if (itDemonstrativo.getInverteSinal()) {
                                totalSubtracao = totalSubtracao.subtract(calculaRestosSql(exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst, formula)).multiply(new BigDecimal(-1));
                            } else {
                                totalSubtracao = totalSubtracao.subtract(calculaRestosSql(exercicioCorrente, dataInicial, dataFinal, tipoAdministracao, poder, relatoriosItemDemonst, formula));
                            }
                        }
                    }
                }
            }
        }
        total = total.add(totalAdicao.add(totalSubtracao));
        return total;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal calculaRestosSql(Exercicio ex, String dataInicial, String dataFinal, String tipoAdministracao, String poder, RelatoriosItemDemonst relatoriosItemDemonst, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        BigDecimal total;
        String sql = " select sum(valor) from ( "
            + " SELECT COALESCE(SUM(EMP.valor),0) AS VALOR "
            + " FROM EMPENHO EMP "
            + " INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID "
            + " INNER JOIN PROVISAOPPADESPESA PROV ON DESP.PROVISAOPPADESPESA_ID = PROV.ID "
            + " INNER JOIN CONTA C ON PROV.CONTADEDESPESA_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON EMP.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID and cast(EMP.DATAEMPENHO as Date) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, cast(EMP.DATAEMPENHO as Date)) "
            + " WHERE trunc(EMP.DATAEMPENHO)  between  to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:DATAFINAL, 'dd/mm/yyyy') " + tipoAdministracao + " " + poder
            + " union all "
            + " SELECT COALESCE(SUM(est.valor),0) * - 1 AS VALOR  "
            + " from empenhoestorno est "
            + " inner join EMPENHO EMP on est.empenho_id = emp.id "
            + " INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID  "
            + " INNER JOIN PROVISAOPPADESPESA PROV ON DESP.PROVISAOPPADESPESA_ID = PROV.ID  "
            + " INNER JOIN CONTA C ON PROV.CONTADEDESPESA_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON EMP.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID and cast(est.dataestorno as Date) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, cast(est.dataestorno as Date))  "
            + " WHERE trunc(est.dataestorno)  between  to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:DATAFINAL, 'dd/mm/yyyy') " + tipoAdministracao + " " + poder
            + " union all "
            + " SELECT COALESCE(SUM(liq.valor),0) * - 1 AS VALOR "
            + " FROM liquidacao liq "
            + " inner join EMPENHO EMP on liq.empenho_id = emp.id"
            + " INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID "
            + " INNER JOIN PROVISAOPPADESPESA PROV ON DESP.PROVISAOPPADESPESA_ID = PROV.ID "
            + " INNER JOIN CONTA C ON PROV.CONTADEDESPESA_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON EMP.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID and cast(EMP.DATAEMPENHO as Date) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, cast(EMP.DATAEMPENHO as Date)) "
            + " WHERE trunc(liq.dataliquidacao)  between  to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:DATAFINAL, 'dd/mm/yyyy')  " + tipoAdministracao + " " + poder
            + " union all "
            + " SELECT COALESCE(SUM(est.valor),0) AS VALOR "
            + " FROM liquidacaoestorno est "
            + " inner join liquidacao liq on est.liquidacao_id = liq.id "
            + " inner join EMPENHO EMP on liq.empenho_id = emp.id"
            + " INNER JOIN DESPESAORC DESP ON EMP.DESPESAORC_ID = DESP.ID "
            + " INNER JOIN PROVISAOPPADESPESA PROV ON DESP.PROVISAOPPADESPESA_ID = PROV.ID "
            + " INNER JOIN CONTA C ON PROV.CONTADEDESPESA_ID = C.ID ";
        if (relatoriosItemDemonst.getUsaConta()) {
            sql += itemDemonstrativoCalculator.recuperaIds(formulaItemDemonstrativo, "C", ComponenteFormulaConta.class);
        }
        sql += " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON EMP.UNIDADEORGANIZACIONAL_ID = VW.SUBORDINADA_ID and cast(EMP.DATAEMPENHO as Date) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, cast(EMP.DATAEMPENHO as Date)) "
            + " WHERE trunc(est.dataestorno) between  to_date(:DATAINICIAL, 'dd/mm/yyyy') AND to_date(:DATAFINAL, 'dd/mm/yyyy') " + tipoAdministracao + " " + poder
            + " )";
        Query q = em.createNativeQuery(sql);
        q.setParameter("DATAINICIAL", dataInicial);
        q.setParameter("DATAFINAL", dataFinal);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            total = (BigDecimal) q.getResultList().get(0);
        } else {
            total = BigDecimal.ZERO;
        }
        return total;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public RelatorioRREOAnexo03Calculator getRelatorioRREOAnexo03Calculator() {
        return relatorioRREOAnexo03Calculator;
    }

    @Override
    protected BigDecimal buscarValor(FormulaItemDemonstrativoDTO formulaItemDemonstrativo, ItemDemonstrativoFiltrosDTO filtros, TipoCalculoItemDemonstrativo tipoCalculo) {
        switch (tipoCalculo) {
            case RESTOS_A_PAGAR_INSCRITOS_NAO_PROCESSADOS:
                return buscarRestosInscritos(formulaItemDemonstrativo, filtros, TipoRestosProcessadoDTO.NAO_PROCESSADOS);
            case DESPESAS_LIQUIDADAS_ATE_BIMESTRE:
                return buscarDespesasLiquidadasAteOBimestrePorCategoria(formulaItemDemonstrativo, filtros, CategoriaOrcamentariaDTO.NORMAL, null);
            default:
                return BigDecimal.ZERO;
        }
    }

    public BigDecimal buscarValorRclAjustada(Mes mesFinal) {
        return getRelatorioRREOAnexo03Calculator().calcularRcl(mesFinal, getSistemaFacade().getExercicioCorrente()).subtract(recuperarValorTransferencias(mesFinal, getSistemaFacade().getExercicioCorrente()));
    }

    public BigDecimal buscarValorRclAjustadaExercicioAnterior() {
        return getRelatorioRREOAnexo03Calculator().calcularRclExercicioAnterior(Mes.DEZEMBRO).subtract(recuperarValorTransferenciasExercicioAnterior(Mes.DEZEMBRO));
    }

    public BigDecimal recuperarValorTransferenciasExercicioAnterior(Mes mesFinal) {
        Exercicio exercicioAnterior = getExercicioFacade().getExercicioPorAno(getSistemaFacade().getExercicioCorrente().getAno() - 1);
        return recuperarValorTransferencias(mesFinal, exercicioAnterior);
    }

    public BigDecimal recuperarValorTransferencias(Mes mesFinal, Exercicio exercicio) {
        RelatoriosItemDemonst relatorio = getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", exercicio, TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativoComponente> itens = popularConfiguracoesDoRelatorio(exercicio);
        BigDecimal transferencias = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente item : itens) {
            if (item.getGrupo() == 2) {
                ItemDemonstrativo itemDemonstrativo = recuperarItemDemonstrativoPeloNomeERelatorio(item.getDescricao(), exercicio, relatorio);
                String data = mesFinal.getNumeroMesString() + "/" + exercicio.getAno();
                for (int i = 1; i <= 12; i++) {
                    transferencias = transferencias.add(getRelatorioRREOAnexo03Calculator().recuperarValorPeloMesAnterior(itemDemonstrativo, data, relatorio, exercicio));
                    data = removerUmMes(data);
                }
            }
        }
        return transferencias;
    }

    private List<ItemDemonstrativoComponente> popularConfiguracoesDoRelatorio(Exercicio exercicio) {
        List<ItemDemonstrativoComponente> itens = Lists.newArrayList();
        List<ItemDemonstrativo> itensDemonstrativos = getItemDemonstrativoFacade().buscarItensPorExercicioAndRelatorio(exercicio, "", "Anexo 1", TipoRelatorioItemDemonstrativo.RGF);
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
        return itens;
    }

    private String removerUmMes(String data) {
        Integer mes = Integer.parseInt(data.substring(0, 2));
        Integer ano = Integer.parseInt(data.substring(3, 7));
        if (mes != 0 && mes != 1) {
            mes -= 1;
        } else {
            mes = 12;
            ano -= 1;
        }
        return StringUtil.preencheString(String.valueOf(mes), 2, '0') + "/" + ano;
    }
}

