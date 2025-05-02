package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo06Facade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mateus on 27/04/18.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo6-2019", pattern = "/relatorio/rgf/anexo6/2019/", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo06-2019.xhtml")
})
public class RelatorioRGFAnexo06Controlador2019 extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private RelatorioRGFAnexo06Facade relatorioRGFAnexo06Facade;
    private Mes mesFinal;
    private BigDecimal rcl;
    private BigDecimal rclAjustada;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private List<ItemDemonstrativoComponente> itens;
    @Enumerated(EnumType.STRING)
    private EsferaDoPoder esferaDoPoder;
    private boolean modeloNovo;

    public RelatorioRGFAnexo06Controlador2019() {
        itens = Lists.newArrayList();
    }

    @URLAction(mappingId = "relatorio-rgf-anexo6-2019", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        esferaDoPoder = null;
        mesFinal = Mes.ABRIL;
        rcl = BigDecimal.ZERO;
        rclAjustada = BigDecimal.ZERO;
        modeloNovo = true;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(Mes.ABRIL, "Jan - Abr"));
        retorno.add(new SelectItem(Mes.AGOSTO, "Mai - Ago"));
        retorno.add(new SelectItem(Mes.DEZEMBRO, "Set - Dez"));
        return retorno;
    }

    public List<SelectItem> getEsferasDoPoder() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "Consolidado"));
        toReturn.add(new SelectItem(EsferaDoPoder.EXECUTIVO, EsferaDoPoder.EXECUTIVO.getDescricao()));
        toReturn.add(new SelectItem(EsferaDoPoder.LEGISLATIVO, EsferaDoPoder.LEGISLATIVO.getDescricao()));
        return toReturn;
    }

    private Mes getMesInicial() {
        if (Mes.ABRIL.equals(mesFinal)) {
            return Mes.JANEIRO;
        } else if (Mes.AGOSTO.equals(mesFinal)) {
            return Mes.MAIO;
        } else {
            return Mes.SETEMBRO;
        }
    }

    private List<RGFAnexo07Item> criarListaComUmItem() {
        List<RGFAnexo07Item> toReturn = Lists.newArrayList();
        toReturn.add(new RGFAnexo07Item(""));
        return toReturn;
    }

    private List<RGFAnexo07Item> gerarDadosRCL() {
        List<RGFAnexo07Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo06Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo07Item it = new RGFAnexo07Item();
                it.setDescricao(item.getDescricaoComEspacos());
                if (item.getOrdem() == 1) {
                    it.setValorColuna1(calcularRcl());
                    rcl = it.getValorColuna1();
                } else if (item.getOrdem() == 2) {
                    it.setValorColuna1(calcularRclAjustada());
                    rclAjustada = it.getValorColuna1();
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<ParametrosRelatorios> criarParametros(Integer local) {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        if (esferaDoPoder != null) {
            parametros.add(new ParametrosRelatorios(" vw.esferadopoder ", ":esferaDoPoder", null, OperacaoRelatorio.IGUAL, esferaDoPoder.name(), null, local, false));
        }
        return parametros;
    }

    private List<RGFAnexo07Item> gerarDadosAnexoUm() {
        BigDecimal percentual = BigDecimal.ZERO;
        BigDecimal percentualPrudencial = BigDecimal.ZERO;
        BigDecimal percentualLimite = BigDecimal.ZERO;
        BigDecimal percentualDespesa = BigDecimal.ZERO;
        BigDecimal limiteMaximo = BigDecimal.ZERO;
        BigDecimal limitePrudencial = BigDecimal.ZERO;
        BigDecimal limiteAlerta = BigDecimal.ZERO;
        BigDecimal despesaTotalComPessoal = BigDecimal.ZERO;
        List<RGFAnexo07Item> toReturn = Lists.newArrayList();
        RelatorioRGFAnexo01Controlador relatorioRGFAnexo01Controlador = (RelatorioRGFAnexo01Controlador) Util.getControladorPeloNome("relatorioRGFAnexo01Controlador");
        relatorioRGFAnexo01Controlador.limparCampos();
        relatorioRGFAnexo01Controlador.setMes(mesFinal);
        relatorioRGFAnexo01Controlador.setEsferaDoPoder(esferaDoPoder);
        despesaTotalComPessoal = relatorioRGFAnexo01Controlador.buscarDespesaTotalComPessoa(criarParametros(1));
        if (EsferaDoPoder.EXECUTIVO.equals(esferaDoPoder)) {
            percentual = BigDecimal.valueOf(54);
            percentualPrudencial = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.05)));
            percentualLimite = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.1)));
        } else if (EsferaDoPoder.LEGISLATIVO.equals(esferaDoPoder)) {
            percentual = BigDecimal.valueOf(6);
            percentualPrudencial = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.05)));
            percentualLimite = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.1)));
        } else {
            percentual = BigDecimal.valueOf(60);
            percentualPrudencial = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.05)));
            percentualLimite = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.1)));
        }
        if (rclAjustada.compareTo(BigDecimal.ZERO) != 0) {
            percentualDespesa = despesaTotalComPessoal.divide(rclAjustada, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100));
            limiteMaximo = rclAjustada.multiply(percentual).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
            limitePrudencial = rclAjustada.multiply(percentualPrudencial).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
            limiteAlerta = rclAjustada.multiply(percentualLimite).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
        }
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo06Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo07Item it = new RGFAnexo07Item();
                it.setDescricao(item.getDescricaoComEspacos());
                switch (item.getOrdem()) {
                    case 1:
                        it.setValorColuna1(despesaTotalComPessoal);
                        it.setValorColuna2(percentualDespesa);
                        break;
                    case 2:
                        it.setValorColuna1(limiteMaximo);
                        it.setValorColuna2(percentual);
                        break;
                    case 3:
                        it.setValorColuna1(limitePrudencial);
                        it.setValorColuna2(percentualPrudencial);
                        break;
                    default:
                        it.setValorColuna1(limiteAlerta);
                        it.setValorColuna2(percentualLimite);
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RGFAnexo07Item> gerarDadosAnexoDois() {
        List<RGFAnexo07Item> toReturn = Lists.newArrayList();
        RelatorioRGFAnexo02Controlador relatorioRGFAnexo02Controlador = (RelatorioRGFAnexo02Controlador) Util.getControladorPeloNome("relatorioRGFAnexo02Controlador");
        relatorioRGFAnexo02Controlador.limparCampos();
        relatorioRGFAnexo02Controlador.setMes(mesFinal);
        List<RGFAnexo02Item> itensAnexo02 = relatorioRGFAnexo02Controlador.buscarDadosGrupo1PopulandoItens();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo06Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 3);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo07Item it = new RGFAnexo07Item();
                it.setDescricao(item.getDescricaoComEspacos());
                if (item.getOrdem() == 1) {
                    for (RGFAnexo02Item itemAnexo02 : itensAnexo02) {
                        if ("DÍVIDA CONSOLIDADA LÍQUIDA² (DCL) (III) = (I - II)".equals(itemAnexo02.getDescricao().trim())) {
                            if (Mes.ABRIL.equals(mesFinal)) {
                                it.setValorColuna1(itemAnexo02.getPrimeiroQuadrimestre());
                            } else if (Mes.AGOSTO.equals(mesFinal)) {
                                it.setValorColuna1(itemAnexo02.getSegundoQuadrimestre());
                            } else {
                                it.setValorColuna1(itemAnexo02.getTerceiroQuadrimestre());
                            }
                        } else if ("% da DCL sobre a RCL (III/RCL)".equals(itemAnexo02.getDescricao())) {
                            if (Mes.ABRIL.equals(mesFinal)) {
                                it.setValorColuna2(itemAnexo02.getPrimeiroQuadrimestre());
                            } else if (Mes.AGOSTO.equals(mesFinal)) {
                                it.setValorColuna2(itemAnexo02.getSegundoQuadrimestre());
                            } else {
                                it.setValorColuna2(itemAnexo02.getTerceiroQuadrimestre());
                            }
                        }
                    }
                } else if (item.getOrdem() == 2) {
                    for (RGFAnexo02Item itemAnexo02 : itensAnexo02) {
                        if ("LIMITE DEFINIDO POR RESOLUÇÃO DO SENADO FEDERAL - 120%".equals(itemAnexo02.getDescricao().trim())) {
                            if (Mes.ABRIL.equals(mesFinal)) {
                                it.setValorColuna1(itemAnexo02.getPrimeiroQuadrimestre());
                            } else if (Mes.AGOSTO.equals(mesFinal)) {
                                it.setValorColuna1(itemAnexo02.getSegundoQuadrimestre());
                            } else {
                                it.setValorColuna1(itemAnexo02.getTerceiroQuadrimestre());
                            }
                            it.setValorColuna2(it.getValorColuna1().divide(rcl, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                        }
                    }
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }


    private List<RGFAnexo07Item> gerarDadosAnexoTres() {
        List<RGFAnexo07Item> toReturn = Lists.newArrayList();
        RelatorioRGFAnexo03Controlador relatorioRGFAnexo03Controlador = (RelatorioRGFAnexo03Controlador) Util.getControladorPeloNome("relatorioRGFAnexo03Controlador");
        relatorioRGFAnexo03Controlador.limparCampos();
        relatorioRGFAnexo03Controlador.setMes(mesFinal);
        List<RGFAnexo03Item> itensAnexo03 = relatorioRGFAnexo03Controlador.buscarDadosPopulandoItens();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo06Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 4);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo07Item it = new RGFAnexo07Item();
                it.setDescricao(item.getDescricaoComEspacos());
                if (item.getOrdem() == 1) {
                    for (RGFAnexo03Item itemAnexo03 : itensAnexo03) {
                        if ("TOTAL GARANTIAS CONCEDIDAS (V) = (I + II + III + IV)".equals(itemAnexo03.getDescricao().trim())) {
                            if (Mes.ABRIL.equals(mesFinal)) {
                                it.setValorColuna1(itemAnexo03.getPrimeiroQuadrimestre());
                            } else if (Mes.AGOSTO.equals(mesFinal)) {
                                it.setValorColuna1(itemAnexo03.getSegundoQuadrimestre());
                            } else {
                                it.setValorColuna1(itemAnexo03.getTerceiroQuadrimestre());
                            }
                        } else if ("% do TOTAL DAS GARANTIAS sobre a RCL".equals(itemAnexo03.getDescricao().trim())) {
                            if (Mes.ABRIL.equals(mesFinal)) {
                                it.setValorColuna2(itemAnexo03.getPrimeiroQuadrimestre());
                            } else if (Mes.AGOSTO.equals(mesFinal)) {
                                it.setValorColuna2(itemAnexo03.getSegundoQuadrimestre());
                            } else {
                                it.setValorColuna2(itemAnexo03.getTerceiroQuadrimestre());
                            }
                        }
                    }
                } else if (item.getOrdem() == 2) {
                    for (RGFAnexo03Item itemAnexo03 : itensAnexo03) {
                        if ("LIMITE DEFINIDO POR RESOLUÇÃO DO SENADO FEDERAL - 22%".equals(itemAnexo03.getDescricao().trim())) {
                            if (Mes.ABRIL.equals(mesFinal)) {
                                it.setValorColuna1(itemAnexo03.getPrimeiroQuadrimestre());
                            } else if (Mes.AGOSTO.equals(mesFinal)) {
                                it.setValorColuna1(itemAnexo03.getSegundoQuadrimestre());
                            } else {
                                it.setValorColuna1(itemAnexo03.getTerceiroQuadrimestre());
                            }
                            it.setValorColuna2(BigDecimal.valueOf(22));
                            break;
                        }
                    }
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RGFAnexo07Item> gerarDadosAnexoQuatro() {
        List<RGFAnexo07Item> toReturn = Lists.newArrayList();
        RelatorioRGFAnexo04Controlador relatorioRGFAnexo04Controlador = (RelatorioRGFAnexo04Controlador) Util.getControladorPeloNome("relatorioRGFAnexo04Controlador");
        relatorioRGFAnexo04Controlador.limparCampos();
        relatorioRGFAnexo04Controlador.instanciarItemDemonstrativoFiltros();
        relatorioRGFAnexo04Controlador.setMes(mesFinal);
        List<RGFAnexo04ItemApuracao> itensAnexo04 = relatorioRGFAnexo04Controlador.preparaDadosAnexo4();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo06Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 5);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo07Item it = new RGFAnexo07Item();
                it.setDescricao(item.getDescricaoComEspacos());
                switch (item.getOrdem()) {
                    case 1:
                        for (RGFAnexo04ItemApuracao itemAnexo04 : itensAnexo04) {
                            if ("TOTAL CONSIDERADO PARA FINS DA APURAÇÃO DO CUMPRIMENTO DO LIMITE (VI)= (IIIa + V - Ia - IIa)".equals(itemAnexo04.getDescricao())) {
                                it.setValorColuna1(itemAnexo04.getValor());
                                it.setValorColuna2(itemAnexo04.getPercentual().multiply(BigDecimal.valueOf(100)));
                                break;
                            }
                        }
                        break;
                    case 2:
                        for (RGFAnexo04ItemApuracao itemAnexo04 : itensAnexo04) {
                            if ("LIMITE GERAL DEFINIDO POR RESOLUÇÃO DO SENADO FEDERAL PARA AS OPERAÇÕES DE CRÉDITO INTERNAS E EXTERNAS".equals(itemAnexo04.getDescricao())) {
                                it.setValorColuna1(itemAnexo04.getValor());
                                it.setValorColuna2(itemAnexo04.getPercentual().multiply(BigDecimal.valueOf(100)));
                                break;
                            }
                        }
                        break;
                    case 3:
                        for (RGFAnexo04ItemApuracao itemAnexo04 : itensAnexo04) {
                            if ("OPERAÇÕES DE CRÉDITO POR ANTECIPAÇÃO DA RECEITA ORÇAMENTÁRIA".equals(itemAnexo04.getDescricao())) {
                                it.setValorColuna1(itemAnexo04.getValor());
                                it.setValorColuna2(itemAnexo04.getPercentual().multiply(BigDecimal.valueOf(100)));
                                break;
                            }
                        }
                        break;
                    default:
                        for (RGFAnexo04ItemApuracao itemAnexo04 : itensAnexo04) {
                            if ("LIMITE DEFINIDO POR RESOLUÇÃO DO SENADO FEDERAL PARA AS OPERAÇÕES DE CRÉDITO POR ANTECIPAÇÃO DA RECEITA ORÇAMENTÁRIA".equals(itemAnexo04.getDescricao())) {
                                it.setValorColuna1(itemAnexo04.getValor());
                                it.setValorColuna2(itemAnexo04.getPercentual().multiply(BigDecimal.valueOf(100)));
                                break;
                            }
                        }
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private List<RGFAnexo07Item> gerarDadosAnexoCinco() {
        List<RGFAnexo07Item> toReturn = Lists.newArrayList();
        RelatorioRGFAnexo05Controlador relatorioRGFAnexo05Controlador = (RelatorioRGFAnexo05Controlador) Util.getControladorPeloNome("relatorioRGFAnexo05Controlador");
        relatorioRGFAnexo05Controlador.limparCampos();
        relatorioRGFAnexo05Controlador.setEsferaDoPoder(esferaDoPoder);
        relatorioRGFAnexo05Controlador.setMes(mesFinal);
        List<RGFAnexo05Item> itensAnexo05 = relatorioRGFAnexo05Controlador.prepararDadosAnexo5(criarParametros(2));
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo06Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 6);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo07Item it = new RGFAnexo07Item();
                it.setDescricao(item.getDescricaoComEspacos());
                for (RGFAnexo05Item itemAnexo05 : itensAnexo05) {
                    if ("TOTAL (III) = (I + II)".equals(itemAnexo05.getDescricao())) {
                        it.setValorColuna1(itemAnexo05.getValorColuna8());
                        it.setValorColuna2(itemAnexo05.getValorColuna10());
                        break;
                    }
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }


    public void gerarRelatorio() {
        try {
            recuperarRelatorio();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(criarListaComUmItem());
            HashMap parameters = montarParametros();
            gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeArquivo(), getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeArquivo() {
        return "LRF-RGF-ANEXO-6-" + getSistemaControlador().getExercicioCorrente().getAno() + "-" + mesFinal.getNumeroMesString() + "-" + (esferaDoPoder != null ? esferaDoPoder.getDescricao().substring(0, 1) : "C");
    }

    public void salvarRelatorio() {
        try {
            recuperarRelatorio();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(criarListaComUmItem());
            HashMap parameters = montarParametros();
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(sistemaControlador.getExercicioCorrente());
            anexo.setNome("Anexo 6 - Demonstrativo Simplificado do Relatório de Gestão Fiscal");
            anexo.setMes(mesFinal);
            anexo.setTipo(PortalTransparenciaTipo.RGF);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void recuperarRelatorio() {
        relatoriosItemDemonst = relatorioRGFAnexo06Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 6", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        rcl = calcularRcl();
        rclAjustada = calcularRcl();
    }

    private String getNomeRelatorio() {
        return "RelatorioRGFAnexo06-2019.jasper";
    }

    private HashMap montarParametros() {
        HashMap parameters = new HashMap();
        parameters.put("MODELO_NOVO", modeloNovo);
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno());
        parameters.put("DATAINICIAL", getMesInicial().getDescricao().toUpperCase());
        parameters.put("DATAFINAL", " " + mesFinal.getDescricao().toUpperCase() + " DE " + sistemaControlador.getExercicioCorrente().getAno());
        parameters.put("GRUPO1", gerarDadosRCL());
        parameters.put("GRUPO2", gerarDadosAnexoUm());
        parameters.put("GRUPO3", gerarDadosAnexoDois());
        parameters.put("GRUPO4", gerarDadosAnexoTres());
        parameters.put("GRUPO5", gerarDadosAnexoQuatro());
        if (EsferaDoPoder.EXECUTIVO.equals(esferaDoPoder)) {
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER EXECUTIVO");
            parameters.put("NOME_RELATORIO", "DEMONSTRATIVO SIMPLIFICADO DO RELATÓRIO DE GESTÃO FISCAL");
        } else if (EsferaDoPoder.LEGISLATIVO.equals(esferaDoPoder)) {
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER LEGISLATIVO");
            parameters.put("NOME_RELATORIO", "DEMONSTRATIVO SIMPLIFICADO DO RELATÓRIO DE GESTÃO FISCAL");
        } else {
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            parameters.put("NOME_RELATORIO", "DEMONSTRATIVO CONSOLIDADO SIMPLIFICADO DO RELATÓRIO DE GESTÃO FISCAL");
        }
        if (Mes.DEZEMBRO.equals(mesFinal)) {
            parameters.put("GRUPO6", gerarDadosAnexoCinco());
        }
        parameters.put("IMAGEM", getCaminhoImagem());
        return parameters;
    }

    private BigDecimal calcularRcl() {
        return relatorioRGFAnexo06Facade.getRelatorioRREOAnexo03Calculator().calcularRcl(mesFinal, getExercicioCorrente());
    }

    private BigDecimal calcularRclAjustada() {
        return relatorioRGFAnexo06Facade.getRelatorioRGFAnexo01Facade().buscarValorRclAjustada(mesFinal);
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
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

    public BigDecimal getRcl() {
        return rcl;
    }

    public void setRcl(BigDecimal rcl) {
        this.rcl = rcl;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public BigDecimal getRclAjustada() {
        return rclAjustada;
    }

    public void setRclAjustada(BigDecimal rclAjustada) {
        this.rclAjustada = rclAjustada;
    }

    public EsferaDoPoder getEsferaDoPoder() {
        return esferaDoPoder;
    }

    public void setEsferaDoPoder(EsferaDoPoder esferaDoPoder) {
        this.esferaDoPoder = esferaDoPoder;
    }
}
