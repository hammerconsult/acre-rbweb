package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.RGFAnexo02Item;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo02Facade;
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
 * Created by Mateus on 28/08/2014.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo2", pattern = "/relatorio/rgf/anexo2/", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo02.xhtml")
})
public class RelatorioRGFAnexo02Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    @EJB
    private RelatorioRGFAnexo02Facade relatorioRGFAnexo02Facade;

    public RelatorioRGFAnexo02Controlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-rgf-anexo2", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        portalTipoAnexo = PortalTipoAnexo.ANEXO2_RGF;
        super.limparCampos();
    }

    private List<RGFAnexo02Item> buscarDadosGrupo1() {
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
        List<RGFAnexo02Item> retorno = Lists.newArrayList();
        for (ItemDemonstrativoComponente itemComponente : itens) {
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo02Facade.getItemDemonstrativoFacade().recuperaPorGrupo(itemComponente.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo02Item rgfItem = new RGFAnexo02Item();
                rgfItem.setDescricao(itemComponente.getDescricaoComEspacos());
                switch (itemComponente.getOrdem()) {
                    case 26:
                        rclAnterior = calcularRclAjustaExercicioAnterior();
                        rclPrimeiroQuadrimestre = calcularRcl(Mes.ABRIL);
                        rgfItem.setSaldoAnterior(rclAnterior);
                        rgfItem.setPrimeiroQuadrimestre(rclPrimeiroQuadrimestre);
                        if (Mes.AGOSTO.equals(mes) || Mes.DEZEMBRO.equals(mes)) {
                            rclSegundoQuadrimestre = calcularRcl(Mes.AGOSTO);
                            rgfItem.setSegundoQuadrimestre(rclSegundoQuadrimestre);
                        }
                        if (Mes.DEZEMBRO.equals(mes)) {
                            rclTerceiroQuadrimestre = calcularRcl(Mes.DEZEMBRO);
                            rgfItem.setTerceiroQuadrimestre(rclTerceiroQuadrimestre);
                        }
                        break;
                    case 27:
                        transferenciasObrigatoriasAnterior = calcularTransferenciasObrigatoriasExercicioAnterior();
                        transferenciasObrigatoriasPrimeiroQuadrimestre = calcularTransferenciasObrigatorias(Mes.ABRIL);
                        rgfItem.setSaldoAnterior(transferenciasObrigatoriasAnterior);
                        rgfItem.setPrimeiroQuadrimestre(transferenciasObrigatoriasPrimeiroQuadrimestre);
                        if (Mes.AGOSTO.equals(mes) || Mes.DEZEMBRO.equals(mes)) {
                            transferenciasObrigatoriasSegundoQuadrimestre = calcularTransferenciasObrigatorias(Mes.AGOSTO);
                            rgfItem.setSegundoQuadrimestre(transferenciasObrigatoriasSegundoQuadrimestre);
                        }
                        if (Mes.DEZEMBRO.equals(mes)) {
                            transferenciasObrigatoriasTerceiroQuadrimestre = calcularTransferenciasObrigatorias(Mes.DEZEMBRO);
                            rgfItem.setTerceiroQuadrimestre(transferenciasObrigatoriasTerceiroQuadrimestre);
                        }
                        break;
                    case 28:
                        rclAjustadaAnterior = rclAnterior.subtract(transferenciasObrigatoriasAnterior);
                        rclAjustadaPrimeiroQuadrimestre = rclPrimeiroQuadrimestre.subtract(transferenciasObrigatoriasPrimeiroQuadrimestre);
                        rgfItem.setSaldoAnterior(rclAjustadaAnterior);
                        rgfItem.setPrimeiroQuadrimestre(rclAjustadaPrimeiroQuadrimestre);
                        if (Mes.AGOSTO.equals(mes) || Mes.DEZEMBRO.equals(mes)) {
                            rclAjustadaSegundoQuadrimestre = rclSegundoQuadrimestre.subtract(transferenciasObrigatoriasSegundoQuadrimestre);
                            rgfItem.setSegundoQuadrimestre(rclAjustadaSegundoQuadrimestre);
                        }
                        if (Mes.DEZEMBRO.equals(mes)) {
                            rclAjustadaTerceiroQuadrimestre = rclTerceiroQuadrimestre.subtract(transferenciasObrigatoriasTerceiroQuadrimestre);
                            rgfItem.setTerceiroQuadrimestre(rclAjustadaTerceiroQuadrimestre);
                        }
                        break;
                    case 29:
                    case 30:
                        atualizarDatas(Mes.JANEIRO, Mes.ABRIL);
                        rgfItem.setSaldoAnterior(buscarValorContabilTransporte(itemDemonstrativo).divide(rclAjustadaAnterior, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                        rgfItem.setPrimeiroQuadrimestre(buscarValorContabil(itemDemonstrativo).divide(rclAjustadaPrimeiroQuadrimestre, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                        if (Mes.AGOSTO.equals(mes) || Mes.DEZEMBRO.equals(mes)) {
                            atualizarDatas(Mes.MAIO, Mes.AGOSTO);
                            rgfItem.setSegundoQuadrimestre(buscarValorContabil(itemDemonstrativo).divide(rclAjustadaSegundoQuadrimestre, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                        }
                        if (Mes.DEZEMBRO.equals(mes)) {
                            atualizarDatas(Mes.SETEMBRO, Mes.DEZEMBRO);
                            rgfItem.setTerceiroQuadrimestre(buscarValorContabil(itemDemonstrativo).divide(rclAjustadaTerceiroQuadrimestre, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                        }
                        break;
                    case 31:
                        rgfItem.setSaldoAnterior(rclAjustadaAnterior.add(rclAjustadaAnterior.multiply(BigDecimal.valueOf(0.20))));
                        rgfItem.setPrimeiroQuadrimestre(rclAjustadaPrimeiroQuadrimestre.add(rclAjustadaPrimeiroQuadrimestre.multiply(BigDecimal.valueOf(0.20))));
                        if (Mes.AGOSTO.equals(mes) || Mes.DEZEMBRO.equals(mes)) {
                            rgfItem.setSegundoQuadrimestre(rclAjustadaSegundoQuadrimestre.add(rclAjustadaSegundoQuadrimestre.multiply(BigDecimal.valueOf(0.20))));
                        }
                        if (Mes.DEZEMBRO.equals(mes)) {
                            rgfItem.setTerceiroQuadrimestre(rclAjustadaTerceiroQuadrimestre.add(rclAjustadaTerceiroQuadrimestre.multiply(BigDecimal.valueOf(0.20))));
                        }
                        break;
                    case 32:
                        rgfItem.setSaldoAnterior(rclAjustadaAnterior.add(rclAjustadaAnterior.multiply(BigDecimal.valueOf(0.08))));
                        rgfItem.setPrimeiroQuadrimestre(rclAjustadaPrimeiroQuadrimestre.add(rclAjustadaPrimeiroQuadrimestre.multiply(BigDecimal.valueOf(0.08))));
                        if (Mes.AGOSTO.equals(mes) || Mes.DEZEMBRO.equals(mes)) {
                            rgfItem.setSegundoQuadrimestre(rclAjustadaSegundoQuadrimestre.add(rclAjustadaSegundoQuadrimestre.multiply(BigDecimal.valueOf(0.08))));
                        }
                        if (Mes.DEZEMBRO.equals(mes)) {
                            rgfItem.setTerceiroQuadrimestre(rclAjustadaTerceiroQuadrimestre.add(rclAjustadaTerceiroQuadrimestre.multiply(BigDecimal.valueOf(0.08))));
                        }
                        break;
                    default:
                        atualizarDatas(Mes.JANEIRO, Mes.ABRIL);
                        rgfItem.setSaldoAnterior(buscarValorContabilTransporte(itemDemonstrativo));
                        rgfItem.setPrimeiroQuadrimestre(buscarValorContabil(itemDemonstrativo));
                        if (Mes.AGOSTO.equals(mes) || Mes.DEZEMBRO.equals(mes)) {
                            atualizarDatas(Mes.MAIO, Mes.AGOSTO);
                            rgfItem.setSegundoQuadrimestre(buscarValorContabil(itemDemonstrativo));
                        }
                        if (Mes.DEZEMBRO.equals(mes)) {
                            atualizarDatas(Mes.SETEMBRO, Mes.DEZEMBRO);
                            rgfItem.setTerceiroQuadrimestre(buscarValorContabil(itemDemonstrativo));
                        }
                        break;
                }
                retorno.add(rgfItem);
            }
        }
        return retorno;
    }

    public List<RGFAnexo02Item> buscarDadosGrupo1PopulandoItens() {
        relatoriosItemDemonst = relatorioRGFAnexo02Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 2", exercicio, TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativo> itensDemonstrativos = relatorioRGFAnexo02Facade.getItemDemonstrativoFacade().buscarItensPorExercicioAndRelatorio(exercicio, "", "Anexo 2", TipoRelatorioItemDemonstrativo.RGF);
        itens = Lists.newArrayList();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
        instanciarItemDemonstrativoFiltros();
        return buscarDadosGrupo1();
    }

    private void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltros.setExercicio(exercicio);
    }

    private void atualizarDatas(Mes mesInicial, Mes mesFinal) {
        itemDemonstrativoFiltros.setDataInicial("01/" + mesInicial.getNumeroMesString() + "/" + exercicio.getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(mesFinal.getNumeroMes(), exercicio.getAno()) + "/" + mesFinal.getNumeroMesString() + "/" + exercicio.getAno());
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        Exercicio exAnterior = getExercicioFacade().getExercicioPorAno(exercicio.getAno() - 1);
        Exercicio exAnteriorAnterior = getExercicioFacade().getExercicioPorAno(exAnterior.getAno() - 1);
        RelatoriosItemDemonst relatorioAnexo02 = relatorioRGFAnexo02Facade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 2", exercicio, TipoRelatorioItemDemonstrativo.RGF);
        RelatoriosItemDemonst relatorioAnexo02ExAnterior = relatorioRGFAnexo02Facade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 2", exAnterior, TipoRelatorioItemDemonstrativo.RGF);
        RelatoriosItemDemonst relatorioAnexo03 = relatorioRGFAnexo02Facade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", exercicio, TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo03ExAnterior = relatorioRGFAnexo02Facade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", exAnterior, TipoRelatorioItemDemonstrativo.RREO);
        List<ItemDemonstrativoComponente> itensAnexo01 = popularConfiguracoesDoRelatorioAnexo01(exercicio);
        List<ItemDemonstrativoComponente> itensAnexo01ExAnterior = popularConfiguracoesDoRelatorioAnexo01(exAnterior);
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome());
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("ID_EXERCICIO", exercicio.getId());
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno());
        dto.adicionarParametro("ID_EXERCICIO_ANTERIOR", exAnterior.getId());
        dto.adicionarParametro("ANO_EXERCICIO_ANTERIOR", exAnterior.getAno());
        dto.adicionarParametro("ID_EXERCICIO_ANTERIOR_ANTERIOR", exAnteriorAnterior.getId());
        dto.adicionarParametro("DATAINICIAL", getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro("DATAFINAL", mes.getDescricao().toUpperCase() + " DE " + exercicio.getAno());
        dto.adicionarParametro("NOTAEXPLICATIVA", "NOTA: " + notaExplicativa.trim());
        dto.adicionarParametro("mesFinal", mes.getToDto());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("relatorioAnexo02", relatorioAnexo02.toDto());
        dto.adicionarParametro("relatorioAnexo02ExAnterior", relatorioAnexo02ExAnterior.toDto());
        dto.adicionarParametro("relatorioAnexo03", relatorioAnexo03.toDto());
        dto.adicionarParametro("relatorioAnexo03ExAnterior", relatorioAnexo03ExAnterior.toDto());
        dto.setNomeRelatorio("LRF-RGF-ANEXO-2-" + exercicio.getAno() + "-" + mes.getNumeroMesString());
        salvarNotaExplicativaRGF();
    }

    private List<ItemDemonstrativoComponente> popularConfiguracoesDoRelatorioAnexo01(Exercicio exercicio) {
        List<ItemDemonstrativoComponente> itens = Lists.newArrayList();
        List<ItemDemonstrativo> itensDemonstrativos = relatorioRGFAnexo02Facade.getItemDemonstrativoFacade().buscarItensPorExercicioAndRelatorio(exercicio, "", "Anexo 1", TipoRelatorioItemDemonstrativo.RGF);
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
        return itens;
    }

    private BigDecimal calcularRcl(Mes mes) {
        return relatorioRGFAnexo02Facade.getRelatorioRREOAnexo03Calculator().calcularRcl(mes, exercicio);
    }

    private BigDecimal calcularRclAjustaExercicioAnterior() {
        return relatorioRGFAnexo02Facade.getRelatorioRREOAnexo03Calculator().calcularRclExercicioAnterior(Mes.DEZEMBRO);
    }

    private BigDecimal calcularTransferenciasObrigatoriasExercicioAnterior() {
        return relatorioRGFAnexo02Facade.recuperarValorTransferencias(Mes.DEZEMBRO, relatorioRGFAnexo02Facade.buscarExercicioAnterior());
    }

    private BigDecimal calcularTransferenciasObrigatorias(Mes mes) {
        return relatorioRGFAnexo02Facade.recuperarValorTransferencias(mes, exercicio);
    }

    private BigDecimal buscarValorContabil(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRGFAnexo02Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.SALDO_CONTABIL_ATUAL);
    }

    private BigDecimal buscarValorContabilTransporte(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRGFAnexo02Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.SALDO_CONTABIL_TRANSPORTE);
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


    @Override
    public String getNomeArquivo() {
        return "RelatorioRGFAnexo02";
    }

    @Override
    public String getApi() {
        return "contabil/rgf-anexo2/";
    }
}
