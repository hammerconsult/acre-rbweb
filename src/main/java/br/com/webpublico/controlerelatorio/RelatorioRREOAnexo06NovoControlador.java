package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.ReferenciaAnual;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.RREOAnexo06Item;
import br.com.webpublico.enums.BimestreAnexosLei;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.relatoriofacade.RelatorioRREOAnexo06NovoFacade;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo6-novo-2018", pattern = "/relatorio/rreo/anexo6/novo/2018/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo06novo2018.xhtml"),
    @URLMapping(id = "relatorio-rreo-anexo6-novo", pattern = "/relatorio/rreo/anexo6/novo/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo06novo.xhtml")
})
public class RelatorioRREOAnexo06NovoControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    @EJB
    private RelatorioRREOAnexo06NovoFacade relatorioFacade;
    private BigDecimal grupo1ReceitaPrimaria;
    private BigDecimal grupo2DespesasPagas;
    private BigDecimal grupo2RestosAPagarProcessadosPagos;
    private BigDecimal grupo2RestosAPagarNaoProcessadosPagos;
    private BigDecimal grupo5Ativos;
    private BigDecimal grupo5Passivos;
    private BigDecimal grupo8DCLColuna1;
    private BigDecimal grupo8DCLColuna2;
    private BigDecimal grupo8RestosAPagarColunaA;
    private BigDecimal grupo8RestosAPagarColunaB;
    private BigDecimal grupo9Total;
    private boolean modeloDe2018;

    public RelatorioRREOAnexo06NovoControlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-rreo-anexo6-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.ANEXO6_RREO;
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
        modeloDe2018 = false;
        super.limparCampos();
    }

    @URLAction(mappingId = "relatorio-rreo-anexo6-novo-2018", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos2018() {
        limparVariaveis();
        portalTipoAnexo = PortalTipoAnexo.ANEXO6_RREO;
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
        modeloDe2018 = true;
        super.limparCampos();
    }

    private void limparVariaveis() {
        grupo1ReceitaPrimaria = BigDecimal.ZERO;
        grupo2DespesasPagas = BigDecimal.ZERO;
        grupo2RestosAPagarProcessadosPagos = BigDecimal.ZERO;
        grupo2RestosAPagarNaoProcessadosPagos = BigDecimal.ZERO;
        grupo5Ativos = BigDecimal.ZERO;
        grupo5Passivos = BigDecimal.ZERO;
        grupo8RestosAPagarColunaA = BigDecimal.ZERO;
        grupo8RestosAPagarColunaB = BigDecimal.ZERO;
        grupo8DCLColuna1 = BigDecimal.ZERO;
        grupo8DCLColuna2 = BigDecimal.ZERO;
        grupo9Total = BigDecimal.ZERO;
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 6 - Novo", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        ReferenciaAnual referenciaAnual = getReferenciaAnual();
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
        dto.adicionarParametro("DATAINICIAL", bimestre.getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro("BIMESTRE_FINAL", isUltimoBimestre());
        dto.adicionarParametro("DATAFINAL", bimestre.getMesFinal().getDescricao().toUpperCase());
        dto.adicionarParametro("ID_EXERCICIO", exercicio.getId());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        Exercicio exAnterior = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1);
        dto.adicionarParametro("ID_EXERCICIO_ANTERIOR", exAnterior.getId());
        dto.adicionarParametro("ANO_EXERCICIO_ANTERIOR", exAnterior.getAno());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("mesInicial", bimestre.getMesInicial().getNumeroMesString());
        dto.adicionarParametro("mesFinal", bimestre.getMesFinal().getNumeroMesString());
        dto.adicionarParametro("modeloDe2018", modeloDe2018);
        dto.adicionarParametro("metaResultadoPrimarioRA", referenciaAnual != null ? referenciaAnual.getMetaResultadoPrimario() : BigDecimal.ZERO);
        dto.adicionarParametro("metaResultadoNominalRA", referenciaAnual != null ? referenciaAnual.getMetaResultadoNominal() : BigDecimal.ZERO);
        dto.adicionarParametro("relatoriosItemDemonst", relatoriosItemDemonst.toDto());
        dto.setNomeRelatorio("RelatorioRREOAnexo06Novo");
    }

    public void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        atualizarExercicio(exercicio);
        atualizarDataInicial(Mes.JANEIRO);
        atualizarExercicioDaDataFinal(exercicio);
        itemDemonstrativoFiltros.setDataReferencia("01/" + bimestre.getMesInicial().getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
    }

    private void atualizarExercicio(Exercicio exercicio) {
        itemDemonstrativoFiltros.setExercicio(exercicio);
    }

    private void atualizarExercicioDaDataFinal(Exercicio exercicio) {
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), exercicio.getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + exercicio.getAno());
    }

    private void atualizarDataInicial(Mes mes) {
        itemDemonstrativoFiltros.setDataInicial("01/" + mes.getNumeroMesString() + "/" + itemDemonstrativoFiltros.getExercicio().getAno());
    }

    public List<RREOAnexo06Item> buscarDadosGrupo1() {
        List<RREOAnexo06Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioFacade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo06Item it = new RREOAnexo06Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                it.setValorColuna1(buscarPrevisaoInicial(itemDemonstrativo).add(buscarPrevisaoAtualizada(itemDemonstrativo)));
                it.setValorColuna2(buscarReceitaRealizadaAteOBimestre(itemDemonstrativo));
                if (item.getOrdem() == 39) {
                    grupo1ReceitaPrimaria = it.getValorColuna2();
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo06Item> buscarDadosGrupo2() {
        List<RREOAnexo06Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioFacade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo06Item it = new RREOAnexo06Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                it.setValorColuna1(buscarDotacaoInicial(itemDemonstrativo).add(buscarCreditosAdicionais(itemDemonstrativo)));
                it.setValorColuna2(buscarDespesasEmpenhadas(itemDemonstrativo));
                it.setValorColuna3(buscarDespesasLiquidadas(itemDemonstrativo));
                it.setValorColuna4(buscarDespesasPagas(itemDemonstrativo));
                it.setValorColuna5(buscarRestosAPagarProcessadosPagos(itemDemonstrativo));
                it.setValorColuna6(buscarRestosAPagarNaoProcessadosLiquidados(itemDemonstrativo));
                it.setValorColuna7(buscarRestosAPagarNaoProcessadosPagos(itemDemonstrativo));
                if (item.getOrdem() == 16) {
                    grupo2DespesasPagas = it.getValorColuna4();
                    grupo2RestosAPagarProcessadosPagos = it.getValorColuna5();
                    grupo2RestosAPagarNaoProcessadosPagos = it.getValorColuna7();
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo06Item> buscarDadosGrupo3() {
        List<RREOAnexo06Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioFacade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 3);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo06Item it = new RREOAnexo06Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                it.setValorColuna1(grupo1ReceitaPrimaria.subtract(
                    (grupo2DespesasPagas.add(grupo2RestosAPagarNaoProcessadosPagos)
                        .add(grupo2RestosAPagarProcessadosPagos))));
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo06Item> buscarDadosGrupo5() {
        List<RREOAnexo06Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioFacade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 5);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo06Item it = new RREOAnexo06Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                if (item.getOrdem() == 1) {
                    it.setValorColuna1(buscarReceitaRealizadaAteOBimestre(itemDemonstrativo));
                    grupo5Ativos = it.getValorColuna1();
                } else {
                    it.setValorColuna1(buscarSaldoContabilAtualPorTipoBalancete(itemDemonstrativo));
                    grupo5Passivos = it.getValorColuna1();
                }

                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo06Item> buscarDadosGrupo6() {
        List<RREOAnexo06Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioFacade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 6);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo06Item it = new RREOAnexo06Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                BigDecimal valorGrupo3 = grupo1ReceitaPrimaria.subtract(
                    (grupo2DespesasPagas.add(grupo2RestosAPagarNaoProcessadosPagos)
                        .add(grupo2RestosAPagarProcessadosPagos)));
                it.setValorColuna1(valorGrupo3.add(grupo5Ativos.subtract(grupo5Passivos)));
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo06Item> buscarDadosGrupo8() {
        List<RREOAnexo06Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioFacade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 8);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo06Item it = new RREOAnexo06Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                if (item.getOrdem() == 8) {
                    it.setValorColuna1(grupo8DCLColuna1.subtract(grupo8DCLColuna2));
                } else {
                    it.setValorColuna1(buscarSaldoContabilInicial(itemDemonstrativo));
                    itemDemonstrativoFiltros.setDataInicial("01/" + bimestre.getMesInicial().getNumeroMesString() + "/" + exercicio.getAno());
                    it.setValorColuna2(buscarSaldoContabilAtualPorTipoBalancete(itemDemonstrativo));
                    atualizarDataInicial(Mes.JANEIRO);
                    if (item.getOrdem() == 5) {
                        grupo8RestosAPagarColunaA = it.getValorColuna1();
                        grupo8RestosAPagarColunaB = it.getValorColuna2();
                    } else if (item.getOrdem() == 7) {
                        grupo8DCLColuna1 = it.getValorColuna1();
                        grupo8DCLColuna2 = it.getValorColuna2();
                    }
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo06Item> buscarDadosGrupo9() {
        List<RREOAnexo06Item> toReturn = Lists.newArrayList();
        BigDecimal passivoReconhecidoNaDC = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioFacade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 9);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo06Item it = new RREOAnexo06Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                it.setOrdem(item.getOrdem());
                if (item.getOrdem() == 1) {
                    it.setValorColuna1(grupo8RestosAPagarColunaA.subtract(grupo8RestosAPagarColunaB));
                } else if (modeloDe2018 ? item.getOrdem() == 5 : item.getOrdem() == 7) {
                    it.setValorColuna1(grupo8DCLColuna1.subtract(grupo8DCLColuna2).subtract(grupo8RestosAPagarColunaA.subtract(grupo8RestosAPagarColunaB)).add(passivoReconhecidoNaDC));
                    grupo9Total = it.getValorColuna1();
                } else {
                    it.setValorColuna1(buscarSaldoContabilAtualPorTipoBalancete(itemDemonstrativo));
                    if (item.getOrdem() == 3) {
                        passivoReconhecidoNaDC = it.getValorColuna1();
                    }
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public List<ItemDemonstrativo> recuperarRelatorioEConfiguracoes() {
        relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 6 - Novo", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        return relatorioFacade.getItemDemonstrativoFacade().buscarItensPorExercicioAndRelatorio(exercicio, "", "Anexo 6 - Novo", TipoRelatorioItemDemonstrativo.RREO);
    }

    public void popularComponentes(List<ItemDemonstrativo> itensDemonstrativos) {
        itens = Lists.newArrayList();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
    }

    public List<RREOAnexo06Item> buscarDadosGrupo10() {
        List<RREOAnexo06Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioFacade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 10);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo06Item it = new RREOAnexo06Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                it.setValorColuna1(grupo9Total.subtract(grupo5Ativos.subtract(grupo5Passivos)));
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private ReferenciaAnual getReferenciaAnual() {
        return getReferenciaAnualFacade().recuperaReferenciaPorExercicio(exercicio);
    }

    private boolean isUltimoBimestre() {
        return bimestre.isUltimoBimestre();
    }

    private BigDecimal buscarPrevisaoInicial(ItemDemonstrativo itemDemonstrativo) {
        return relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.PREVISAO_INICIAL);
    }

    private BigDecimal buscarPrevisaoAtualizada(ItemDemonstrativo itemDemonstrativo) {
        return relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.PREVISAO_ATUALIZADA);
    }

    private BigDecimal buscarDotacaoInicial(ItemDemonstrativo itemDemonstrativo) {
        return relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DOTACAO_INICIAL);
    }

    private BigDecimal buscarCreditosAdicionais(ItemDemonstrativo itemDemonstrativo) {
        return relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.CREDITOS_ADICIONAIS);
    }

    private BigDecimal buscarCreditosAdicionaisSuperavit(ItemDemonstrativo itemDemonstrativo) {
        return relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.CREDITOS_ADICIONAIS_SUPERAVIT);
    }

    private BigDecimal buscarDespesasEmpenhadas(ItemDemonstrativo itemDemonstrativo) {
        return relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_EMPENHADAS_ATE_BIMESTRE);
    }

    private BigDecimal buscarDespesasLiquidadas(ItemDemonstrativo itemDemonstrativo) {
        return relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_LIQUIDADAS_ATE_BIMESTRE);
    }

    private BigDecimal buscarDespesasPagas(ItemDemonstrativo itemDemonstrativo) {
        return relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_PAGAS_ATE_BIMESTRE);
    }

    private BigDecimal buscarReceitaRealizadaAteOBimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RECEITA_REALIZADA_ATE_BIMESTRE);
    }

    private BigDecimal buscarRestosAPagarProcessadosPagos(ItemDemonstrativo itemDemonstrativo) {
        return relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RESTOS_PROCESSADOS_PAGOS_ATE_BIMESTRE);
    }

    private BigDecimal buscarRestosAPagarNaoProcessadosPagos(ItemDemonstrativo itemDemonstrativo) {
        return relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RESTOS_NAO_PROCESSADOS_PAGOS_ATE_BIMESTRE);
    }

    private BigDecimal buscarRestosAPagarNaoProcessadosLiquidados(ItemDemonstrativo itemDemonstrativo) {
        return relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RESTOS_NAO_PROCESSADOS_LIQUIDADOS_ATE_BIMESTRE);
    }

    private BigDecimal buscarSaldoContabilAtual(ItemDemonstrativo itemDemonstrativo) {
        return relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.SALDO_CONTABIL_ATUAL);
    }

    private BigDecimal buscarSaldoContabilAtualPorTipoBalancete(ItemDemonstrativo itemDemonstrativo) {
        return relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.SALDO_CONTABIL_ATUAL_POR_TIPO_BALANCETE);
    }

    private BigDecimal buscarSaldoContabilInicial(ItemDemonstrativo itemDemonstrativo) {
        return relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.SALDO_CONTABIL_INICIAL);
    }

    private BigDecimal buscarRestoAPagarNaoProcessados(ItemDemonstrativo itemDemonstrativo) {
        return relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RESTOS_A_PAGAR_NAO_PROCESSADOS);
    }

    public boolean isModeloDe2018() {
        return modeloDe2018;
    }

    public void setModeloDe2018(boolean modeloDe2018) {
        this.modeloDe2018 = modeloDe2018;
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioRREOAnexo06Novo";
    }

    @Override
    public String getApi() {
        return "contabil/rreo-anexo6-novo/";
    }
}
