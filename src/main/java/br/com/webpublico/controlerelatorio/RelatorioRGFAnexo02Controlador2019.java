package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.NotaExplicativaRGF;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.RGFAnexo02Item;
import br.com.webpublico.enums.AnexoRGF;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.NotaExplicativaRGFFacade;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo02Facade;
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
 * Created by Mateus on 28/08/2014.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo2-2019", pattern = "/relatorio/rgf/anexo2/2019/", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo02-2019.xhtml")
})
public class RelatorioRGFAnexo02Controlador2019 extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private RelatorioRGFAnexo02Facade relatorioRGFAnexo02Facade;
    @EJB
    private NotaExplicativaRGFFacade notaExplicativaRGFFacade;
    private Mes mesFinal;
    private List<ItemDemonstrativoComponente> itens;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltros;
    private String notaExplicativa;

    public RelatorioRGFAnexo02Controlador2019() {
        itens = Lists.newArrayList();
    }

    @URLAction(mappingId = "relatorio-rgf-anexo2-2019", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mesFinal = Mes.ABRIL;
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        buscarNotaPorMes();
    }

    public void buscarNotaPorMes() {
        notaExplicativa = notaExplicativaRGFFacade.buscarNotaPorMes(mesFinal, sistemaControlador.getExercicioCorrente(), AnexoRGF.ANEXO_2).getNotaExplicativa();
    }

    private List<RGFAnexo02Item> criarListaComUmItem() {
        List<RGFAnexo02Item> itens = new ArrayList<>();
        itens.add(new RGFAnexo02Item(""));
        return itens;
    }

    private List<RGFAnexo02Item> buscarDadosGrupo1() {
        BigDecimal rclAnterior = BigDecimal.ZERO;
        BigDecimal rclPrimeiroQuadrimestre = BigDecimal.ZERO;
        BigDecimal rclSegundoQuadrimestre = BigDecimal.ZERO;
        BigDecimal rclTerceiroQuadrimestre = BigDecimal.ZERO;
        List<RGFAnexo02Item> retorno = new ArrayList<>();
        for (ItemDemonstrativoComponente itemComponente : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo02Facade.getItemDemonstrativoFacade().recuperaPorGrupo(itemComponente.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo02Item rgfItem = new RGFAnexo02Item();
                rgfItem.setDescricao(itemComponente.getDescricaoComEspacos());
                if (itemComponente.getOrdem() == 26) {
                    rclAnterior = calcularRclAjustaExercicioAnterior();
                    rclPrimeiroQuadrimestre = calcularRcl(Mes.ABRIL);
                    rgfItem.setSaldoAnterior(rclAnterior);
                    rgfItem.setPrimeiroQuadrimestre(rclPrimeiroQuadrimestre);
                    if (Mes.AGOSTO.equals(mesFinal) || Mes.DEZEMBRO.equals(mesFinal)) {
                        rclSegundoQuadrimestre = calcularRcl(Mes.AGOSTO);
                        rgfItem.setSegundoQuadrimestre(rclSegundoQuadrimestre);
                    }
                    if (Mes.DEZEMBRO.equals(mesFinal)) {
                        rclTerceiroQuadrimestre = calcularRcl(Mes.DEZEMBRO);
                        rgfItem.setTerceiroQuadrimestre(rclTerceiroQuadrimestre);
                    }
                } else if (itemComponente.getOrdem() == 27 || itemComponente.getOrdem() == 28) {
                    atualizarDatas(Mes.JANEIRO, Mes.ABRIL);
                    rgfItem.setSaldoAnterior(buscarValorContabilTransporte(itemDemonstrativo).divide(rclAnterior, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                    rgfItem.setPrimeiroQuadrimestre(buscarValorContabil(itemDemonstrativo).divide(rclPrimeiroQuadrimestre, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                    if (Mes.AGOSTO.equals(mesFinal) || Mes.DEZEMBRO.equals(mesFinal)) {
                        atualizarDatas(Mes.MAIO, Mes.AGOSTO);
                        rgfItem.setSegundoQuadrimestre(buscarValorContabil(itemDemonstrativo).divide(rclSegundoQuadrimestre, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                    }
                    if (Mes.DEZEMBRO.equals(mesFinal)) {
                        atualizarDatas(Mes.SETEMBRO, Mes.DEZEMBRO);
                        rgfItem.setTerceiroQuadrimestre(buscarValorContabil(itemDemonstrativo).divide(rclTerceiroQuadrimestre, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                    }
                } else if (itemComponente.getOrdem() == 29) {
                    rgfItem.setSaldoAnterior(rclAnterior.add(rclAnterior.multiply(BigDecimal.valueOf(0.20))));
                    rgfItem.setPrimeiroQuadrimestre(rclPrimeiroQuadrimestre.add(rclPrimeiroQuadrimestre.multiply(BigDecimal.valueOf(0.20))));
                    if (Mes.AGOSTO.equals(mesFinal) || Mes.DEZEMBRO.equals(mesFinal)) {
                        rgfItem.setSegundoQuadrimestre(rclSegundoQuadrimestre.add(rclSegundoQuadrimestre.multiply(BigDecimal.valueOf(0.20))));
                    }
                    if (Mes.DEZEMBRO.equals(mesFinal)) {
                        rgfItem.setTerceiroQuadrimestre(rclTerceiroQuadrimestre.add(rclTerceiroQuadrimestre.multiply(BigDecimal.valueOf(0.20))));
                    }
                } else if (itemComponente.getOrdem() == 30) {
                    rgfItem.setSaldoAnterior(rclAnterior.add(rclAnterior.multiply(BigDecimal.valueOf(0.08))));
                    rgfItem.setPrimeiroQuadrimestre(rclPrimeiroQuadrimestre.add(rclPrimeiroQuadrimestre.multiply(BigDecimal.valueOf(0.08))));
                    if (Mes.AGOSTO.equals(mesFinal) || Mes.DEZEMBRO.equals(mesFinal)) {
                        rgfItem.setSegundoQuadrimestre(rclSegundoQuadrimestre.add(rclSegundoQuadrimestre.multiply(BigDecimal.valueOf(0.08))));
                    }
                    if (Mes.DEZEMBRO.equals(mesFinal)) {
                        rgfItem.setTerceiroQuadrimestre(rclTerceiroQuadrimestre.add(rclTerceiroQuadrimestre.multiply(BigDecimal.valueOf(0.08))));
                    }
                } else {
                    atualizarDatas(Mes.JANEIRO, Mes.ABRIL);
                    rgfItem.setSaldoAnterior(buscarValorContabilTransporte(itemDemonstrativo));
                    rgfItem.setPrimeiroQuadrimestre(buscarValorContabil(itemDemonstrativo));
                    if (Mes.AGOSTO.equals(mesFinal) || Mes.DEZEMBRO.equals(mesFinal)) {
                        atualizarDatas(Mes.MAIO, Mes.AGOSTO);
                        rgfItem.setSegundoQuadrimestre(buscarValorContabil(itemDemonstrativo));
                    }
                    if (Mes.DEZEMBRO.equals(mesFinal)) {
                        atualizarDatas(Mes.SETEMBRO, Mes.DEZEMBRO);
                        rgfItem.setTerceiroQuadrimestre(buscarValorContabil(itemDemonstrativo));
                    }
                }
                retorno.add(rgfItem);
            }
        }
        return retorno;
    }

    public List<RGFAnexo02Item> buscarDadosGrupo1PopulandoItens() {
        relatoriosItemDemonst = relatorioRGFAnexo02Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 2", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativo> itensDemonstrativos = relatorioRGFAnexo02Facade.getItemDemonstrativoFacade().buscarItensPorExercicioAndRelatorio(sistemaControlador.getExercicioCorrente(), "", "Anexo 2", TipoRelatorioItemDemonstrativo.RGF);
        itens = Lists.newArrayList();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
        instanciarItemDemonstrativoFiltros();
        return buscarDadosGrupo1();
    }

    private List<RGFAnexo02Item> buscarDadosGrupo2() {
        List<RGFAnexo02Item> retorno = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo02Facade.getItemDemonstrativoFacade().recuperaPorGrupo(item.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo02Item it = new RGFAnexo02Item();
                it.setDescricao(item.getDescricaoComEspacos());
                atualizarDatas(Mes.JANEIRO, Mes.ABRIL);
                it.setSaldoAnterior(buscarValorContabilTransporte(itemDemonstrativo));
                it.setPrimeiroQuadrimestre(buscarValorContabil(itemDemonstrativo));
                if (Mes.AGOSTO.equals(mesFinal) || Mes.DEZEMBRO.equals(mesFinal)) {
                    atualizarDatas(Mes.MAIO, Mes.AGOSTO);
                    it.setSegundoQuadrimestre(buscarValorContabil(itemDemonstrativo));
                }
                if (Mes.DEZEMBRO.equals(mesFinal)) {
                    atualizarDatas(Mes.SETEMBRO, Mes.DEZEMBRO);
                    it.setTerceiroQuadrimestre(buscarValorContabil(itemDemonstrativo));
                }
                retorno.add(it);
            }
        }
        return retorno;
    }

    public void gerarRelatorio() {
        try {
            relatoriosItemDemonst = relatorioRGFAnexo02Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 2", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(criarListaComUmItem());
            HashMap parameters = montarParametros();
            gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeArquivo(), getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeArquivo() {
        return "LRF-RGF-ANEXO-2-" + getSistemaControlador().getExercicioCorrente().getAno() + "-" + mesFinal.getNumeroMesString();
    }

    public void salvarRelatorio() {
        try {
            relatoriosItemDemonst = relatorioRGFAnexo02Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 2", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(criarListaComUmItem());
            HashMap parameters = montarParametros();
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(sistemaControlador.getExercicioCorrente());
            anexo.setNome("Anexo 2 - Demonstrativo da Dívida Consolidada Líquida");
            anexo.setMes(mesFinal);
            anexo.setTipo(PortalTransparenciaTipo.RGF);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltros.setExercicio(sistemaControlador.getExercicioCorrente());
    }

    private void atualizarDatas(Mes mesInicial, Mes mesFinal) {
        itemDemonstrativoFiltros.setDataInicial("01/" + mesInicial.getNumeroMesString() + "/" + sistemaControlador.getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(mesFinal.getNumeroMes(), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal.getNumeroMesString() + "/" + sistemaControlador.getExercicioCorrente().getAno());
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(Mes.ABRIL, "Jan - Abr"));
        retorno.add(new SelectItem(Mes.AGOSTO, "Mai - Ago"));
        retorno.add(new SelectItem(Mes.DEZEMBRO, "Set - Dez"));
        return retorno;
    }

    private String getNomeRelatorio() {
        return "RelatorioRGFAnexo02-2019.jasper";
    }

    private HashMap montarParametros() {
        instanciarItemDemonstrativoFiltros();
        relatorioRGFAnexo02Facade.limparHash();
        relatorioRGFAnexo02Facade.limparHashItensRecuperados();
        HashMap parameters = new HashMap();
        parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno());
        parameters.put("GRUPO1", buscarDadosGrupo1());
        parameters.put("GRUPO2", buscarDadosGrupo2());
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("USER", sistemaControlador.getUsuarioCorrente().getNome());
        parameters.put("DATAINICIAL", getMesInicial().getDescricao().toUpperCase());
        parameters.put("DATAFINAL", mesFinal.getDescricao().toUpperCase() + " DE " + sistemaControlador.getExercicioCorrente().getAno());
        parameters.put("NOTAEXPLICATIVA", "NOTA: " + notaExplicativa.trim());
        NotaExplicativaRGF notaExplicativaRGF = notaExplicativaRGFFacade.buscarNotaPorMes(mesFinal, sistemaControlador.getExercicioCorrente(), AnexoRGF.ANEXO_2);
        notaExplicativaRGF.setNotaExplicativa(notaExplicativa);
        notaExplicativaRGF.setAnexoRGF(AnexoRGF.ANEXO_2);
        notaExplicativaRGF.setExercicio(sistemaControlador.getExercicioCorrente());
        notaExplicativaRGFFacade.salvar(notaExplicativaRGF);
        return parameters;
    }

    private BigDecimal calcularRcl(Mes mes) {
        return relatorioRGFAnexo02Facade.getRelatorioRREOAnexo03Calculator().calcularRcl(mes, sistemaControlador.getExercicioCorrente());
    }

    private BigDecimal calcularRclAjustaExercicioAnterior() {
        return relatorioRGFAnexo02Facade.getRelatorioRREOAnexo03Calculator().calcularRclExercicioAnterior(Mes.DEZEMBRO);
    }

    private BigDecimal buscarValorContabil(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRGFAnexo02Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.SALDO_CONTABIL_ATUAL);
    }

    private BigDecimal buscarValorContabilTransporte(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRGFAnexo02Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.SALDO_CONTABIL_TRANSPORTE);
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public List<ItemDemonstrativoComponente> getItens() {
        return itens;
    }

    public void setItens(List<ItemDemonstrativoComponente> itens) {
        this.itens = itens;
    }

    public RelatoriosItemDemonst getRelatoriosItemDemonst() {
        return relatoriosItemDemonst;
    }

    public void setRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst) {
        this.relatoriosItemDemonst = relatoriosItemDemonst;
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
