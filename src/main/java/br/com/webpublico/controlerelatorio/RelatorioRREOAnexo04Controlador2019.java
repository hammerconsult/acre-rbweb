/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.RREOAnexo04Item;
import br.com.webpublico.enums.BimestreAnexosLei;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.RelatorioRREOAnexo04Facade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rreo-anexo4-2019", pattern = "/relatorio/rreo/anexo4/2019/", viewId = "/faces/financeiro/relatorioslrf/relatoriorreoanexo04-2019.xhtml")
})
@Deprecated
public class RelatorioRREOAnexo04Controlador2019 extends AbstractReport implements Serializable {

    @EJB
    private RelatorioRREOAnexo04Facade relatorioRREOAnexo04Facade;
    private BimestreAnexosLei bimestre;
    private List<ItemDemonstrativoComponente> itens;
    private BigDecimal totalPrevisaoInicialGrupo1;
    private BigDecimal totalPrevisaoAtualizadaGrupo1;
    private BigDecimal totalReceitasRealizadasAteOBimestreGrupo1;
    private BigDecimal totalReceitasRealizadasExAnteriorAteOBimestreGrupo1;
    private BigDecimal totalPrevisaoInicialGrupo7;
    private BigDecimal totalPrevisaoAtualizadaGrupo7;
    private BigDecimal totalReceitasRealizadasAteOBimestreGrupo7;
    private BigDecimal totalReceitasRealizadasExAnteriorAteOBimestreGrupo7;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltros;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltrosExercicioAnterior;

    public RelatorioRREOAnexo04Controlador2019() {
        itens = Lists.newArrayList();
    }

    public List<SelectItem> getBimestres() {
        return Util.getListSelectItemSemCampoVazio(BimestreAnexosLei.values(), false);
    }

    @URLAction(mappingId = "relatorio-rreo-anexo4", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
    }

