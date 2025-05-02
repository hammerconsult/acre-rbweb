package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.RGFAnexo03Item;
import br.com.webpublico.enums.AnexoRGF;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.NotaExplicativaRGFFacade;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo03Facade;
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mateus on 25/04/18.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo3-2019", pattern = "/relatorio/rgf/anexo3/2019/", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo03-2019.xhtml")
})
public class RelatorioRGFAnexo03Controlador2019 extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private RelatorioRGFAnexo03Facade relatorioRGFAnexo03Facade;
    @EJB
    private NotaExplicativaRGFFacade notaExplicativaRGFFacade;
    private Mes mesFinal;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private List<ItemDemonstrativoComponente> itens;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltros;
    private String notaExplicativa;

    public RelatorioRGFAnexo03Controlador2019() {
        itens = Lists.newArrayList();
    }

    @URLAction(mappingId = "relatorio-rgf-anexo3-2019", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mesFinal = Mes.ABRIL;
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        buscarNotaPorMes();
    }

    public void buscarNotaPorMes() {
        notaExplicativa = notaExplicativaRGFFacade.buscarNotaPorMes(mesFinal, sistemaControlador.getExercicioCorrente(), AnexoRGF.ANEXO_3).getNotaExplicativa();
    }

    private List<RGFAnexo03Item> criarListaComUmItem() {
        List<RGFAnexo03Item> toReturn = Lists.newArrayList();
        toReturn.add(new RGFAnexo03Item(""));
        return toReturn;
    }

    private List<RGFAnexo03Item> buscarGarantias() {
        List<RGFAnexo03Item> toReturn = new ArrayList<>();
        BigDecimal valorExercicioAnterior = BigDecimal.ZERO;
        BigDecimal valorPrimeiroQuadrimestre = BigDecimal.ZERO;
        BigDecimal valorSegundoQuadrimestre = BigDecimal.ZERO;
        BigDecimal valorTerceiroQuadrimestre = BigDecimal.ZERO;

        BigDecimal percentualExercicioAnterior = BigDecimal.ZERO;
        BigDecimal percentualPrimeiroQuadrimestre = BigDecimal.ZERO;
        BigDecimal percentualSegundoQuadrimestre = BigDecimal.ZERO;
        BigDecimal percentualTerceiroQuadrimestre = BigDecimal.ZERO;

        BigDecimal valorExercicioAnteriorRcl = BigDecimal.ZERO;
        BigDecimal valorPrimeiroQuadrimestreRcl = BigDecimal.ZERO;
        BigDecimal valorSegundoQuadrimestreRcl = BigDecimal.ZERO;
        BigDecimal valorTerceiroQuadrimestreRcl = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente it : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo03Facade.getItemDemonstrativoFacade().recuperaPorGrupo(it.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo03Item item = new RGFAnexo03Item();
                item.setDescricao(it.getDescricaoComEspacos());
                if (it.getOrdem() == 12) {
                    item.setExercicioAnterior(calcularRclExercicioAnterior());
                    item.setPrimeiroQuadrimestre(calcularRcl(Mes.ABRIL));
                    if (Mes.AGOSTO.equals(mesFinal) || Mes.DEZEMBRO.equals(mesFinal)) {
                        item.setSegundoQuadrimestre(calcularRcl(Mes.AGOSTO));
                    }
                    if (Mes.DEZEMBRO.equals(mesFinal)) {
                        item.setTerceiroQuadrimestre(calcularRcl(Mes.DEZEMBRO));
                    }
                    if (item.getExercicioAnterior().compareTo(BigDecimal.ZERO) != 0 && valorExercicioAnterior.compareTo(BigDecimal.ZERO) != 0) {
                        percentualExercicioAnterior = valorExercicioAnterior.divide(item.getExercicioAnterior(), 2);
                    }
                    if (item.getPrimeiroQuadrimestre().compareTo(BigDecimal.ZERO) != 0 && valorPrimeiroQuadrimestre.compareTo(BigDecimal.ZERO) != 0) {
                        percentualPrimeiroQuadrimestre = valorPrimeiroQuadrimestre.divide(item.getPrimeiroQuadrimestre(), 2);
                    }
                    if (item.getSegundoQuadrimestre().compareTo(BigDecimal.ZERO) != 0 && valorSegundoQuadrimestre.compareTo(BigDecimal.ZERO) != 0) {
                        percentualSegundoQuadrimestre = valorSegundoQuadrimestre.divide(item.getSegundoQuadrimestre(), 2);
                    }
                    if (item.getTerceiroQuadrimestre().compareTo(BigDecimal.ZERO) != 0 && valorTerceiroQuadrimestre.compareTo(BigDecimal.ZERO) != 0) {
                        percentualTerceiroQuadrimestre = valorTerceiroQuadrimestre.divide(item.getTerceiroQuadrimestre(), 2);
                    }
                    valorExercicioAnteriorRcl = item.getExercicioAnterior();
                    valorPrimeiroQuadrimestreRcl = item.getPrimeiroQuadrimestre();
                    valorSegundoQuadrimestreRcl = item.getSegundoQuadrimestre();
                    valorTerceiroQuadrimestreRcl = item.getTerceiroQuadrimestre();
                } else if (it.getOrdem() == 14) {

                    if (valorExercicioAnteriorRcl.compareTo(BigDecimal.ZERO) > 0) {
                        item.setExercicioAnterior(valorExercicioAnteriorRcl.multiply(BigDecimal.valueOf(22).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)).setScale(2, RoundingMode.DOWN));
                    }
                    if (valorPrimeiroQuadrimestreRcl.compareTo(BigDecimal.ZERO) > 0) {
                        item.setPrimeiroQuadrimestre(valorPrimeiroQuadrimestreRcl.multiply(BigDecimal.valueOf(22).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)).setScale(2, RoundingMode.DOWN));
                    }
                    if (valorSegundoQuadrimestreRcl.compareTo(BigDecimal.ZERO) > 0) {
                        item.setSegundoQuadrimestre(valorSegundoQuadrimestreRcl.multiply(BigDecimal.valueOf(22).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)).setScale(2, RoundingMode.DOWN));
                    }
                    if (valorTerceiroQuadrimestreRcl.compareTo(BigDecimal.ZERO) > 0) {
                        item.setTerceiroQuadrimestre(valorTerceiroQuadrimestreRcl.multiply(BigDecimal.valueOf(22).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)).setScale(2, RoundingMode.DOWN));
                    }
                } else if (it.getOrdem() == 13) {
                    item.setExercicioAnterior(percentualExercicioAnterior);
                    item.setPrimeiroQuadrimestre(percentualPrimeiroQuadrimestre);
                    if (Mes.AGOSTO.equals(mesFinal) || Mes.DEZEMBRO.equals(mesFinal)) {
                        item.setSegundoQuadrimestre(percentualSegundoQuadrimestre);
                    }
                    if (Mes.DEZEMBRO.equals(mesFinal)) {
                        item.setTerceiroQuadrimestre(percentualTerceiroQuadrimestre);
                    }
                } else if (it.getOrdem() == 15) {
                    if (valorExercicioAnteriorRcl.compareTo(BigDecimal.ZERO) > 0) {
                        item.setExercicioAnterior(valorExercicioAnteriorRcl.multiply(BigDecimal.valueOf(19.8).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                    }
                    if (valorPrimeiroQuadrimestreRcl.compareTo(BigDecimal.ZERO) > 0) {
                        item.setPrimeiroQuadrimestre(valorPrimeiroQuadrimestreRcl.multiply(BigDecimal.valueOf(19.8).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                    }
                    if (valorSegundoQuadrimestreRcl.compareTo(BigDecimal.ZERO) > 0) {
                        item.setSegundoQuadrimestre(valorSegundoQuadrimestreRcl.multiply(BigDecimal.valueOf(19.8).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                    }
                    if (valorTerceiroQuadrimestreRcl.compareTo(BigDecimal.ZERO) > 0) {
                        item.setTerceiroQuadrimestre(valorTerceiroQuadrimestreRcl.multiply(BigDecimal.valueOf(19.8).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                    }
                } else {
                    atualizarDatas(Mes.JANEIRO, Mes.DEZEMBRO, getExercicioAnterior());
                    item.setExercicioAnterior(buscarValorReceitaRealizada(itemDemonstrativo));
                    atualizarDatas(Mes.JANEIRO, Mes.ABRIL, getExercicioCorrente());
                    item.setPrimeiroQuadrimestre(buscarValorReceitaRealizada(itemDemonstrativo));
                    if (Mes.AGOSTO.equals(mesFinal) || Mes.DEZEMBRO.equals(mesFinal)) {
                        atualizarDatas(Mes.MAIO, Mes.AGOSTO, getExercicioCorrente());
                        item.setSegundoQuadrimestre(buscarValorReceitaRealizada(itemDemonstrativo));
                    }
                    if (Mes.DEZEMBRO.equals(mesFinal)) {
                        atualizarDatas(Mes.SETEMBRO, Mes.DEZEMBRO, getExercicioCorrente());
                        item.setTerceiroQuadrimestre(buscarValorReceitaRealizada(itemDemonstrativo));
                    }
                    if (it.getOrdem() == 11) {
                        valorExercicioAnterior = item.getExercicioAnterior();
                        valorPrimeiroQuadrimestre = item.getPrimeiroQuadrimestre();
                        valorSegundoQuadrimestre = item.getSegundoQuadrimestre();
                        valorTerceiroQuadrimestre = item.getTerceiroQuadrimestre();
                    }
                }
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(Mes.ABRIL, "Jan - Abr"));
        retorno.add(new SelectItem(Mes.AGOSTO, "Mai - Ago"));
        retorno.add(new SelectItem(Mes.DEZEMBRO, "Set - Dez"));
        return retorno;
    }

    public List<RGFAnexo03Item> buscarDadosPopulandoItens() {
        relatoriosItemDemonst = relatorioRGFAnexo03Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativo> itensDemonstrativos = relatorioRGFAnexo03Facade.getItemDemonstrativoFacade().buscarItensPorExercicioAndRelatorio(sistemaControlador.getExercicioCorrente(), "", "Anexo 3", TipoRelatorioItemDemonstrativo.RGF);
        itens = Lists.newArrayList();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
        instanciarItemDemonstrativoFiltros();
        return buscarGarantias();
    }

    private BigDecimal calcularRcl(Mes mes) {
        return relatorioRGFAnexo03Facade.getRelatorioRREOAnexo03Calculator().calcularRcl(mes, sistemaControlador.getExercicioCorrente());
    }

    private BigDecimal calcularRclExercicioAnterior() {
        return relatorioRGFAnexo03Facade.getRelatorioRREOAnexo03Calculator().calcularRclExercicioAnterior(Mes.DEZEMBRO);
    }

    private List<RGFAnexo03Item> buscarContragarantia() {
        List<RGFAnexo03Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo03Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo03Item it = new RGFAnexo03Item();
                it.setDescricao(item.getDescricaoComEspacos());
                atualizarDatas(Mes.JANEIRO, Mes.DEZEMBRO, getExercicioAnterior());
                it.setExercicioAnterior(buscarValorReceitaRealizada(itemDemonstrativo));
                atualizarDatas(Mes.JANEIRO, Mes.ABRIL, getExercicioCorrente());
                it.setPrimeiroQuadrimestre(buscarValorReceitaRealizada(itemDemonstrativo));
                if (Mes.AGOSTO.equals(mesFinal) || Mes.DEZEMBRO.equals(mesFinal)) {
                    atualizarDatas(Mes.MAIO, Mes.AGOSTO, getExercicioCorrente());
                    it.setSegundoQuadrimestre(buscarValorReceitaRealizada(itemDemonstrativo));
                }
                if (Mes.DEZEMBRO.equals(mesFinal)) {
                    atualizarDatas(Mes.SETEMBRO, Mes.DEZEMBRO, getExercicioCorrente());
                    it.setTerceiroQuadrimestre(buscarValorReceitaRealizada(itemDemonstrativo));
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public void gerarRelatorio() {
        try {
            relatoriosItemDemonst = relatorioRGFAnexo03Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
            HashMap parameters = recuperarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(criarListaComUmItem());
            gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeArquivo(), getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeArquivo() {
        return "LRF-RGF-ANEXO-3-" + getSistemaControlador().getExercicioCorrente().getAno() + "-" + mesFinal.getNumeroMesString();
    }

    public void salvarRelatorio() {
        try {
            relatoriosItemDemonst = relatorioRGFAnexo03Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
            HashMap parameters = recuperarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(criarListaComUmItem());
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(sistemaControlador.getExercicioCorrente());
            anexo.setNome("Anexo 3 - Demonstrativo das Garantias e Contragarantias de Valores");
            anexo.setMes(mesFinal);
            anexo.setTipo(PortalTransparenciaTipo.RGF);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "RelatorioAnexo03RGFGarantias-2019.jasper";
    }

    private HashMap recuperarParametros() {
        HashMap parameters = new HashMap();
        instanciarItemDemonstrativoFiltros();
        parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("USER", sistemaControlador.getUsuarioCorrente().getNome());
        parameters.put("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno());
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("GARANTIAS", buscarGarantias());
        parameters.put("CONTRAGARANTIAS", buscarContragarantia());
        parameters.put("MEDIDACORRETIVA", "");
        parameters.put("DATAINICIAL", getMesInicial().getDescricao().toUpperCase());
        parameters.put("DATAFINAL", " " + mesFinal.getDescricao().toUpperCase() + " DE " + sistemaControlador.getExercicioCorrente().getAno());
        parameters.put("NOTAEXPLICATIVA", "NOTA: " + notaExplicativa.trim());
        NotaExplicativaRGF notaExplicativaRGF = notaExplicativaRGFFacade.buscarNotaPorMes(mesFinal, sistemaControlador.getExercicioCorrente(), AnexoRGF.ANEXO_3);
        notaExplicativaRGF.setNotaExplicativa(notaExplicativa);
        notaExplicativaRGF.setAnexoRGF(AnexoRGF.ANEXO_3);
        notaExplicativaRGF.setExercicio(sistemaControlador.getExercicioCorrente());
        notaExplicativaRGFFacade.salvar(notaExplicativaRGF);
        return parameters;
    }

    private void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
    }

    private void atualizarDatas(Mes mesInicial, Mes mesFinal, Exercicio exercicio) {
        itemDemonstrativoFiltros.setDataInicial("01/" + mesInicial.getNumeroMesString() + "/" + sistemaControlador.getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(mesFinal.getNumeroMes(), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal.getNumeroMesString() + "/" + sistemaControlador.getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setExercicio(exercicio);
    }

    private Exercicio getExercicioAnterior() {
        return getExercicioFacade().getExercicioPorAno(getSistemaFacade().getExercicioCorrente().getAno() - 1);
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    private BigDecimal buscarValorReceitaRealizada(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRGFAnexo03Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RECEITA_REALIZADA_ATE_BIMESTRE);
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

    public ItemDemonstrativoFiltros getItemDemonstrativoFiltros() {
        return itemDemonstrativoFiltros;
    }

    public void setItemDemonstrativoFiltros(ItemDemonstrativoFiltros itemDemonstrativoFiltros) {
        this.itemDemonstrativoFiltros = itemDemonstrativoFiltros;
    }

    public String getNotaExplicativa() {
        return notaExplicativa;
    }

    public void setNotaExplicativa(String notaExplicativa) {
        this.notaExplicativa = notaExplicativa;
    }
}

