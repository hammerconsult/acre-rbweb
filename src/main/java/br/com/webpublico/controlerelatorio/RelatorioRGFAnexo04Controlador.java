package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.RGFAnexo04ItemApuracao;
import br.com.webpublico.entidadesauxiliares.RGFAnexo04item;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo04Facade;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mateus on 26/04/18.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo4", pattern = "/relatorio/rgf/anexo4/", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo04.xhtml")
})
public class RelatorioRGFAnexo04Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    @EJB
    private RelatorioRGFAnexo04Facade relatorioRGFAnexo04Facade;
    private BigDecimal totalInternaAteQuadrimestre;
    private BigDecimal totalExternaAteQuadrimestre;
    private BigDecimal totalGeralAteQuadrimestre;

    public RelatorioRGFAnexo04Controlador() {
        super();
    }

    private List<RGFAnexo04item> buscarDadosGrupoUm() {
        List<RGFAnexo04item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente it : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo04Facade.getItemDemonstrativoFacade().recuperaPorGrupo(it.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo04item item = new RGFAnexo04item();
                item.setDescricao(it.getDescricaoComEspacos());
                atualizarDatas(getMesInicial(), mes);
                if (Mes.ABRIL.equals(mes)) {
                    item.setValorNoQuadrimestre(calcularValorAteQuadrimestre(itemDemonstrativo));
                } else {
                    item.setValorNoQuadrimestre(calcularValorNoQuadrimestre(itemDemonstrativo));
                }
                atualizarDatas(Mes.JANEIRO, mes);
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
        List<RGFAnexo04ItemApuracao> toReturn = Lists.newArrayList();
        BigDecimal rcl = calcularRcl();
        BigDecimal transferenciasObrigatorias = calcularTransferenciasObrigatorias();
        BigDecimal rclAjustada = rcl.subtract(transferenciasObrigatorias);
        for (ItemDemonstrativoComponente it : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo04Facade.getItemDemonstrativoFacade().recuperaPorGrupo(it.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo04ItemApuracao item = new RGFAnexo04ItemApuracao();
                item.setDescricao(it.getDescricaoComEspacos());
                atualizarDatas(Mes.JANEIRO, mes);
                switch (it.getOrdem()) {
                    case 1:
                        item.setValor(rcl);
                        break;
                    case 2:
                        item.setValor(transferenciasObrigatorias);
                        break;
                    case 3:
                        item.setValor(rclAjustada);
                        break;
                    case 5:
                        item.setValor(totalGeralAteQuadrimestre.subtract(totalInternaAteQuadrimestre).subtract(totalExternaAteQuadrimestre));
                        if (rclAjustada.compareTo(BigDecimal.ZERO) != 0) {
                            item.setPercentual((item.getValor().divide(rclAjustada, 6, BigDecimal.ROUND_HALF_EVEN)));
                        }
                        break;
                    case 6:
                        if (rclAjustada.compareTo(BigDecimal.ZERO) != 0) {
                            item.setValor(rclAjustada.multiply(BigDecimal.valueOf(16).divide(BigDecimal.valueOf(100), 6, BigDecimal.ROUND_HALF_EVEN)));
                        }
                        item.setPercentual(BigDecimal.valueOf(16).divide(BigDecimal.valueOf(100), 6, BigDecimal.ROUND_HALF_EVEN));
                        break;
                    case 7:
                        if (rclAjustada.compareTo(BigDecimal.ZERO) != 0) {
                            item.setValor(rclAjustada.multiply(BigDecimal.valueOf(14.4).divide(BigDecimal.valueOf(100), 6, BigDecimal.ROUND_HALF_EVEN)));
                        }
                        item.setPercentual(BigDecimal.valueOf(14.4).divide(BigDecimal.valueOf(100), 6, BigDecimal.ROUND_HALF_EVEN));
                        break;
                    case 9:
                        if (rclAjustada.compareTo(BigDecimal.ZERO) != 0) {
                            item.setValor(rclAjustada.multiply(BigDecimal.valueOf(7).divide(BigDecimal.valueOf(100), 6, BigDecimal.ROUND_HALF_EVEN)));
                        }
                        item.setPercentual(BigDecimal.valueOf(7).divide(BigDecimal.valueOf(100), 6, BigDecimal.ROUND_HALF_EVEN));
                        break;
                }
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    public List<RGFAnexo04ItemApuracao> preparaDadosAnexo4() {
        relatoriosItemDemonst = relatorioRGFAnexo04Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", exercicio, TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativo> itensDemonstrativos = relatorioRGFAnexo04Facade.getItemDemonstrativoFacade().buscarItensPorExercicioAndRelatorio(exercicio, "", "Anexo 4", TipoRelatorioItemDemonstrativo.RGF);
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

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        Exercicio exAnterior = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1);
        RelatoriosItemDemonst relatorioAnexo01 = relatorioRGFAnexo04Facade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", exercicio, TipoRelatorioItemDemonstrativo.RGF);
        RelatoriosItemDemonst relatorioAnexo03 = relatorioRGFAnexo04Facade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatoriosItemDemonst = relatorioRGFAnexo04Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", exercicio, TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativoComponente> itensAnexo01 = popularConfiguracoesDoRelatorioAnexo01(exercicio);
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("ID_EXERCICIO", exercicio.getId());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("ID_EXERCICIO_ANTERIOR", exAnterior.getId());
        dto.adicionarParametro("DATAINICIAL", getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro("DATAFINAL", mes.getDescricao().toUpperCase());
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
        dto.adicionarParametro("NOTAEXPLICATIVA", "NOTA: " + notaExplicativa.trim());
        dto.adicionarParametro("mesFinal", mes.getToDto());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("itensAnexo01", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itensAnexo01));
        dto.adicionarParametro("relatorioAnexo01", relatorioAnexo01.toDto());
        dto.adicionarParametro("relatorioAnexo03", relatorioAnexo03.toDto());
        dto.adicionarParametro("relatoriosItemDemonstDTO", relatoriosItemDemonst.toDto());
        dto.setNomeRelatorio("LRF-RGF-ANEXO-4-" + exercicio.getAno() + "-" + mes.getNumeroMesString());
        salvarNotaExplicativaRGF();
    }

    private List<ItemDemonstrativoComponente> popularConfiguracoesDoRelatorioAnexo01(Exercicio exercicio) {
        List<ItemDemonstrativoComponente> itens = Lists.newArrayList();
        List<ItemDemonstrativo> itensDemonstrativos = relatorioRGFAnexo04Facade.getItemDemonstrativoFacade().buscarItensPorExercicioAndRelatorio(exercicio, "", "Anexo 1", TipoRelatorioItemDemonstrativo.RGF);
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
        return itens;
    }

    private Mes getMesInicial() {
        if (Mes.ABRIL.equals(mes)) {
            return Mes.JANEIRO;
        } else if (Mes.AGOSTO.equals(mes)) {
            return Mes.MAIO;
        } else {
            return Mes.SETEMBRO;
        }
    }

    public void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        if (relatoriosItemDemonst == null) {
            relatoriosItemDemonst = relatorioRGFAnexo04Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", exercicio, TipoRelatorioItemDemonstrativo.RGF);
        }
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltros.setExercicio(exercicio);
    }

    private void atualizarDatas(Mes mesInicial, Mes mes) {
        itemDemonstrativoFiltros.setDataInicial("01/" + mesInicial.getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(mes.getNumeroMes(), exercicio.getAno()) + "/" + mes.getNumeroMesString() + "/" + exercicio.getAno());
    }

    @URLAction(mappingId = "relatorio-rgf-anexo4", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        totalInternaAteQuadrimestre = BigDecimal.ZERO;
        totalExternaAteQuadrimestre = BigDecimal.ZERO;
        totalGeralAteQuadrimestre = BigDecimal.ZERO;
        portalTipoAnexo = PortalTipoAnexo.ANEXO4_RGF;
        super.limparCampos();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioAnexo04RGFOperacoes";
    }

    @Override
    public String getApi() {
        return "contabil/rgf-anexo4/";
    }

    private BigDecimal calcularRcl() {
        return relatorioRGFAnexo04Facade.getRelatorioRREOAnexo03Calculator().calcularRcl(mes, exercicio);
    }

    private BigDecimal calcularTransferenciasObrigatorias() {
        return relatorioRGFAnexo04Facade.recuperarValorTransferencias(mes, exercicio);
    }

    private BigDecimal calcularValorNoQuadrimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRGFAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RECEITA_REALIZADA_NO_BIMESTRE);
    }

    private BigDecimal calcularValorAteQuadrimestre(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRGFAnexo04Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RECEITA_REALIZADA_ATE_BIMESTRE);
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

    public BigDecimal getTotalGeralAteQuadrimestre() {
        return totalGeralAteQuadrimestre;
    }

    public void setTotalGeralAteQuadrimestre(BigDecimal totalGeralAteQuadrimestre) {
        this.totalGeralAteQuadrimestre = totalGeralAteQuadrimestre;
    }
}