    public void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setDataInicial("01/01/" + getSistemaFacade().getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), getSistemaFacade().getExercicioCorrente().getAno()) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + getSistemaFacade().getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setDataReferencia("01/" + bimestre.getMesInicial().getNumeroMesString() + "/" + getSistemaFacade().getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setRelatorio(relatorioRREOAnexo04Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO));
        itemDemonstrativoFiltros.setExercicio(getSistemaFacade().getExercicioCorrente());
    }

    public void instanciarItemDemonstrativoFiltrosExercicioAnterior() {
        itemDemonstrativoFiltrosExercicioAnterior = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltrosExercicioAnterior.setDataInicial("01/01/" + (getSistemaFacade().getExercicioCorrente().getAno() - 1));
        itemDemonstrativoFiltrosExercicioAnterior.setDataFinal(Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), (getSistemaFacade().getExercicioCorrente().getAno() - 1)) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + (getSistemaFacade().getExercicioCorrente().getAno() - 1));
        itemDemonstrativoFiltrosExercicioAnterior.setDataReferencia("01/" + bimestre.getMesInicial().getNumeroMesString() + "/" + (getSistemaFacade().getExercicioCorrente().getAno() - 1));
        itemDemonstrativoFiltrosExercicioAnterior.setRelatorio(relatorioRREOAnexo04Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO));
        itemDemonstrativoFiltrosExercicioAnterior.setExercicio(buscarExercicioAnterior(getExercicioCorrente()));
    }

    public List<RREOAnexo04Item> prepararDados() {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        RREOAnexo04Item item = new RREOAnexo04Item();
        item.setDescricao("");
        toReturn.add(item);
        return toReturn;
    }

    public List<RREOAnexo04Item> prepararDadosParaEmissaoGrupo1() {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo04Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo04Item it = new RREOAnexo04Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                it.setValorColuna1(recuperarPrevisaoInicial(itemDemonstrativo));
                it.setValorColuna2(recuperarPrevisaoAtualizada(itemDemonstrativo).add(it.getValorColuna1()));
                it.setValorColuna4(recuperarReceitasRealizadasAteOBimestre(itemDemonstrativo));
                it.setValorColuna5(recuperarReceitasRealizadasAteOBimestreExAnterior(getItemExercicioAnterior(itemDemonstrativo)));
                if (item.getOrdem() == 33) {
                    totalPrevisaoInicialGrupo1 = it.getValorColuna1();
                    totalPrevisaoAtualizadaGrupo1 = it.getValorColuna2();
                    totalReceitasRealizadasAteOBimestreGrupo1 = it.getValorColuna4();
                    totalReceitasRealizadasExAnteriorAteOBimestreGrupo1 = it.getValorColuna5();
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo04Item> prepararDadosParaEmissaoGrupo2() {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo04Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo04Item it = new RREOAnexo04Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                if (item.getOrdem() == 17) {
                    it.setValorColuna1(recuperarDotacaoInicial(itemDemonstrativo).add(totalPrevisaoInicialGrupo1));
                    it.setValorColuna2(recuperarCreditosAdicionais(itemDemonstrativo).add(recuperarDotacaoInicial(itemDemonstrativo)).add(totalPrevisaoAtualizadaGrupo1));
                    it.setValorColuna3(recuperarDespesasEmpenhadasAteOBimestre(itemDemonstrativo).add(totalReceitasRealizadasAteOBimestreGrupo1));
                    it.setValorColuna4(recuperarDespesasEmpenhadasAteOBimestreExAnterior(getItemExercicioAnterior(itemDemonstrativo)).add(totalReceitasRealizadasExAnteriorAteOBimestreGrupo1));
                    it.setValorColuna5(recuperarDespesasLiquidadasAteOBimestre(itemDemonstrativo).add(totalReceitasRealizadasAteOBimestreGrupo1));
                    it.setValorColuna6(recuperarDespesasLiquidadasAteOBimestreExAnterior(getItemExercicioAnterior(itemDemonstrativo)).add(totalReceitasRealizadasExAnteriorAteOBimestreGrupo1));
                    if (bimestre.isUltimoBimestre()) {
                        it.setValorColuna7(recuperarRestoAPagarNaoProcessados(itemDemonstrativo).add(totalReceitasRealizadasAteOBimestreGrupo1));
                        it.setValorColuna8(recuperarRestoAPagarNaoProcessadosExAnterior(itemDemonstrativo).add(totalReceitasRealizadasExAnteriorAteOBimestreGrupo1));
                    }
                    toReturn.add(new RREOAnexo04Item());
                } else {
                    it.setValorColuna1(recuperarDotacaoInicial(itemDemonstrativo));
                    it.setValorColuna2(recuperarCreditosAdicionais(itemDemonstrativo).add(it.getValorColuna1()));
                    it.setValorColuna3(recuperarDespesasEmpenhadasAteOBimestre(itemDemonstrativo));
                    it.setValorColuna4(recuperarDespesasEmpenhadasAteOBimestreExAnterior(getItemExercicioAnterior(itemDemonstrativo)));
                    it.setValorColuna5(recuperarDespesasLiquidadasAteOBimestre(itemDemonstrativo));
                    it.setValorColuna6(recuperarDespesasLiquidadasAteOBimestreExAnterior(getItemExercicioAnterior(itemDemonstrativo)));
                    if (bimestre.isUltimoBimestre()) {
                        it.setValorColuna7(recuperarRestoAPagarNaoProcessados(itemDemonstrativo));
                        it.setValorColuna8(recuperarRestoAPagarNaoProcessadosExAnterior(itemDemonstrativo));
                    } else {
                        it.setValorColuna7(BigDecimal.ZERO);
                        it.setValorColuna8(BigDecimal.ZERO);
                    }
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public List<RREOAnexo04Item> prepararDadosParaEmissaoGrupo3() {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo04Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 3);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo04Item it = new RREOAnexo04Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                it.setValorColuna1(recuperarReserva(itemDemonstrativo));
                toReturn.add(it);
            }
        }
        return toReturn;
    }


    private List<RREOAnexo04Item> prepararDadosParaEmissaoGrupo4() {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo04Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 4);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo04Item it = new RREOAnexo04Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                it.setValorColuna1(recuperarReserva(itemDemonstrativo));
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo04Item> prepararDadosParaEmissaoGrupo5() {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo04Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 5);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo04Item it = new RREOAnexo04Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                it.setValorColuna1(recuperarReceitasRealizadasAteOBimestre(itemDemonstrativo));
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo04Item> prepararDadosParaEmissaoGrupo6() {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo04Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 6);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo04Item it = new RREOAnexo04Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                atualizarDataInicial(bimestre.getMesInicial());
                it.setValorColuna1(recuperarSaldoAtePeriodo(itemDemonstrativo).multiply(new BigDecimal(-1)));
                atualizarDataInicialDoExercicioAnterior(bimestre.getMesInicial());
                itemDemonstrativoFiltrosExercicioAnterior.setDataFinal("31/12/" + (getSistemaFacade().getExercicioCorrente().getAno() - 1));
                it.setValorColuna2(recuperarSaldoPeriodoExAnterior(itemDemonstrativo).multiply(new BigDecimal(-1)));
                it.setValorColuna3(recuperarSaldoPeriodoExAnterior(itemDemonstrativo).multiply(new BigDecimal(-1)));
                itemDemonstrativoFiltros.setDataInicial("01/01/" + getSistemaFacade().getExercicioCorrente().getAno());
                itemDemonstrativoFiltrosExercicioAnterior.setDataInicial("01/01/" + (getSistemaFacade().getExercicioCorrente().getAno() - 1));
                itemDemonstrativoFiltrosExercicioAnterior.setDataFinal(Util.getDiasMes(bimestre.getMesFinal().getNumeroMes(), (getSistemaFacade().getExercicioCorrente().getAno() - 1)) + "/" + bimestre.getMesFinal().getNumeroMesString() + "/" + (getSistemaFacade().getExercicioCorrente().getAno() - 1));
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private void atualizarDataInicial(Mes mes) {
        itemDemonstrativoFiltros.setDataInicial(Util.getDiasMes(mes.getNumeroMes(), itemDemonstrativoFiltros.getExercicio().getAno()) + "/" + mes.getNumeroMesString() + "/" + itemDemonstrativoFiltros.getExercicio().getAno());
    }

    private void atualizarDataInicialDoExercicioAnterior(Mes mes) {
        itemDemonstrativoFiltrosExercicioAnterior.setDataInicial(Util.getDiasMes(mes.getNumeroMes(), itemDemonstrativoFiltrosExercicioAnterior.getExercicio().getAno()) + "/" + mes.getNumeroMesString() + "/" + itemDemonstrativoFiltrosExercicioAnterior.getExercicio().getAno());
    }

    private List<RREOAnexo04Item> prepararDadosParaEmissaoGrupo7() {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo04Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 7);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo04Item it = new RREOAnexo04Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                it.setValorColuna1(recuperarPrevisaoInicial(itemDemonstrativo));
                it.setValorColuna2(recuperarPrevisaoAtualizada(itemDemonstrativo).add(it.getValorColuna1()));
                it.setValorColuna4(recuperarReceitasRealizadasAteOBimestre(itemDemonstrativo));
                it.setValorColuna5(recuperarReceitasRealizadasAteOBimestreExAnterior(itemDemonstrativo));
                if (item.getOrdem() == 32) {
                    totalPrevisaoInicialGrupo7 = it.getValorColuna1();
                    totalPrevisaoAtualizadaGrupo7 = it.getValorColuna2();
                    totalReceitasRealizadasAteOBimestreGrupo7 = it.getValorColuna4();
                    totalReceitasRealizadasExAnteriorAteOBimestreGrupo7 = it.getValorColuna5();
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo04Item> prepararDadosParaEmissaoGrupo8() {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo04Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 8);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo04Item it = new RREOAnexo04Item();
                it.setNumeroLinhaNoXLS(itemDemonstrativo.getNumeroLinhaNoXLS());
                it.setDescricao(item.getDescricaoComEspacos());
                it.setValorColuna1(recuperarDotacaoInicial(itemDemonstrativo));
                it.setValorColuna2(recuperarCreditosAdicionais(itemDemonstrativo).add(it.getValorColuna1()));
                it.setValorColuna3(recuperarDespesasEmpenhadasAteOBimestre(itemDemonstrativo));
                it.setValorColuna4(recuperarDespesasEmpenhadasAteOBimestreExAnterior(getItemExercicioAnterior(itemDemonstrativo)));
                it.setValorColuna5(recuperarDespesasLiquidadasAteOBimestre(itemDemonstrativo));
                it.setValorColuna6(recuperarDespesasLiquidadasAteOBimestreExAnterior(getItemExercicioAnterior(itemDemonstrativo)));
                if (bimestre.isUltimoBimestre()) {
                    it.setValorColuna7(recuperarRestoAPagarNaoProcessados(itemDemonstrativo));
                    it.setValorColuna8(recuperarRestoAPagarNaoProcessadosExAnterior(getItemExercicioAnterior(itemDemonstrativo)));
                } else {
                    it.setValorColuna7(BigDecimal.ZERO);
                    it.setValorColuna8(BigDecimal.ZERO);
                }
                if (item.getOrdem() == 17) {
                    it.setValorColuna1(it.getValorColuna1().add(totalPrevisaoInicialGrupo7));
                    it.setValorColuna2(it.getValorColuna2().add(totalPrevisaoAtualizadaGrupo7));
                    it.setValorColuna3(it.getValorColuna3().add(totalReceitasRealizadasAteOBimestreGrupo7));
                    it.setValorColuna4(it.getValorColuna4().add(totalReceitasRealizadasExAnteriorAteOBimestreGrupo7));
                    it.setValorColuna5(it.getValorColuna5().add(totalReceitasRealizadasAteOBimestreGrupo7));
                    it.setValorColuna6(it.getValorColuna6().add(totalReceitasRealizadasExAnteriorAteOBimestreGrupo7));
                    it.setValorColuna7(null);
                    it.setValorColuna8(null);
                    toReturn.add(new RREOAnexo04Item());
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RREOAnexo04Item> prepararDadosParaEmissaoGrupo9() {
        List<RREOAnexo04Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRREOAnexo04Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 9);
            if (itemDemonstrativo.getId() != null) {
                RREOAnexo04Item it = new RREOAnexo04Item();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setValorColuna1(recuperarSaldoAtePeriodo(itemDemonstrativo));
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public void gerarRelatorio() {
        try {
            relatorioRREOAnexo04Facade.limparHash();
            relatorioRREOAnexo04Facade.limparHashItensRecuperados();
            instanciarItemDemonstrativoFiltros();
            instanciarItemDemonstrativoFiltrosExercicioAnterior();
            HashMap parameters = montarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(prepararDados());
            gerarRelatorioComDadosEmCollection(getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }


    public void salvarRelatorio() {
        try {
            relatorioRREOAnexo04Facade.limparHash();
            relatorioRREOAnexo04Facade.limparHashItensRecuperados();
            instanciarItemDemonstrativoFiltros();
            instanciarItemDemonstrativoFiltrosExercicioAnterior();
            HashMap parameters = montarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(prepararDados());
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(getExercicioCorrente());
            anexo.setNome("Anexo 4 - Demonstrativo das Receitas e Despesas Previdenciárias do Regime Próprio de Previdência dos Servidores");
            anexo.setMes(bimestre.getMesFinal());
            anexo.setTipo(PortalTransparenciaTipo.RREO);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "RelatorioRREOAnexo04-2019.jasper";
    }

    public HashMap montarParametros() {
        HashMap parameters = new HashMap();
        parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("GRUPO1", prepararDadosParaEmissaoGrupo1());
        parameters.put("GRUPO2", prepararDadosParaEmissaoGrupo2());
        parameters.put("GRUPO3", prepararDadosParaEmissaoGrupo3());
        parameters.put("GRUPO4", prepararDadosParaEmissaoGrupo4());
        parameters.put("GRUPO5", prepararDadosParaEmissaoGrupo5());
        parameters.put("GRUPO6", prepararDadosParaEmissaoGrupo6());
        parameters.put("GRUPO7", prepararDadosParaEmissaoGrupo7());
        parameters.put("GRUPO8", prepararDadosParaEmissaoGrupo8());
        parameters.put("GRUPO9", prepararDadosParaEmissaoGrupo9());
        parameters.put("BIMESTRE_FINAL", bimestre.isUltimoBimestre());
        parameters.put("DATAINICIAL", bimestre.getMesInicial().getDescricao().toUpperCase());
        parameters.put("DATAFINAL", bimestre.getMesFinal().getDescricao().toUpperCase());
        parameters.put("ANO_EXERCICIO", getExercicioCorrente().getAno());
        return parameters;
    }

    private BigDecimal recuperarPrevisaoInicial(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.PREVISAO_INICIAL);
    }

    private BigDecimal recuperarPrevisaoAtualizada(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.PREVISAO_ATUALIZADA);
    }

    private BigDecimal recuperarSaldoPeriodoExAnterior(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltrosExercicioAnterior.toDto(), TipoCalculoItemDemonstrativo.SALDO_CONTABIL_ATUAL_POR_TIPO_BALANCETE);
    }

    private BigDecimal recuperarSaldoAtePeriodo(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.SALDO_CONTABIL_ATUAL_POR_TIPO_BALANCETE);
    }

    private BigDecimal recuperarReserva(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RESERVADO);
    }

    private BigDecimal recuperarReceitasRealizadasAteOBimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RECEITA_REALIZADA_ATE_BIMESTRE);
    }

    private BigDecimal recuperarReceitasRealizadasAteOBimestreExAnterior(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltrosExercicioAnterior.toDto(), TipoCalculoItemDemonstrativo.RECEITA_REALIZADA_ATE_BIMESTRE);
    }

    private ItemDemonstrativo getItemExercicioAnterior(ItemDemonstrativo itemDemonstrativo) {
        return itemDemonstrativo.getItemExercicioAnterior() != null ? itemDemonstrativo.getItemExercicioAnterior() : itemDemonstrativo;
    }

    private BigDecimal recuperarDotacaoInicial(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DOTACAO_INICIAL);
    }

    private BigDecimal recuperarCreditosAdicionais(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.CREDITOS_ADICIONAIS);
    }

    private BigDecimal recuperarDespesasEmpenhadasAteOBimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_EMPENHADAS_ATE_BIMESTRE);
    }

    private BigDecimal recuperarDespesasEmpenhadasAteOBimestreExAnterior(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltrosExercicioAnterior.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_EMPENHADAS_ATE_BIMESTRE);
    }

    private BigDecimal recuperarDespesasLiquidadasAteOBimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_LIQUIDADAS_ATE_BIMESTRE);
    }

    private BigDecimal recuperarDespesasLiquidadasAteOBimestreExAnterior(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltrosExercicioAnterior.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_LIQUIDADAS_ATE_BIMESTRE);
    }

    private Exercicio buscarExercicioAnterior(Exercicio exercicioCorrente) {
        return getExercicioFacade().getExercicioPorAno(exercicioCorrente.getAno() - 1);
    }

    private BigDecimal recuperarRestoAPagarNaoProcessados(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RESTOS_A_PAGAR_NAO_PROCESSADOS);
    }

    private BigDecimal recuperarRestoAPagarNaoProcessadosExAnterior(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRREOAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltrosExercicioAnterior.toDto(), TipoCalculoItemDemonstrativo.RESTOS_A_PAGAR_NAO_PROCESSADOS);
    }

    public List<ItemDemonstrativoComponente> getItens() {
        return itens;
    }

    public void setItens(List<ItemDemonstrativoComponente> itens) {
        this.itens = itens;
    }

    public BimestreAnexosLei getBimestre() {
        return bimestre;
    }

    public void setBimestre(BimestreAnexosLei bimestre) {
        this.bimestre = bimestre;
    }
}
