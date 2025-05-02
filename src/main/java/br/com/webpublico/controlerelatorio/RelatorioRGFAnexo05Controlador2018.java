package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.RGFAnexo05Item;
import br.com.webpublico.enums.*;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo05Facade;
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
import java.util.HashMap;
import java.util.List;

/**
 * Created by mateus on 26/04/18.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo5-2018", pattern = "/relatorio/rgf/anexo5/2018", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo05-2018.xhtml")
})
public class RelatorioRGFAnexo05Controlador2018 extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private RelatorioRGFAnexo05Facade relatorioRGFAnexo05Facade;
    private Mes mesFinal;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private List<ItemDemonstrativoComponente> itens;
    @Enumerated(EnumType.STRING)
    private EsferaDoPoder esferaDoPoder;
    private ItemDemonstrativoFiltros filtros;

    public RelatorioRGFAnexo05Controlador2018() {
        itens = Lists.newArrayList();
    }

    @URLAction(mappingId = "relatorio-rgf-anexo5-2018", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        esferaDoPoder = null;
    }

    public List<SelectItem> getEsferasDoPoder() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "Consolidado"));
        toReturn.add(new SelectItem(EsferaDoPoder.EXECUTIVO, EsferaDoPoder.EXECUTIVO.getDescricao()));
        toReturn.add(new SelectItem(EsferaDoPoder.LEGISLATIVO, EsferaDoPoder.LEGISLATIVO.getDescricao()));
        return toReturn;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(Mes.ABRIL, "Jan - Abr"));
        retorno.add(new SelectItem(Mes.AGOSTO, "Mai - Ago"));
        retorno.add(new SelectItem(Mes.DEZEMBRO, "Set - Dez"));
        return retorno;
    }

    private void adicionaParametros(List<ParametrosRelatorios> parametros, String dataFinal) {
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, sistemaControlador.getExercicioCorrente().getId(), null, 0, false));
        parametros.add(new ParametrosRelatorios(" SLD.DATASALDO ", ":DATAFINAL", null, OperacaoRelatorio.MENOR_IGUAL, dataFinal, null, 1, true));
        parametros.add(new ParametrosRelatorios(" SLD.DATASALDO ", ":DATAINICIAL", null, OperacaoRelatorio.MENOR_IGUAL, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), null, 10, true));
        parametros.add(new ParametrosRelatorios(" SALDO.DATASALDO ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 3, true));
        parametros.add(new ParametrosRelatorios(" trunc(emp.dataempenho) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 4, true));
        parametros.add(new ParametrosRelatorios(" trunc(est.dataestorno) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 5, true));
        parametros.add(new ParametrosRelatorios(" trunc(liq.dataliquidacao) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 6, true));
        parametros.add(new ParametrosRelatorios(" trunc(pag.datapagamento) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 7, true));
        parametros.add(new ParametrosRelatorios(" trunc(re.datareceita) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 8, true));
        parametros.add(new ParametrosRelatorios(" trunc(pg.datapagto) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 9, true));
        parametros.add(new ParametrosRelatorios(" trunc(lanc.datalancamento) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 12, true));
        parametros.add(new ParametrosRelatorios(" trunc(transf.DATATRANSFERENCIA) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 11, true));
        parametros.add(new ParametrosRelatorios(" trunc(ajuste.DATAAJUSTE) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 13, true));
        parametros.add(new ParametrosRelatorios(" trunc(LIB.DATALIBERACAO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 14, true));
    }

    public void gerarRelatorio() {
        try {
            List<ParametrosRelatorios> parametros = montarParametrosRelatorio();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(criarListaComUmItem());
            HashMap parameters = montarParametros(parametros);
            gerarRelatorioComDadosEmCollection(getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarRelatorio() {
        try {
            List<ParametrosRelatorios> parametros = montarParametrosRelatorio();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(criarListaComUmItem());
            HashMap parameters = montarParametros(parametros);
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(sistemaControlador.getExercicioCorrente());
            anexo.setNome("Anexo 5 - Demonstrativo da Disponibilidade de Caixa");
            anexo.setMes(mesFinal);
            anexo.setTipo(PortalTransparenciaTipo.RGF);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "RelatorioRGFAnexo05_2018.jasper";
    }

    private List<ParametrosRelatorios> montarParametrosRelatorio() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        relatoriosItemDemonst = relatorioRGFAnexo05Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 5", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        adicionaParametros(parametros, getData());
        filtros = new ItemDemonstrativoFiltros();
        filtros.setParametros(parametros);
        filtros.setRelatorio(relatoriosItemDemonst);
        filtros.setExercicio(getExercicioCorrente());
        filtros.setApresentacaoRelatorio(ApresentacaoRelatorio.ORGAO);
        return parametros;
    }

    private String getData() {
        return Util.getDiasMes(mesFinal.getNumeroMes(), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal.getNumeroMesString() + "/" + sistemaControlador.getExercicioCorrente().getAno();
    }

    private HashMap montarParametros(List<ParametrosRelatorios> parametros) {
        limparHashs();
        HashMap parameters = new HashMap();
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno());
        parameters.put("DATAINICIAL", getMesInicial().getDescricao().toUpperCase());
        parameters.put("DATAFINAL", " " + mesFinal.getDescricao().toUpperCase() + " DE " + sistemaControlador.getExercicioCorrente().getAno());
        parameters.put("IMAGEM", getCaminhoImagem());
        if (EsferaDoPoder.EXECUTIVO.equals(esferaDoPoder)) {
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER EXECUTIVO");
            parameters.put("NOMERELATORIO", "DEMONSTRATIVO DA DISPONIBILIDADE DE CAIXA E DOS RESTOS A PAGAR");
            parametros.add(new ParametrosRelatorios("vw.esferadopoder", ":esfera", null, OperacaoRelatorio.LIKE, esferaDoPoder.name(), null, 2, false));
        } else if (EsferaDoPoder.LEGISLATIVO.equals(esferaDoPoder)) {
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER LEGISLATIVO");
            parameters.put("NOMERELATORIO", "DEMONSTRATIVO DA DISPONIBILIDADE DE CAIXA E DOS RESTOS A PAGAR");
            parametros.add(new ParametrosRelatorios("vw.esferadopoder", ":esfera", null, OperacaoRelatorio.LIKE, esferaDoPoder.name(), null, 2, false));
        } else {
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            parameters.put("NOMERELATORIO", "DEMONSTRATIVO CONSOLIDADO DA DISPONIBILIDADE DE CAIXA E DOS RESTOS A PAGAR");
        }
        parameters.put("GRUPO1", prepararGrupo1(parametros));
        return parameters;
    }

    private void limparHashs() {
        relatorioRGFAnexo05Facade.getRelatorioDDRFacade().limparHash();
        relatorioRGFAnexo05Facade.getRelatorioDDRFacade().limparHashItensRecuperados();
        relatorioRGFAnexo05Facade.limparHash();
        relatorioRGFAnexo05Facade.limparHashItensRecuperados();
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

    public List<RGFAnexo05Item> prepararDadosAnexo5(List<ParametrosRelatorios> parametros) {
        relatoriosItemDemonst = relatorioRGFAnexo05Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 5", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativo> itensDemonstrativos = relatorioRGFAnexo05Facade.getItemDemonstrativoFacade().buscarItensPorExercicioAndRelatorio(sistemaControlador.getExercicioCorrente(), "", "Anexo 5", TipoRelatorioItemDemonstrativo.RGF);
        itens = Lists.newArrayList();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
        limparHashs();
        List<ParametrosRelatorios> parametrosRelatorio = montarParametrosRelatorio();
        parametrosRelatorio.addAll(parametros);
        return prepararGrupo1(parametrosRelatorio);
    }

    private List<RGFAnexo05Item> criarListaComUmItem() {
        List<RGFAnexo05Item> toReturn = Lists.newArrayList();
        toReturn.add(new RGFAnexo05Item(""));
        return toReturn;
    }

    private List<RGFAnexo05Item> prepararGrupo1(List<ParametrosRelatorios> parametros) {
        List<RGFAnexo05Item> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente it : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo05Facade.getItemDemonstrativoFacade().recuperaPorGrupo(it.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo05Item item = new RGFAnexo05Item();
                item.setDescricao(it.getDescricaoComEspacos());
                item.setValorColuna1(relatorioRGFAnexo05Facade.getRelatorioDDRFacade().calcularValor(itemDemonstrativo.toDto(), filtros.toDto(), TipoCalculoItemDemonstrativo.DISPONIBILIDADE_CAIXA_BRUTA));
                item.setValorColuna2(relatorioRGFAnexo05Facade.calcularRestoLiquidadosNaoPagosProcessadosExerciciosAnteriores(itemDemonstrativo, relatoriosItemDemonst, parametros));
                item.setValorColuna3(relatorioRGFAnexo05Facade.calcularValor(itemDemonstrativo.toDto(), filtros.toDto(), TipoCalculoItemDemonstrativo.RESTOS_A_PAGAR_PROCESSADOS_LIQUIDADOS_NAO_PAGOS));
                item.setValorColuna4(relatorioRGFAnexo05Facade.calcularRestoEmpenhoNaoProcessadosExerciciosAnteriores(itemDemonstrativo, relatoriosItemDemonst, parametros));
                BigDecimal valorColuna5 = relatorioRGFAnexo05Facade.getRelatorioDDRFacade().calcularValor(itemDemonstrativo.toDto(), filtros.toDto(), TipoCalculoItemDemonstrativo.DISPONIBILIDADE_COMPROMETIDA_LIQUIDACAO);
                valorColuna5 = valorColuna5.add(relatorioRGFAnexo05Facade.getRelatorioDDRFacade().calcularValor(itemDemonstrativo.toDto(), filtros.toDto(), TipoCalculoItemDemonstrativo.DISPONIBILIDADE_COMPROMETIDA_CONSIGNACAO));
                valorColuna5 = valorColuna5.add(relatorioRGFAnexo05Facade.getRelatorioDDRFacade().calcularValor(itemDemonstrativo.toDto(), filtros.toDto(), TipoCalculoItemDemonstrativo.DISPONIBILIDADE_COMPROMETIDA_COMPENSATORIAS));
                valorColuna5 = valorColuna5.subtract(item.getValorColuna2()).subtract(item.getValorColuna3());
                item.setValorColuna5(valorColuna5);
                item.setValorColuna6(BigDecimal.ZERO);
                item.setValorColuna7(item.getValorColuna1().subtract(item.getValorColuna2().add(item.getValorColuna3()).add(item.getValorColuna4()).add(item.getValorColuna5())));
                item.setValorColuna8(relatorioRGFAnexo05Facade.calcularRestoEmpenhoNaoProcessados(itemDemonstrativo, relatoriosItemDemonst, parametros));
                item.setValorColuna9(BigDecimal.ZERO);
                toReturn.add(item);
            }
        }
        return toReturn;
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

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public EsferaDoPoder getEsferaDoPoder() {
        return esferaDoPoder;
    }

    public void setEsferaDoPoder(EsferaDoPoder esferaDoPoder) {
        this.esferaDoPoder = esferaDoPoder;
    }

    public ItemDemonstrativoFiltros getFiltros() {
        return filtros;
    }

    public void setFiltros(ItemDemonstrativoFiltros filtros) {
        this.filtros = filtros;
    }
}
