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
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.relatoriofacade.RelatorioDemonstrativoDisponibilidadeRecursoFacade;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo05Calculator2017;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mateus on 04/09/2014.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo5-2017", pattern = "/relatorio/rgf/anexo5/2017", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo05-2017.xhtml")
})
public class RelatorioRGFAnexo05Controlador2017 extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @EJB
    private RelatorioRGFAnexo05Calculator2017 relatorioRGFAnexo05Calculator2017;
    @EJB
    private RelatorioDemonstrativoDisponibilidadeRecursoFacade relatorioFacade;
    private Integer quadrimestre;
    private String dataInicial;
    private String dataFinal;
    private String mesFinal;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private List<ItemDemonstrativoComponente> itens;
    @Enumerated(EnumType.STRING)
    private Esferas esferas;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltros;

    public RelatorioRGFAnexo05Controlador2017() {
        itens = new ArrayList<ItemDemonstrativoComponente>();
    }

    @URLAction(mappingId = "relatorio-rgf-anexo5-2017", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.esferas = Esferas.CONSOLIDADO;
        this.dataInicial = "";
        this.dataFinal = "";
    }

    public List<SelectItem> getListaEsferas() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Esferas es : Esferas.values()) {
            toReturn.add(new SelectItem(es, es.getDescricao()));
        }
        return toReturn;
    }

    private List<ParametrosRelatorios> adicionaParametros(List<ParametrosRelatorios> listaParametros, String dataFinal) {
        listaParametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, sistemaControlador.getExercicioCorrente().getId(), null, 0, false));
        listaParametros.add(new ParametrosRelatorios(" SLD.DATASALDO ", ":DATAFINAL", null, OperacaoRelatorio.MENOR_IGUAL, dataFinal, null, 1, true));
        listaParametros.add(new ParametrosRelatorios(" SLD.DATASALDO ", ":DATAINICIAL", null, OperacaoRelatorio.MENOR_IGUAL, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), null, 10, true));
        listaParametros.add(new ParametrosRelatorios(" SALDO.DATASALDO ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 3, true));
        listaParametros.add(new ParametrosRelatorios(" cast(emp.dataempenho as Date) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 4, true));
        listaParametros.add(new ParametrosRelatorios(" cast(est.dataestorno as Date) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 5, true));
        listaParametros.add(new ParametrosRelatorios(" cast(liq.dataliquidacao as Date) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 6, true));
        listaParametros.add(new ParametrosRelatorios(" cast(pag.datapagamento as Date) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 7, true));
        listaParametros.add(new ParametrosRelatorios(" cast(re.datareceita as Date) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 8, true));
        listaParametros.add(new ParametrosRelatorios(" cast(pg.datapagto as Date) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 9, true));
        listaParametros.add(new ParametrosRelatorios(" cast(lanc.datalancamento as Date) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 12, true));
        listaParametros.add(new ParametrosRelatorios(" cast(transf.DATATRANSFERENCIA as Date) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 11, true));
        listaParametros.add(new ParametrosRelatorios(" cast(ajuste.DATAAJUSTE as Date) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 13, true));
        listaParametros.add(new ParametrosRelatorios(" cast(LIB.DATALIBERACAO as Date) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), dataFinal, 14, true));
        return listaParametros;
    }

    public void gerarRelatorio() {
        try {
            List<ParametrosRelatorios> listaParametros = montarParametrosRelatorio();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(preparaDados());
            HashMap parameters = montarParametros(listaParametros);
            gerarRelatorioComDadosEmCollection(getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarRelatorio() {
        try {
            List<ParametrosRelatorios> listaParametros = montarParametrosRelatorio();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(preparaDados());
            HashMap parameters = montarParametros(listaParametros);
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(sistemaControlador.getExercicioCorrente());
            anexo.setNome("Anexo 5 - Demonstrativo da Disponibilidade de Caixa");
            anexo.setMes(Mes.getMesToInt(Integer.valueOf(mesFinal)));
            anexo.setTipo(PortalTransparenciaTipo.RGF);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "RelatorioRGFAnexo05_2017.jasper";
    }

    private List<ParametrosRelatorios> montarParametrosRelatorio() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 5", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        parametros = adicionaParametros(parametros, getData());
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setParametros(parametros);
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltros.setApresentacaoRelatorio(ApresentacaoRelatorio.ORGAO);
        return parametros;
    }

    private String getData() {
        if (quadrimestre == 1) {
            mesFinal = "4";
            return "30/04/" + sistemaControlador.getExercicioCorrente().getAno();
        } else if (quadrimestre == 2) {
            mesFinal = "8";
            return "31/08/" + sistemaControlador.getExercicioCorrente().getAno();
        } else if (quadrimestre == 3) {
            mesFinal = "12";
            return "31/12/" + sistemaControlador.getExercicioCorrente().getAno();
        }
        return "";
    }

    private HashMap montarParametros(List<ParametrosRelatorios> listaParametros) {
        relatorioFacade.limparHash();
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
        parameters.put("IMAGEM", getCaminhoImagem());
        if (esferas.name().equals("EXECUTIVO")) {
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER EXECUTIVO");
            parameters.put("NOMERELATORIO", "DEMONSTRATIVO DA DISPONIBILIDADE DE CAIXA E DOS RESTOS A PAGAR");
            listaParametros.add(new ParametrosRelatorios("vw.esferadopoder", ":esfera", null, OperacaoRelatorio.LIKE, esferas.name(), null, 2, false));
        } else if (esferas.name().equals("LEGISLATIVO")) {
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER LEGISLATIVO");
            parameters.put("NOMERELATORIO", "DEMONSTRATIVO DA DISPONIBILIDADE DE CAIXA E DOS RESTOS A PAGAR");
            listaParametros.add(new ParametrosRelatorios("vw.esferadopoder", ":esfera", null, OperacaoRelatorio.LIKE, esferas.name(), null, 2, false));
        } else {
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            parameters.put("NOMERELATORIO", "DEMONSTRATIVO CONSOLIDADO DA DISPONIBILIDADE DE CAIXA E DOS RESTOS A PAGAR");
        }
        parameters.put("GRUPO1", prepararGrupo1(listaParametros));
        parameters.put("GRUPO2", preparaDadosGrupo2(listaParametros));
        return parameters;
    }


    public List<RGFAnexo05Item> prepararDadosAnexo5() {
        relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 5", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativo> itensDemonstrativos = itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaControlador.getExercicioCorrente(), "", "Anexo 5", TipoRelatorioItemDemonstrativo.RGF);
        itens = Lists.newArrayList();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            ItemDemonstrativoComponente itemDemonstrativoComponente = new ItemDemonstrativoComponente();
            itemDemonstrativoComponente.setNome(itemDemonstrativo.getNome());
            itemDemonstrativoComponente.setDescricao(itemDemonstrativo.getDescricao());
            itemDemonstrativoComponente.setOrdem(itemDemonstrativo.getOrdem());
            itemDemonstrativoComponente.setColuna(itemDemonstrativo.getColuna());
            itemDemonstrativoComponente.setGrupo(itemDemonstrativo.getGrupo());
            itemDemonstrativoComponente.setEspaco(itemDemonstrativo.getEspaco());
            itemDemonstrativoComponente.setItemDemonstrativo(itemDemonstrativo);
            itens.add(itemDemonstrativoComponente);
        }
        return prepararGrupo1(montarParametrosRelatorio());
    }


    private List<RGFAnexo05Item> preparaDados() {
        List<RGFAnexo05Item> toReturn = new ArrayList<>();
        RGFAnexo05Item item = new RGFAnexo05Item();
        toReturn.add(item);
        return toReturn;
    }

    private List<RGFAnexo05Item> prepararGrupo1(List<ParametrosRelatorios> listaParametros) {
        List<RGFAnexo05Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente it : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(it.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo05Item item = new RGFAnexo05Item();
                String descricao = StringUtil.preencheString("", it.getEspaco(), ' ') + it.getNome();
                item.setDescricao(descricao);
                item.setValorColuna1(relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DISPONIBILIDADE_CAIXA_BRUTA));
                item.setValorColuna2(relatorioRGFAnexo05Calculator2017.calcularRestoLiquidadosNaoPagosProcessadosExerciciosAnteriores(itemDemonstrativo, relatoriosItemDemonst, listaParametros));
                item.setValorColuna3(relatorioRGFAnexo05Calculator2017.calcularRestoLiquidadosNaoPagosProcessados(itemDemonstrativo, relatoriosItemDemonst, listaParametros));
                item.setValorColuna4(relatorioRGFAnexo05Calculator2017.calcularRestoEmpenhoNaoProcessadosExerciciosAnteriores(itemDemonstrativo, relatoriosItemDemonst, listaParametros));
                BigDecimal valorColuna5 = relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DISPONIBILIDADE_COMPROMETIDA_LIQUIDACAO);
                valorColuna5 = valorColuna5.add(relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DISPONIBILIDADE_COMPROMETIDA_CONSIGNACAO));
                valorColuna5 = valorColuna5.add(relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DISPONIBILIDADE_COMPROMETIDA_COMPENSATORIAS));
                valorColuna5 = valorColuna5.subtract(item.getValorColuna2()).subtract(item.getValorColuna3());
                item.setValorColuna5(valorColuna5);
                item.setValorColuna6(item.getValorColuna1().subtract(item.getValorColuna2().add(item.getValorColuna3()).add(item.getValorColuna4()).add(item.getValorColuna5())));
                item.setValorColuna7(relatorioRGFAnexo05Calculator2017.calcularRestoEmpenhoNaoProcessados(itemDemonstrativo, relatoriosItemDemonst, listaParametros));
                item.setValorColuna8(BigDecimal.ZERO);
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    private List<RGFAnexo05Item> preparaDadosGrupo2(List<ParametrosRelatorios> listaParametros) {
        List<RGFAnexo05Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente it : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(it.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo05Item item = new RGFAnexo05Item();
                String descricao = StringUtil.preencheString("", it.getEspaco(), ' ') + it.getNome();
                item.setDescricao(descricao);
                item.setValorColuna1(relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DISPONIBILIDADE_CAIXA_BRUTA));
                item.setValorColuna2(relatorioRGFAnexo05Calculator2017.calcularRestoLiquidadosNaoPagosProcessadosExerciciosAnteriores(itemDemonstrativo, relatoriosItemDemonst, listaParametros));
                item.setValorColuna3(relatorioRGFAnexo05Calculator2017.calcularRestoLiquidadosNaoPagosProcessados(itemDemonstrativo, relatoriosItemDemonst, listaParametros));
                item.setValorColuna4(relatorioRGFAnexo05Calculator2017.calcularRestoEmpenhoNaoProcessadosExerciciosAnteriores(itemDemonstrativo, relatoriosItemDemonst, listaParametros));
                BigDecimal valorColuna5 = relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DISPONIBILIDADE_COMPROMETIDA_LIQUIDACAO);
                valorColuna5 = valorColuna5.add(relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DISPONIBILIDADE_COMPROMETIDA_CONSIGNACAO));
                valorColuna5 = valorColuna5.add(relatorioFacade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DISPONIBILIDADE_COMPROMETIDA_COMPENSATORIAS));
                valorColuna5 = valorColuna5.subtract(item.getValorColuna2()).subtract(item.getValorColuna3());
                item.setValorColuna5(valorColuna5);
                item.setValorColuna6(item.getValorColuna1().subtract(item.getValorColuna2().add(item.getValorColuna3()).add(item.getValorColuna4()).add(item.getValorColuna5())));
                item.setValorColuna7(relatorioRGFAnexo05Calculator2017.calcularRestoEmpenhoNaoProcessados(itemDemonstrativo, relatoriosItemDemonst, listaParametros));
                item.setValorColuna8(BigDecimal.ZERO);
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

    public ItemDemonstrativoFacade getItemDemonstrativoFacade() {
        return itemDemonstrativoFacade;
    }

    public void setItemDemonstrativoFacade(ItemDemonstrativoFacade itemDemonstrativoFacade) {
        this.itemDemonstrativoFacade = itemDemonstrativoFacade;
    }

    public RelatorioRGFAnexo05Calculator2017 getRelatorioRGFAnexo05Calculator2017() {
        return relatorioRGFAnexo05Calculator2017;
    }

    public void setRelatorioRGFAnexo05Calculator2017(RelatorioRGFAnexo05Calculator2017 relatorioRGFAnexo05Calculator2017) {
        this.relatorioRGFAnexo05Calculator2017 = relatorioRGFAnexo05Calculator2017;
    }

    public RelatorioDemonstrativoDisponibilidadeRecursoFacade getRelatorioFacade() {
        return relatorioFacade;
    }

    public void setRelatorioFacade(RelatorioDemonstrativoDisponibilidadeRecursoFacade relatorioFacade) {
        this.relatorioFacade = relatorioFacade;
    }
}
