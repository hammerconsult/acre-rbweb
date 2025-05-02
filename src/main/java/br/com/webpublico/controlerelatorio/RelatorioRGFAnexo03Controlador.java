package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.RGFAnexo03Item;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo03Facade;
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
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by mateus on 25/04/18.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo3", pattern = "/relatorio/rgf/anexo3/", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo03.xhtml")
})
public class RelatorioRGFAnexo03Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    @EJB
    private RelatorioRGFAnexo03Facade relatorioRGFAnexo03Facade;

    public RelatorioRGFAnexo03Controlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-rgf-anexo3", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        portalTipoAnexo = PortalTipoAnexo.ANEXO3_RGF;
        super.limparCampos();
    }

    private List<RGFAnexo03Item> buscarGarantias() {
        List<RGFAnexo03Item> toReturn = Lists.newArrayList();
        BigDecimal valorExercicioAnterior = BigDecimal.ZERO;
        BigDecimal valorPrimeiroQuadrimestre = BigDecimal.ZERO;
        BigDecimal valorSegundoQuadrimestre = BigDecimal.ZERO;
        BigDecimal valorTerceiroQuadrimestre = BigDecimal.ZERO;

        BigDecimal percentualExercicioAnterior = BigDecimal.ZERO;
        BigDecimal percentualPrimeiroQuadrimestre = BigDecimal.ZERO;
        BigDecimal percentualSegundoQuadrimestre = BigDecimal.ZERO;
        BigDecimal percentualTerceiroQuadrimestre = BigDecimal.ZERO;

        BigDecimal rclAnterior = BigDecimal.ZERO;
        BigDecimal rclPrimeiroQuadrimestre = BigDecimal.ZERO;
        BigDecimal rclSegundoQuadrimestre = BigDecimal.ZERO;
        BigDecimal rclTerceiroQuadrimestre = BigDecimal.ZERO;

        BigDecimal rclAjustadaAnterior = BigDecimal.ZERO;
        BigDecimal rclAjustadaPrimeiroQuadrimestre = BigDecimal.ZERO;
        BigDecimal rclAjustadaSegundoQuadrimestre = BigDecimal.ZERO;
        BigDecimal rclAjustadaTerceiroQuadrimestre = BigDecimal.ZERO;

        BigDecimal transferenciasObrigatoriasAnterior = BigDecimal.ZERO;
        BigDecimal transferenciasObrigatoriasPrimeiroQuadrimestre = BigDecimal.ZERO;
        BigDecimal transferenciasObrigatoriasSegundoQuadrimestre = BigDecimal.ZERO;
        BigDecimal transferenciasObrigatoriasTerceiroQuadrimestre = BigDecimal.ZERO;
        for (ItemDemonstrativoComponente it : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo03Facade.getItemDemonstrativoFacade().recuperaPorGrupo(it.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo03Item item = new RGFAnexo03Item();
                item.setDescricao(it.getDescricaoComEspacos());
                switch (it.getOrdem()) {
                    case 12:
                        item.setExercicioAnterior(calcularRclExercicioAnterior());
                        item.setPrimeiroQuadrimestre(calcularRcl(Mes.ABRIL));
                        if (Mes.AGOSTO.equals(mes) || Mes.DEZEMBRO.equals(mes)) {
                            item.setSegundoQuadrimestre(calcularRcl(Mes.AGOSTO));
                        }
                        if (Mes.DEZEMBRO.equals(mes)) {
                            item.setTerceiroQuadrimestre(calcularRcl(Mes.DEZEMBRO));
                        }
                        rclAnterior = item.getExercicioAnterior();
                        rclPrimeiroQuadrimestre = item.getPrimeiroQuadrimestre();
                        rclSegundoQuadrimestre = item.getSegundoQuadrimestre();
                        rclTerceiroQuadrimestre = item.getTerceiroQuadrimestre();
                        break;
                    case 13:
                        item.setExercicioAnterior(calcularTransferenciasObrigatoriasExercicioAnterior());
                        item.setPrimeiroQuadrimestre(calcularTransferenciasObrigatorias(Mes.ABRIL));
                        if (Mes.AGOSTO.equals(mes) || Mes.DEZEMBRO.equals(mes)) {
                            item.setSegundoQuadrimestre(calcularTransferenciasObrigatorias(Mes.AGOSTO));
                        }
                        if (Mes.DEZEMBRO.equals(mes)) {
                            item.setTerceiroQuadrimestre(calcularTransferenciasObrigatorias(Mes.DEZEMBRO));
                        }
                        transferenciasObrigatoriasAnterior = item.getExercicioAnterior();
                        transferenciasObrigatoriasPrimeiroQuadrimestre = item.getPrimeiroQuadrimestre();
                        transferenciasObrigatoriasSegundoQuadrimestre = item.getSegundoQuadrimestre();
                        transferenciasObrigatoriasTerceiroQuadrimestre = item.getTerceiroQuadrimestre();
                        break;
                    case 14:
                        item.setExercicioAnterior(rclAnterior.subtract(transferenciasObrigatoriasAnterior));
                        item.setPrimeiroQuadrimestre(rclPrimeiroQuadrimestre.subtract(transferenciasObrigatoriasPrimeiroQuadrimestre));
                        if (Mes.AGOSTO.equals(mes) || Mes.DEZEMBRO.equals(mes)) {
                            item.setSegundoQuadrimestre(rclSegundoQuadrimestre.subtract(transferenciasObrigatoriasSegundoQuadrimestre));
                        }
                        if (Mes.DEZEMBRO.equals(mes)) {
                            item.setTerceiroQuadrimestre(rclTerceiroQuadrimestre.subtract(transferenciasObrigatoriasTerceiroQuadrimestre));
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
                        rclAjustadaAnterior = item.getExercicioAnterior();
                        rclAjustadaPrimeiroQuadrimestre = item.getPrimeiroQuadrimestre();
                        rclAjustadaSegundoQuadrimestre = item.getSegundoQuadrimestre();
                        rclAjustadaTerceiroQuadrimestre = item.getTerceiroQuadrimestre();
                        break;
                    case 15:
                        item.setExercicioAnterior(percentualExercicioAnterior);
                        item.setPrimeiroQuadrimestre(percentualPrimeiroQuadrimestre);
                        if (Mes.AGOSTO.equals(mes) || Mes.DEZEMBRO.equals(mes)) {
                            item.setSegundoQuadrimestre(percentualSegundoQuadrimestre);
                        }
                        if (Mes.DEZEMBRO.equals(mes)) {
                            item.setTerceiroQuadrimestre(percentualTerceiroQuadrimestre);
                        }
                        break;
                    case 16:
                        if (rclAjustadaAnterior.compareTo(BigDecimal.ZERO) > 0) {
                            item.setExercicioAnterior(rclAjustadaAnterior.multiply(BigDecimal.valueOf(22).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)).setScale(2, RoundingMode.DOWN));
                        }
                        if (rclAjustadaPrimeiroQuadrimestre.compareTo(BigDecimal.ZERO) > 0) {
                            item.setPrimeiroQuadrimestre(rclAjustadaPrimeiroQuadrimestre.multiply(BigDecimal.valueOf(22).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)).setScale(2, RoundingMode.DOWN));
                        }
                        if (rclAjustadaSegundoQuadrimestre.compareTo(BigDecimal.ZERO) > 0) {
                            item.setSegundoQuadrimestre(rclAjustadaSegundoQuadrimestre.multiply(BigDecimal.valueOf(22).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)).setScale(2, RoundingMode.DOWN));
                        }
                        if (rclAjustadaTerceiroQuadrimestre.compareTo(BigDecimal.ZERO) > 0) {
                            item.setTerceiroQuadrimestre(rclAjustadaTerceiroQuadrimestre.multiply(BigDecimal.valueOf(22).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)).setScale(2, RoundingMode.DOWN));
                        }
                        break;
                    case 17:
                        if (rclAjustadaAnterior.compareTo(BigDecimal.ZERO) > 0) {
                            item.setExercicioAnterior(rclAjustadaAnterior.multiply(BigDecimal.valueOf(19.8).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                        }
                        if (rclAjustadaPrimeiroQuadrimestre.compareTo(BigDecimal.ZERO) > 0) {
                            item.setPrimeiroQuadrimestre(rclAjustadaPrimeiroQuadrimestre.multiply(BigDecimal.valueOf(19.8).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                        }
                        if (rclAjustadaSegundoQuadrimestre.compareTo(BigDecimal.ZERO) > 0) {
                            item.setSegundoQuadrimestre(rclAjustadaSegundoQuadrimestre.multiply(BigDecimal.valueOf(19.8).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                        }
                        if (rclAjustadaTerceiroQuadrimestre.compareTo(BigDecimal.ZERO) > 0) {
                            item.setTerceiroQuadrimestre(rclAjustadaTerceiroQuadrimestre.multiply(BigDecimal.valueOf(19.8).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                        }
                        break;

                    default:
                        atualizarDatas(Mes.JANEIRO, Mes.DEZEMBRO, getExercicioAnterior());
                        item.setExercicioAnterior(buscarValorReceitaRealizada(itemDemonstrativo));
                        atualizarDatas(Mes.JANEIRO, Mes.ABRIL, exercicio);
                        item.setPrimeiroQuadrimestre(buscarValorReceitaRealizada(itemDemonstrativo));
                        if (Mes.AGOSTO.equals(mes) || Mes.DEZEMBRO.equals(mes)) {
                            atualizarDatas(Mes.MAIO, Mes.AGOSTO, exercicio);
                            item.setSegundoQuadrimestre(buscarValorReceitaRealizada(itemDemonstrativo));
                        }
                        if (Mes.DEZEMBRO.equals(mes)) {
                            atualizarDatas(Mes.SETEMBRO, Mes.DEZEMBRO, exercicio);
                            item.setTerceiroQuadrimestre(buscarValorReceitaRealizada(itemDemonstrativo));
                        }
                        if (it.getOrdem() == 11) {
                            valorExercicioAnterior = item.getExercicioAnterior();
                            valorPrimeiroQuadrimestre = item.getPrimeiroQuadrimestre();
                            valorSegundoQuadrimestre = item.getSegundoQuadrimestre();
                            valorTerceiroQuadrimestre = item.getTerceiroQuadrimestre();
                        }
                        break;
                }
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    public List<RGFAnexo03Item> buscarDadosPopulandoItens() {
        relatoriosItemDemonst = relatorioRGFAnexo03Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", exercicio, TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativo> itensDemonstrativos = relatorioRGFAnexo03Facade.getItemDemonstrativoFacade().buscarItensPorExercicioAndRelatorio(exercicio, "", "Anexo 3", TipoRelatorioItemDemonstrativo.RGF);
        itens = Lists.newArrayList();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
        instanciarItemDemonstrativoFiltros();
        return buscarGarantias();
    }

    private BigDecimal calcularRcl(Mes mes) {
        return relatorioRGFAnexo03Facade.getRelatorioRREOAnexo03Calculator().calcularRcl(mes, exercicio);
    }

    private BigDecimal calcularRclExercicioAnterior() {
        return relatorioRGFAnexo03Facade.getRelatorioRREOAnexo03Calculator().calcularRclExercicioAnterior(Mes.DEZEMBRO);
    }

    private BigDecimal calcularTransferenciasObrigatoriasExercicioAnterior() {
        return relatorioRGFAnexo03Facade.recuperarValorTransferencias(Mes.DEZEMBRO, relatorioRGFAnexo03Facade.buscarExercicioAnterior());
    }

    private BigDecimal calcularTransferenciasObrigatorias(Mes mes) {
        return relatorioRGFAnexo03Facade.recuperarValorTransferencias(mes, exercicio);
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        Exercicio exAnterior = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1);
        Exercicio exAnteriorAnterior = getExercicioFacade().getExercicioPorAno(exAnterior.getAno() - 1);
        RelatoriosItemDemonst relatoriosItemDemonst = relatorioRGFAnexo03Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", exercicio, TipoRelatorioItemDemonstrativo.RGF);
        RelatoriosItemDemonst relatoriosItemDemonstExAnterior = relatorioRGFAnexo03Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", exAnterior, TipoRelatorioItemDemonstrativo.RGF);
        RelatoriosItemDemonst relatorioAnexo03 = relatorioRGFAnexo03Facade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo03ExAnterior = relatorioRGFAnexo03Facade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", exAnterior, TipoRelatorioItemDemonstrativo.RREO);
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("ID_EXERCICIO", exercicio.getId());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno());
        dto.adicionarParametro("ID_EXERCICIO_ANTERIOR", exAnterior.getId());
        dto.adicionarParametro("ANO_EXERCICIO_ANTERIOR", exAnterior.getAno());
        dto.adicionarParametro("ID_EXERCICIO_ANTERIOR_ANTERIOR", exAnteriorAnterior.getId());
        dto.adicionarParametro("MEDIDACORRETIVA", "");
        dto.adicionarParametro("DATAINICIAL", getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro("DATAFINAL", " " + mes.getDescricao().toUpperCase() + " DE " + exercicio.getAno());
        dto.adicionarParametro("NOTAEXPLICATIVA", "NOTA: " + notaExplicativa.trim());
        dto.adicionarParametro("mesFinal", mes.getToDto());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("relatoriosItemDemonstDTO", relatoriosItemDemonst.toDto());
        dto.adicionarParametro("relatoriosItemDemonstDTOExAnterior", relatoriosItemDemonstExAnterior.toDto());
        dto.adicionarParametro("relatorioAnexo03", relatorioAnexo03.toDto());
        dto.adicionarParametro("relatorioAnexo03ExAnterior", relatorioAnexo03ExAnterior.toDto());
        dto.setNomeRelatorio("LRF-RGF-ANEXO-3-" + exercicio.getAno() + "-" + mes.getNumeroMesString());
        salvarNotaExplicativaRGF();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioAnexo03RGFGarantias";
    }

    @Override
    public String getApi() {
        return "contabil/rgf-anexo3/";
    }

    private void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
    }

    private void atualizarDatas(Mes mesInicial, Mes mes, Exercicio exercicio) {
        itemDemonstrativoFiltros.setDataInicial("01/" + mesInicial.getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(mes.getNumeroMes(), exercicio.getAno()) + "/" + mes.getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setExercicio(exercicio);
    }

    private Exercicio getExercicioAnterior() {
        return getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1);
    }

    private BigDecimal buscarValorReceitaRealizada(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRGFAnexo03Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RECEITA_REALIZADA_ATE_BIMESTRE);
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
}

