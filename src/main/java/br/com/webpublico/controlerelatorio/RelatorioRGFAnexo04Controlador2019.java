package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.NotaExplicativaRGF;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.RGFAnexo04ItemApuracao;
import br.com.webpublico.entidadesauxiliares.RGFAnexo04item;
import br.com.webpublico.enums.AnexoRGF;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.NotaExplicativaRGFFacade;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo04Facade;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mateus on 26/04/18.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo4-2019", pattern = "/relatorio/rgf/anexo4/2019/", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo04-2019.xhtml")
})
public class RelatorioRGFAnexo04Controlador2019 extends AbstractReport implements Serializable {
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private RelatorioRGFAnexo04Facade relatorioRGFAnexo04Facade;
    @EJB
    private NotaExplicativaRGFFacade notaExplicativaRGFFacade;
    private Mes mesFinal;
    private BigDecimal totalInternaAteQuadrimestre;
    private BigDecimal totalExternaAteQuadrimestre;
    private BigDecimal totalGeralAteQuadrimestre;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private List<ItemDemonstrativoComponente> itens;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltros;
    private String notaExplicativa;

    public RelatorioRGFAnexo04Controlador2019() {
        itens = Lists.newArrayList();
    }

    private List<RGFAnexo04item> buscarDadosGrupoUm() {
        List<RGFAnexo04item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente it : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo04Facade.getItemDemonstrativoFacade().recuperaPorGrupo(it.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo04item item = new RGFAnexo04item();
                item.setDescricao(it.getDescricaoComEspacos());
                atualizarDatas(getMesInicial(), mesFinal);
                if (Mes.ABRIL.equals(mesFinal)) {
                    item.setValorNoQuadrimestre(calcularValorAteQuadrimestre(itemDemonstrativo));
                } else {
                    item.setValorNoQuadrimestre(calcularValorNoQuadrimestre(itemDemonstrativo));
                }
                atualizarDatas(Mes.JANEIRO, mesFinal);
                item.setValorAteQuadrimestre(calcularValorAteQuadrimestre(itemDemonstrativo));
                if (it.getOrdem() == 10) {
                    totalInternaAteQuadrimestre = item.getValorAteQuadrimestre();
                } else if (it.getOrdem() == 16) {
                    totalExternaAteQuadrimestre = item.getValorAteQuadrimestre();
                } else if (it.getOrdem() == 17) {
                    totalGeralAteQuadrimestre = item.getValorAteQuadrimestre();
                }
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    private List<RGFAnexo04ItemApuracao> buscarDadosGrupoDois() {
        List<RGFAnexo04ItemApuracao> toReturn = new ArrayList<>();
        BigDecimal totalCorrenteLiquida = calcularRcl();
        for (ItemDemonstrativoComponente it : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo04Facade.getItemDemonstrativoFacade().recuperaPorGrupo(it.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo04ItemApuracao item = new RGFAnexo04ItemApuracao();
                item.setDescricao(it.getDescricaoComEspacos());
                atualizarDatas(Mes.JANEIRO, mesFinal);
                if (it.getOrdem() == 1) {
                    item.setValor(totalCorrenteLiquida);
                } else if (it.getOrdem() == 3) {
                    item.setValor(totalGeralAteQuadrimestre.subtract(totalInternaAteQuadrimestre).subtract(totalExternaAteQuadrimestre));
                    if (totalCorrenteLiquida.compareTo(BigDecimal.ZERO) != 0) {
                        item.setPercentual((item.getValor().divide(totalCorrenteLiquida, 6, BigDecimal.ROUND_HALF_EVEN)));
                    }
                } else if (it.getOrdem() == 4) {
                    if (totalCorrenteLiquida.compareTo(BigDecimal.ZERO) != 0) {
                        item.setValor(totalCorrenteLiquida.multiply(BigDecimal.valueOf(16).divide(BigDecimal.valueOf(100), 6, BigDecimal.ROUND_HALF_EVEN)));
                    }
                    item.setPercentual(BigDecimal.valueOf(16).divide(BigDecimal.valueOf(100), 6, BigDecimal.ROUND_HALF_EVEN));
                } else if (it.getOrdem() == 5) {
                    if (totalCorrenteLiquida.compareTo(BigDecimal.ZERO) != 0) {
                        item.setValor(totalCorrenteLiquida.multiply(BigDecimal.valueOf(14.4).divide(BigDecimal.valueOf(100), 6, BigDecimal.ROUND_HALF_EVEN)));
                    }
                    item.setPercentual(BigDecimal.valueOf(14.4).divide(BigDecimal.valueOf(100), 6, BigDecimal.ROUND_HALF_EVEN));
                } else if (it.getOrdem() == 7) {
                    if (totalCorrenteLiquida.compareTo(BigDecimal.ZERO) != 0) {
                        item.setValor(totalCorrenteLiquida.multiply(BigDecimal.valueOf(7).divide(BigDecimal.valueOf(100), 6, BigDecimal.ROUND_HALF_EVEN)));
                    }
                    item.setPercentual(BigDecimal.valueOf(7).divide(BigDecimal.valueOf(100), 6, BigDecimal.ROUND_HALF_EVEN));
                }
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    private List<RGFAnexo04item> buscarDadosGrupoTres() {
        List<RGFAnexo04item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente it : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo04Facade.getItemDemonstrativoFacade().recuperaPorGrupo(it.getItemDemonstrativo(), 3);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo04item item = new RGFAnexo04item();
                item.setDescricao(it.getDescricaoComEspacos());
                atualizarDatas(getMesInicial(), mesFinal);
                item.setValorNoQuadrimestre(calcularValorNoQuadrimestre(itemDemonstrativo));
                atualizarDatas(Mes.JANEIRO, mesFinal);
                item.setValorAteQuadrimestre(calcularValorAteQuadrimestre(itemDemonstrativo));
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    public List<RGFAnexo04ItemApuracao> preparaDadosAnexo4() {
        relatoriosItemDemonst = relatorioRGFAnexo04Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativo> itensDemonstrativos = relatorioRGFAnexo04Facade.getItemDemonstrativoFacade().buscarItensPorExercicioAndRelatorio(getExercicioCorrente(), "", "Anexo 4", TipoRelatorioItemDemonstrativo.RGF);
        itens = Lists.newArrayList();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
        totalInternaAteQuadrimestre = BigDecimal.ZERO;
        totalExternaAteQuadrimestre = BigDecimal.ZERO;
        totalGeralAteQuadrimestre = BigDecimal.ZERO;
        buscarDadosGrupoUm();
        return buscarDadosGrupoDois();
    }

    public void gerarRelatorio() {
        try {
            relatorioRGFAnexo04Facade.limparHash();
            relatorioRGFAnexo04Facade.limparHashItensRecuperados();
            relatoriosItemDemonst = relatorioRGFAnexo04Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
            instanciarItemDemonstrativoFiltros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(buscarDadosGrupoUm());
            HashMap parameters = montarParametros();
            gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeArquivo(), getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarRelatorio() {
        try {
            relatorioRGFAnexo04Facade.limparHash();
            relatorioRGFAnexo04Facade.limparHashItensRecuperados();
            relatoriosItemDemonst = relatorioRGFAnexo04Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
            instanciarItemDemonstrativoFiltros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(buscarDadosGrupoUm());
            HashMap parameters = montarParametros();
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(getExercicioCorrente());
            anexo.setNome("Anexo 4 - Demonstrativo das Operações de Crédito");
            anexo.setMes(mesFinal);
            anexo.setTipo(PortalTransparenciaTipo.RGF);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "RelatorioAnexo04RGFOperacoes-2019.jasper";
    }

    private HashMap montarParametros() {
        HashMap parameters = new HashMap();
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("ANO_EXERCICIO", getExercicioCorrente().getAno().toString());
        parameters.put("DATAINICIAL", getMesInicial().getDescricao().toUpperCase());
        parameters.put("DATAFINAL", mesFinal.getDescricao().toUpperCase());
        parameters.put("APURACAO", buscarDadosGrupoDois());
        parameters.put("OUTRASOPERACOES", buscarDadosGrupoTres());
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
        parameters.put("USER", sistemaControlador.getUsuarioCorrente().getNome());
        parameters.put("NOTAEXPLICATIVA", "NOTA: " + notaExplicativa.trim());
        NotaExplicativaRGF notaExplicativaRGF = notaExplicativaRGFFacade.buscarNotaPorMes(mesFinal, sistemaControlador.getExercicioCorrente(), AnexoRGF.ANEXO_4);
        notaExplicativaRGF.setNotaExplicativa(notaExplicativa);
        notaExplicativaRGF.setAnexoRGF(AnexoRGF.ANEXO_4);
        notaExplicativaRGF.setExercicio(sistemaControlador.getExercicioCorrente());
        notaExplicativaRGFFacade.salvar(notaExplicativaRGF);
        return parameters;
    }

    private String getNomeArquivo() {
        return "LRF-RGF-ANEXO-4-" + getSistemaControlador().getExercicioCorrente().getAno() + "-" + mesFinal.getNumeroMesString();
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

    public void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        if (relatoriosItemDemonst == null) {
            relatoriosItemDemonst = relatorioRGFAnexo04Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        }
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltros.setExercicio(getExercicioCorrente());
    }

    private void atualizarDatas(Mes mesInicial, Mes mesFinal) {
        itemDemonstrativoFiltros.setDataInicial("01/" + mesInicial.getNumeroMesString() + "/" + getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(mesFinal.getNumeroMes(), getExercicioCorrente().getAno()) + "/" + mesFinal.getNumeroMesString() + "/" + getExercicioCorrente().getAno());
    }

    @URLAction(mappingId = "relatorio-rgf-anexo4", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        totalInternaAteQuadrimestre = BigDecimal.ZERO;
        totalExternaAteQuadrimestre = BigDecimal.ZERO;
        totalGeralAteQuadrimestre = BigDecimal.ZERO;
        mesFinal = Mes.ABRIL;
        buscarNotaPorMes();
    }

    public void buscarNotaPorMes() {
        notaExplicativa = notaExplicativaRGFFacade.buscarNotaPorMes(mesFinal, sistemaControlador.getExercicioCorrente(), AnexoRGF.ANEXO_4).getNotaExplicativa();
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(Mes.ABRIL, "Jan - Abr"));
        retorno.add(new SelectItem(Mes.AGOSTO, "Mai - Ago"));
        retorno.add(new SelectItem(Mes.DEZEMBRO, "Set - Dez"));
        return retorno;
    }

    private BigDecimal calcularRcl() {
        return relatorioRGFAnexo04Facade.getRelatorioRREOAnexo03Calculator().calcularRcl(mesFinal, getExercicioCorrente());
    }

    private BigDecimal calcularValorNoQuadrimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRGFAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.PREVISAO_ATUALIZADA);
    }

    private BigDecimal calcularValorAteQuadrimestre(ItemDemonstrativo itemDemonstrativo) {
        BigDecimal retorno = relatorioRGFAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.PREVISAO_INICIAL);
        retorno = retorno.add(relatorioRGFAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.PREVISAO_ATUALIZADA));
        return retorno;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public BigDecimal getTotalInternaAteQuadrimestre() {
        return totalInternaAteQuadrimestre;
    }

    public void setTotalInternaAteQuadrimestre(BigDecimal totalInternaAteQuadrimestre) {
        this.totalInternaAteQuadrimestre = totalInternaAteQuadrimestre;
    }

    public BigDecimal getTotalExternaAteQuadrimestre() {
        return totalExternaAteQuadrimestre;
    }

    public void setTotalExternaAteQuadrimestre(BigDecimal totalExternaAteQuadrimestre) {
        this.totalExternaAteQuadrimestre = totalExternaAteQuadrimestre;
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

    public BigDecimal getTotalGeralAteQuadrimestre() {
        return totalGeralAteQuadrimestre;
    }

    public void setTotalGeralAteQuadrimestre(BigDecimal totalGeralAteQuadrimestre) {
        this.totalGeralAteQuadrimestre = totalGeralAteQuadrimestre;
    }

    public String getNotaExplicativa() {
        return notaExplicativa;
    }

    public void setNotaExplicativa(String notaExplicativa) {
        this.notaExplicativa = notaExplicativa;
    }
}

