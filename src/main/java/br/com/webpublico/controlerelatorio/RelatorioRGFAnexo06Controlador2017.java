package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo06Calculator2017;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mateus on 06/10/2014.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo6-2017", pattern = "/relatorio/rgf/anexo6/2017", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo06-2017.xhtml")
})
public class RelatorioRGFAnexo06Controlador2017 extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private RelatorioRGFAnexo06Calculator2017 relatorioRGFAnexo06Calculator2017;
    private RelatorioRGFAnexo01Controlador2017 relatorioRGFAnexo01Controlador2017;
    private RelatorioRGFAnexo02Controlador2017 relatorioRGFAnexo02Controlador2017;
    private RelatorioRGFAnexo03Controlador2017 relatorioRGFAnexo03Controlador2017;
    private RelatorioRGFAnexo04Controlador2017 relatorioRGFAnexo04Controlador2017;
    private RelatorioRGFAnexo05Controlador2017 relatorioRGFAnexo05Controlador2017;
    private Integer quadrimestre;
    private String dataInicial;
    private String dataFinal;
    private String mesFinal;
    private BigDecimal rcl;

    private RelatoriosItemDemonst relatoriosItemDemonst;
    private List<ItemDemonstrativoComponente> itens;
    @Enumerated(EnumType.STRING)
    private Esferas esferas;

    public RelatorioRGFAnexo06Controlador2017() {
        itens = new ArrayList<ItemDemonstrativoComponente>();
    }

    @URLAction(mappingId = "relatorio-rgf-anexo6-2017", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.esferas = Esferas.CONSOLIDADO;
        this.dataInicial = "";
        this.dataFinal = "";
        this.mesFinal = "";
        this.rcl = BigDecimal.ZERO;
    }

    public List<SelectItem> getListaEsferas() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Esferas es : Esferas.values()) {
            toReturn.add(new SelectItem(es, es.getDescricao()));
        }
        return toReturn;
    }

    private List<RGFAnexo07Item> prepararDados() {
        List<RGFAnexo07Item> toReturn = new ArrayList<>();
        RGFAnexo07Item item = new RGFAnexo07Item();
        item.setDescricao("");
        toReturn.add(item);
        return toReturn;
    }

    private List<RGFAnexo07Item> gerarDadosRCL() {
        List<RGFAnexo07Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo06Calculator2017.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo07Item it = new RGFAnexo07Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setValorColuna1(calcularRcl());
                toReturn.add(it);
            }
        }
        return toReturn;
    }


    private List<RGFAnexo07Item> gerarDadosAnexoUm() {
        BigDecimal percentual = BigDecimal.ZERO;
        BigDecimal percentualPrudencial = BigDecimal.ZERO;
        BigDecimal percentualDespesa = BigDecimal.ZERO;
        BigDecimal limiteMaximo = BigDecimal.ZERO;
        BigDecimal limitePrudencial = BigDecimal.ZERO;
        BigDecimal despesaTotalComPessoal = BigDecimal.ZERO;
        List<RGFAnexo07Item> toReturn = new ArrayList<>();
        List<RGFAnexo01Pessoal2017> lista = new ArrayList<>();
        relatorioRGFAnexo01Controlador2017 = new RelatorioRGFAnexo01Controlador2017();
        relatorioRGFAnexo01Controlador2017.setMesFinal(mesFinal);
        relatorioRGFAnexo01Controlador2017.setMesInicial("01");
        relatorioRGFAnexo01Controlador2017.setSistemaControlador(sistemaControlador);
        relatorioRGFAnexo01Controlador2017.setItemDemonstrativoFacade(relatorioRGFAnexo06Calculator2017.getItemDemonstrativoFacade());
        relatorioRGFAnexo01Controlador2017.setRelatorioRGFAnexo01Calculator2017(relatorioRGFAnexo06Calculator2017.getRelatorioRGFAnexo01Calculator2017());
        relatorioRGFAnexo01Controlador2017.setEsferas(RelatorioRGFAnexo01Controlador2017.Esferas.valueOf(esferas.name()));
        lista = relatorioRGFAnexo01Controlador2017.buscaDadosGrupoUm();
        for (RGFAnexo01Pessoal2017 item : lista) {
            if (item.getDescricao().equals("DESPESA LÍQUIDA COM PESSOAL (III) = (I - II)")) {
                despesaTotalComPessoal = item.getLiquidacao().add(item.getInscritasRestos());
            }
        }
        if (esferas.name().equals("EXECUTIVO")) {
            percentual = new BigDecimal(54);
            percentualPrudencial = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.05)));
        } else if (esferas.name().equals("LEGISLATIVO")) {
            percentual = new BigDecimal(6);
            percentualPrudencial = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.05)));
        } else {
            percentual = new BigDecimal(60);
            percentualPrudencial = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.05)));
        }
        if (rcl != BigDecimal.ZERO) {
            percentualDespesa = despesaTotalComPessoal.divide(rcl, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100));
            limiteMaximo = rcl.multiply(percentual).divide(BigDecimal.valueOf(100), 2);
            limitePrudencial = rcl.multiply(percentualPrudencial).divide(BigDecimal.valueOf(100), 2);
        }
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo06Calculator2017.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo07Item it = new RGFAnexo07Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (it.getDescricao().equals("Despesa Total com Pessoal - DTP")) {
                    it.setValorColuna1(despesaTotalComPessoal);
                    it.setValorColuna2(percentualDespesa);
                } else if (it.getDescricao().equals("Limite Máximo (Incisos I, II e III, art. 20 da LRF) - <%>")) {
                    it.setValorColuna1(limiteMaximo);
                    it.setValorColuna2(percentual);
                } else if (it.getDescricao().equals("Limite Prudencial (§ Único, art. 22 da LRF) - <%>")) {
                    it.setValorColuna1(limitePrudencial);
                    it.setValorColuna2(percentualPrudencial);
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RGFAnexo07Item> gerarDadosAnexoDois() {
        List<RGFAnexo07Item> toReturn = new ArrayList<>();
        List<RGFAnexo02Item> lista = new ArrayList<>();
        relatorioRGFAnexo02Controlador2017 = new RelatorioRGFAnexo02Controlador2017();
        relatorioRGFAnexo02Controlador2017.setMesFinal(mesFinal);
        relatorioRGFAnexo02Controlador2017.setMesInicial("01");
        relatorioRGFAnexo02Controlador2017.setSistemaControlador(sistemaControlador);
        relatorioRGFAnexo02Controlador2017.setItemDemonstrativoFacade(relatorioRGFAnexo06Calculator2017.getItemDemonstrativoFacade());
        relatorioRGFAnexo02Controlador2017.setItemDemonstrativoCalculator(relatorioRGFAnexo06Calculator2017.getItemDemonstrativoCalculator());
        relatorioRGFAnexo02Controlador2017.setRelatorioRREOAnexo05Calculator(relatorioRGFAnexo06Calculator2017.getRelatorioRREOAnexo05Calculator());
        relatorioRGFAnexo02Controlador2017.setRelatorioRREOAnexo03Calculator(relatorioRGFAnexo06Calculator2017.getRelatorioRREOAnexo03Calculator());
        lista = relatorioRGFAnexo02Controlador2017.buscaDadosGrupo1();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo06Calculator2017.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 3);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo07Item it = new RGFAnexo07Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (it.getDescricao().equals("Dívida Consolidada Líquida")) {
                    for (RGFAnexo02Item itemAnexo02 : lista) {
                        if (itemAnexo02.getDescricao().trim().equals("DÍVIDA CONSOLIDADA LÍQUIDA (DCL) (III) = (I - II)")) {
                            if (quadrimestre == 1) {
                                it.setValorColuna1(itemAnexo02.getPrimeiroQuadrimestre());
                            } else if (quadrimestre == 2) {
                                it.setValorColuna1(itemAnexo02.getSegundoQuadrimestre());
                            } else if (quadrimestre == 3) {
                                it.setValorColuna1(itemAnexo02.getTerceiroQuadrimestre());
                            }
                        } else if (itemAnexo02.getDescricao().equals("% da DCL sobre a RCL (III / RCL)")) {
                            if (quadrimestre == 1) {
                                it.setValorColuna2(itemAnexo02.getPrimeiroQuadrimestre());
                            } else if (quadrimestre == 2) {
                                it.setValorColuna2(itemAnexo02.getSegundoQuadrimestre());
                            } else if (quadrimestre == 3) {
                                it.setValorColuna2(itemAnexo02.getTerceiroQuadrimestre());
                            }
                        }
                    }
                } else if (item.getNome().equals("Limite Definido por Resolução do Senado Federal")) {
                    for (RGFAnexo02Item itemAnexo02 : lista) {
                        if (itemAnexo02.getDescricao().equals("LIMITE DEFINIDO POR RESOLUÇÃO DO SENADO FEDERAL - 120%")) {
                            if (quadrimestre == 1) {
                                it.setValorColuna1(itemAnexo02.getPrimeiroQuadrimestre());
                            } else if (quadrimestre == 2) {
                                it.setValorColuna1(itemAnexo02.getSegundoQuadrimestre());
                            } else if (quadrimestre == 3) {
                                it.setValorColuna1(itemAnexo02.getTerceiroQuadrimestre());
                            }
                            it.setValorColuna2(it.getValorColuna1().divide(rcl, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
                        }
                    }
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }


    private List<RGFAnexo07Item> gerarDadosAnexoTres() {
        List<RGFAnexo07Item> toReturn = new ArrayList<>();
        List<RGFAnexo03Item> lista = new ArrayList<>();
        relatorioRGFAnexo03Controlador2017 = new RelatorioRGFAnexo03Controlador2017();
        relatorioRGFAnexo03Controlador2017.setMesFinal(mesFinal);
        relatorioRGFAnexo03Controlador2017.setSistemaControlador(sistemaControlador);
        relatorioRGFAnexo03Controlador2017.setItemDemonstrativoFacade(relatorioRGFAnexo06Calculator2017.getItemDemonstrativoFacade());
        relatorioRGFAnexo03Controlador2017.setRelatorioRGFAnexo03Calculator2017(relatorioRGFAnexo06Calculator2017.getRelatorioRGFAnexo03Calculator2017());
        lista = relatorioRGFAnexo03Controlador2017.geraDadosRelatorioAnexo3();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo06Calculator2017.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 4);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo07Item it = new RGFAnexo07Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (it.getDescricao().equals("Total das Garantias Concedidas")) {
                    for (RGFAnexo03Item itemAnexo03 : lista) {
                        if (itemAnexo03.getDescricao().equals("TOTAL GARANTIAS CONCEDIDAS (III) = (I + II)")) {
                            if (quadrimestre == 1) {
                                it.setValorColuna1(itemAnexo03.getPrimeiroQuadrimestre());
                            } else if (quadrimestre == 2) {
                                it.setValorColuna1(itemAnexo03.getSegundoQuadrimestre());
                            } else if (quadrimestre == 3) {
                                it.setValorColuna1(itemAnexo03.getTerceiroQuadrimestre());
                            }
                        } else if (itemAnexo03.getDescricao().equals("% do TOTAL DAS GARANTIAS sobre a RCL")) {
                            if (quadrimestre == 1) {
                                it.setValorColuna2(itemAnexo03.getPrimeiroQuadrimestre());
                            } else if (quadrimestre == 2) {
                                it.setValorColuna2(itemAnexo03.getSegundoQuadrimestre());
                            } else if (quadrimestre == 3) {
                                it.setValorColuna2(itemAnexo03.getTerceiroQuadrimestre());
                            }
                        }
                    }
                } else if (item.getNome().equals("Limite Definido por Resolução do Senado Federal")) {
                    for (RGFAnexo03Item itemAnexo03 : lista) {
                        if (itemAnexo03.getDescricao().equals("LIMITE DEFINIDO POR RESOLUÇÃO DO SENADO FEDERAL - 22%")) {
                            if (quadrimestre == 1) {
                                it.setValorColuna1(itemAnexo03.getPrimeiroQuadrimestre());
                            } else if (quadrimestre == 2) {
                                it.setValorColuna1(itemAnexo03.getSegundoQuadrimestre());
                            } else if (quadrimestre == 3) {
                                it.setValorColuna1(itemAnexo03.getTerceiroQuadrimestre());
                            }
                            it.setValorColuna2(new BigDecimal(22));
                        }
                    }
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RGFAnexo07Item> gerarDadosAnexoQuatro() throws ParseException {
        List<RGFAnexo07Item> toReturn = new ArrayList<>();
        List<RGFAnexo04ItemApuracao> lista = new ArrayList<>();
        relatorioRGFAnexo04Controlador2017 = new RelatorioRGFAnexo04Controlador2017();
        relatorioRGFAnexo04Controlador2017.setDataFinal(dataFinal);
        relatorioRGFAnexo04Controlador2017.setDataInicial(dataInicial);
        relatorioRGFAnexo04Controlador2017.setQuadrimestre(quadrimestre);
        relatorioRGFAnexo04Controlador2017.setSistemaControlador(sistemaControlador);
        relatorioRGFAnexo04Controlador2017.setItemDemonstrativoFacade(relatorioRGFAnexo06Calculator2017.getItemDemonstrativoFacade());
        relatorioRGFAnexo04Controlador2017.setRelatorioRGFAnexo04Calculator2017(relatorioRGFAnexo06Calculator2017.getRelatorioRGFAnexo04Calculator2017());
        lista = relatorioRGFAnexo04Controlador2017.preparaDadosAnexo4();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo06Calculator2017.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 5);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo07Item it = new RGFAnexo07Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (it.getDescricao().equals("Operações de Créditos Externas e Internas")) {
                    for (RGFAnexo04ItemApuracao itemAnexo04 : lista) {
                        if (itemAnexo04.getDescricao().equals("TOTAL CONSIDERADO PARA FINS DA APURAÇÃO DO CUMPRIMENTO DO LIMITE (IV)= (Ia + III)")) {
                            it.setValorColuna1(itemAnexo04.getValor());
                            it.setValorColuna2(itemAnexo04.getPercentual().multiply(new BigDecimal(100)));
                        }
                    }
                } else if (it.getDescricao().equals("Operações de Crédito por Antecipação da Receita")) {
                    for (RGFAnexo04ItemApuracao itemAnexo04 : lista) {
                        if (itemAnexo04.getDescricao().equals("OPERAÇÕES DE CRÉDITO POR ANTECIPAÇÃO DA RECEITA ORÇAMENTÁRIA")) {
                            it.setValorColuna1(itemAnexo04.getValor());
                            it.setValorColuna2(itemAnexo04.getPercentual().multiply(new BigDecimal(100)));
                        }
                    }
                } else if (it.getDescricao().equals("Limite Definido p/ Senado Federal para Op. de Crédito Externas e Internas")) {
                    for (RGFAnexo04ItemApuracao itemAnexo04 : lista) {
                        if (itemAnexo04.getDescricao().equals("LIMITE GERAL DEFINIDO POR RESOLUÇÃO DO SENADO FEDERAL PARA AS OPERAÇÕES DE CRÉDITO INTERNAS E EXTERNAS")) {
                            it.setValorColuna1(itemAnexo04.getValor());
                            it.setValorColuna2(itemAnexo04.getPercentual().multiply(new BigDecimal(100)));
                        }
                    }
                } else if (it.getDescricao().equals("Limite Definido p/ Senado Federal para Op. de Crédito por Antecipação da Receita")) {
                    for (RGFAnexo04ItemApuracao itemAnexo04 : lista) {
                        if (itemAnexo04.getDescricao().equals("LIMITE DEFINIDO POR RESOLUÇÃO DO SENADO FEDERAL PARA AS OPERAÇÕES DE CRÉDITO POR ANTECIPAÇÃO DA RECEITA ORÇAMENTÁRIA")) {
                            it.setValorColuna1(itemAnexo04.getValor());
                            it.setValorColuna2(itemAnexo04.getPercentual().multiply(new BigDecimal(100)));
                        }
                    }
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RGFAnexo07Item> gerarDadosAnexoCinco() {
        List<RGFAnexo07Item> toReturn = new ArrayList<>();
        List<RGFAnexo05Item> lista = new ArrayList<>();
        relatorioRGFAnexo05Controlador2017 = new RelatorioRGFAnexo05Controlador2017();
        relatorioRGFAnexo05Controlador2017.setDataFinal(dataFinal);
        relatorioRGFAnexo05Controlador2017.setDataInicial(dataInicial);
        relatorioRGFAnexo05Controlador2017.setQuadrimestre(quadrimestre);
        relatorioRGFAnexo05Controlador2017.setEsferas(RelatorioRGFAnexo05Controlador2017.Esferas.valueOf(esferas.name()));
        relatorioRGFAnexo05Controlador2017.setSistemaControlador(sistemaControlador);
        relatorioRGFAnexo05Controlador2017.setItemDemonstrativoFacade(relatorioRGFAnexo06Calculator2017.getItemDemonstrativoFacade());
        relatorioRGFAnexo05Controlador2017.setRelatorioFacade(relatorioRGFAnexo06Calculator2017.getRelatorioDemonstrativoDisponibilidadeRecursoFacade());
        relatorioRGFAnexo05Controlador2017.setRelatorioRGFAnexo05Calculator2017(relatorioRGFAnexo06Calculator2017.getRelatorioRGFAnexo05Calculator2017());
        lista = relatorioRGFAnexo05Controlador2017.prepararDadosAnexo5();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo06Calculator2017.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 6);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo07Item it = new RGFAnexo07Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (it.getDescricao().equals("Valor Total")) {
                    System.out.println(lista);
                    for (RGFAnexo05Item itemAnexo05 : lista) {
                        System.out.println(itemAnexo05.getDescricao());
                        if (itemAnexo05.getDescricao().equals("TOTAL (III) = (I + II)")) {
                            it.setValorColuna1(itemAnexo05.getValorColuna7());
                            it.setValorColuna2(itemAnexo05.getValorColuna6());
                        }
                    }
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }


    public void gerarRelatorio() throws IOException, JRException, ParseException {
        try {
            recuperarRelatorio();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(prepararDados());
            HashMap parameters = montarParametros();
            gerarRelatorioComDadosEmCollection(getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarRelatorio()  {
        try {
            recuperarRelatorio();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(prepararDados());
            HashMap parameters = montarParametros();
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(sistemaControlador.getExercicioCorrente());
            anexo.setNome("Anexo 6 - Demonstrativo Simplificado do Relatório de Gestão Fiscal");
            anexo.setMes(Mes.getMesToInt(Integer.valueOf(mesFinal)));
            anexo.setTipo(PortalTransparenciaTipo.RGF);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void recuperarRelatorio() {
        relatoriosItemDemonst = relatorioRGFAnexo06Calculator2017.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 6", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        montarData();
        rcl = calcularRcl();
    }

    private void montarData() {
        if (quadrimestre == 1) {
            dataFinal = "30/04/" + sistemaControlador.getExercicioCorrente().getAno();
            mesFinal = "04";
        } else if (quadrimestre == 2) {
            dataFinal = "31/08/" + sistemaControlador.getExercicioCorrente().getAno();
            mesFinal = "08";
        } else if (quadrimestre == 3) {
            dataFinal = "31/12/" + sistemaControlador.getExercicioCorrente().getAno();
            mesFinal = "12";
        }
    }

    private String getNomeRelatorio() {
        return "RelatorioRGFAnexo06_2017.jasper";
    }

    private HashMap montarParametros() throws ParseException {
        HashMap parameters = new HashMap();

        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno());
        if (quadrimestre == 1) {
            parameters.put("DATAINICIAL", "JANEIRO");
            parameters.put("DATAFINAL", " ABRIL DE " + sistemaControlador.getExercicioCorrente().getAno());
        } else if (quadrimestre == 2) {
            parameters.put("DATAINICIAL", "JANEIRO");
            parameters.put("DATAFINAL", " AGOSTO DE " + sistemaControlador.getExercicioCorrente().getAno());
        } else if (quadrimestre == 3) {
            parameters.put("DATAINICIAL", "JANEIRO");
            parameters.put("DATAFINAL", "DEZEMBRO DE " + sistemaControlador.getExercicioCorrente().getAno());
        }
        parameters.put("GRUPO1", gerarDadosRCL());
        parameters.put("GRUPO2", gerarDadosAnexoUm());
        if (esferas.name().equals("EXECUTIVO")) {
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER EXECUTIVO");
            parameters.put("GRUPO3", gerarDadosAnexoDois());
            parameters.put("GRUPO4", gerarDadosAnexoTres());
            parameters.put("GRUPO5", gerarDadosAnexoQuatro());
            parameters.put("NOME_RELATORIO", "DEMONSTRATIVO SIMPLIFICADO DO RELATÓRIO DE GESTÃO FISCAL");
        } else if (esferas.name().equals("LEGISLATIVO")) {
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER LEGISLATIVO");
            parameters.put("NOME_RELATORIO", "DEMONSTRATIVO SIMPLIFICADO DO RELATÓRIO DE GESTÃO FISCAL");
        } else {
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            parameters.put("GRUPO3", gerarDadosAnexoDois());
            parameters.put("GRUPO4", gerarDadosAnexoTres());
            parameters.put("GRUPO5", gerarDadosAnexoQuatro());
            parameters.put("NOME_RELATORIO", "DEMONSTRATIVO CONSOLIDADO SIMPLIFICADO DO RELATÓRIO DE GESTÃO FISCAL");
        }
        if (quadrimestre == 3) {
            parameters.put("GRUPO6", gerarDadosAnexoCinco());
        }
        parameters.put("IMAGEM", getCaminhoImagem());
        return parameters;
    }

    public BigDecimal calcularRcl() {
        try {
            BigDecimal toReturn = BigDecimal.ZERO;
            RelatoriosItemDemonst relatorio = relatorioRGFAnexo06Calculator2017.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo06Calculator2017.getItemDemonstrativoCalculator().recuperarItemDemonstrativoPeloNomeERelatorio("RECEITA CORRENTE LÍQUIDA", sistemaControlador.getExercicioCorrente(), relatorio);
            GregorianCalendar dataCalendar = new GregorianCalendar(sistemaControlador.getExercicioCorrente().getAno(), new Integer(mesFinal) - 1, new Integer(1));
            String data = DataUtil.getDataFormatada(dataCalendar.getTime(), "MM/yyyy");
            for (int i = 1; i <= 12; i++) {
                toReturn = toReturn.add(relatorioRGFAnexo06Calculator2017.getRelatorioRREOAnexo03Calculator().recuperarValorPeloMesAnterior(itemDemonstrativo, data, relatorio, sistemaControlador.getExercicioCorrente()));
                data = alteraMes(data);
            }
            return toReturn;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    private String alteraMes(String data) {
        Integer mes = Integer.parseInt(data.substring(0, 2));
        Integer ano = Integer.parseInt(data.substring(3, 7));
        if (mes != 1) {
            mes += -1;
        } else {
            mes = 12;
            ano += -1;
        }
        String toReturn;
        if (mes < 10) {
            toReturn = "0" + mes + "/" + ano;
        } else {
            toReturn = mes + "/" + ano;
        }
        return toReturn;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Integer getQuadrimestre() {
        return quadrimestre;
    }

    public void setQuadrimestre(Integer quadrimestre) {
        this.quadrimestre = quadrimestre;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public RelatoriosItemDemonst getRelatoriosItemDemonst() {
        return relatoriosItemDemonst;
    }

    public void setRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst) {
        this.relatoriosItemDemonst = relatoriosItemDemonst;
    }

    public List<ItemDemonstrativoComponente> getItens() {
        return itens;
    }

    public void setItens(List<ItemDemonstrativoComponente> itens) {
        this.itens = itens;
    }

    public Esferas getEsferas() {
        return esferas;
    }

    public void setEsferas(Esferas esferas) {
        this.esferas = esferas;
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public BigDecimal getRcl() {
        return rcl;
    }

    public void setRcl(BigDecimal rcl) {
        this.rcl = rcl;
    }

    public enum Esferas {

        CONSOLIDADO("Consolidado"),
        EXECUTIVO("Executivo"),
        LEGISLATIVO("Legislativo");
        private String descricao;

        private Esferas(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }
}
