/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.RGFAnexo03Item;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo03Calculator2017;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-rgf-anexo3-2017", pattern = "/relatorio/rgf/anexo3/2017", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo03-2017.xhtml")})
public class RelatorioRGFAnexo03Controlador2017 extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private RelatorioRGFAnexo03Calculator2017 relatorioRGFAnexo03Calculator2017;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    private String mesFinal;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private List<ItemDemonstrativoComponente> itens;

    public RelatorioRGFAnexo03Controlador2017() {
        itens = new ArrayList<ItemDemonstrativoComponente>();
    }

    @URLAction(mappingId = "relatorio-rgf-anexo3-2017", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
    }

    private List<RGFAnexo03Item> geraDados() {
        List<RGFAnexo03Item> toReturn = new ArrayList<>();
        RGFAnexo03Item item = new RGFAnexo03Item();
        item.setDescricao("");
        toReturn.add(item);
        return toReturn;
    }

    private List<RGFAnexo03Item> geraDadosRelatorio() {
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
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(it.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo03Item item = new RGFAnexo03Item();
                String descricao = StringUtil.preencheString("", it.getEspaco(), ' ') + it.getNome();
                item.setDescricao(descricao);
                if (descricao.equals("RECEITA CORRENTE LÍQUIDA - RCL (IV)")) {
                    item.setExercicioAnterior(calculaRclExercicioAnterior());
                    item.setPrimeiroQuadrimestre(calculaRclPrimeiroQuadrimestre());
                    if (mesFinal.equals("08") || mesFinal.equals("12")) {
                        item.setSegundoQuadrimestre(calculaRclSegundoQuadrimestre());
                    } else {
                        item.setSegundoQuadrimestre(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        item.setTerceiroQuadrimestre(calculaRclTerceiroQuadrimestre());
                    } else {
                        item.setTerceiroQuadrimestre(BigDecimal.ZERO);
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
                } else if (descricao.equals("LIMITE DEFINIDO POR RESOLUÇÃO DO SENADO FEDERAL - 22%")) {
                    item.setDescricao(descricao);
                    item.setExercicioAnterior(BigDecimal.ZERO);
                    item.setPrimeiroQuadrimestre(BigDecimal.ZERO);
                    item.setSegundoQuadrimestre(BigDecimal.ZERO);
                    item.setTerceiroQuadrimestre(BigDecimal.ZERO);
                    if (valorExercicioAnteriorRcl.compareTo(BigDecimal.ZERO) > 0) {
                        item.setExercicioAnterior(valorExercicioAnteriorRcl.multiply(BigDecimal.valueOf(22).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                    }
                    if (valorPrimeiroQuadrimestreRcl.compareTo(BigDecimal.ZERO) > 0) {
                        item.setPrimeiroQuadrimestre(valorPrimeiroQuadrimestreRcl.multiply(BigDecimal.valueOf(22).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                    }
                    if (valorSegundoQuadrimestreRcl.compareTo(BigDecimal.ZERO) > 0) {
                        item.setSegundoQuadrimestre(valorSegundoQuadrimestreRcl.multiply(BigDecimal.valueOf(22).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                    }
                    if (valorTerceiroQuadrimestreRcl.compareTo(BigDecimal.ZERO) > 0) {
                        item.setTerceiroQuadrimestre(valorTerceiroQuadrimestreRcl.multiply(BigDecimal.valueOf(22).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                    }
                } else if (descricao.equals("% do TOTAL DAS GARANTIAS sobre a RCL")) {
                    item.setExercicioAnterior(percentualExercicioAnterior);
                    item.setPrimeiroQuadrimestre(percentualPrimeiroQuadrimestre);
                    if (mesFinal.equals("08") || mesFinal.equals("12")) {
                        item.setSegundoQuadrimestre(percentualSegundoQuadrimestre);
                    } else {
                        item.setSegundoQuadrimestre(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        item.setTerceiroQuadrimestre(percentualTerceiroQuadrimestre);
                    } else {
                        item.setTerceiroQuadrimestre(BigDecimal.ZERO);
                    }
                } else if (descricao.equals("LIMITE DE ALERTA (inciso III do §1º do art. 59 da LRF) - 19.80%")) {
                    item.setDescricao(descricao);
                    item.setExercicioAnterior(BigDecimal.ZERO);
                    item.setPrimeiroQuadrimestre(BigDecimal.ZERO);
                    item.setSegundoQuadrimestre(BigDecimal.ZERO);
                    item.setTerceiroQuadrimestre(BigDecimal.ZERO);
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
                    item.setExercicioAnterior(calculaExercicioAnterior(itemDemonstrativo));
                    item.setPrimeiroQuadrimestre(calculaPrimeiroQuadrimestre(itemDemonstrativo));
                    if (mesFinal.equals("08") || mesFinal.equals("12")) {
                        item.setSegundoQuadrimestre(calculaSegundoQuadrimestre(itemDemonstrativo));
                    } else {
                        item.setSegundoQuadrimestre(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        item.setTerceiroQuadrimestre(calculaTerceiroQuadrimestre(itemDemonstrativo));
                    } else {
                        item.setTerceiroQuadrimestre(BigDecimal.ZERO);
                    }

                    if (descricao.equals("TOTAL GARANTIAS CONCEDIDAS (III) = (I + II)")) {
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

    public List<RGFAnexo03Item> geraDadosRelatorioAnexo3() {
        relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativo> itensDemonstrativos = itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaControlador.getExercicioCorrente(), "", "Anexo 3", TipoRelatorioItemDemonstrativo.RGF);
        itens = new ArrayList<ItemDemonstrativoComponente>();
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
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(it.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo03Item item = new RGFAnexo03Item();
                String descricao = StringUtil.preencheString("", it.getEspaco(), ' ') + it.getNome();
                item.setDescricao(descricao);
                if (descricao.equals("RECEITA CORRENTE LÍQUIDA - RCL (IV)")) {
                    item.setExercicioAnterior(calculaRclExercicioAnterior());
                    item.setPrimeiroQuadrimestre(calculaRclPrimeiroQuadrimestre());
                    if (mesFinal.equals("08") || mesFinal.equals("12")) {
                        item.setSegundoQuadrimestre(calculaRclSegundoQuadrimestre());
                    } else {
                        item.setSegundoQuadrimestre(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        item.setTerceiroQuadrimestre(calculaRclTerceiroQuadrimestre());
                    } else {
                        item.setTerceiroQuadrimestre(BigDecimal.ZERO);
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
                } else if (descricao.equals("LIMITE DEFINIDO POR RESOLUÇÃO DO SENADO FEDERAL - 22%")) {
                    item.setDescricao(descricao);
                    item.setExercicioAnterior(BigDecimal.ZERO);
                    item.setPrimeiroQuadrimestre(BigDecimal.ZERO);
                    item.setSegundoQuadrimestre(BigDecimal.ZERO);
                    item.setTerceiroQuadrimestre(BigDecimal.ZERO);
                    if (valorExercicioAnteriorRcl.compareTo(BigDecimal.ZERO) > 0) {
                        item.setExercicioAnterior(valorExercicioAnteriorRcl.multiply(BigDecimal.valueOf(22).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                    }
                    if (valorPrimeiroQuadrimestreRcl.compareTo(BigDecimal.ZERO) > 0) {
                        item.setPrimeiroQuadrimestre(valorPrimeiroQuadrimestreRcl.multiply(BigDecimal.valueOf(22).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                    }
                    if (valorSegundoQuadrimestreRcl.compareTo(BigDecimal.ZERO) > 0) {
                        item.setSegundoQuadrimestre(valorSegundoQuadrimestreRcl.multiply(BigDecimal.valueOf(22).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                    }
                    if (valorTerceiroQuadrimestreRcl.compareTo(BigDecimal.ZERO) > 0) {
                        item.setTerceiroQuadrimestre(valorTerceiroQuadrimestreRcl.multiply(BigDecimal.valueOf(22).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_EVEN)));
                    }
                } else if (descricao.equals("% do TOTAL DAS GARANTIAS sobre a RCL")) {
                    item.setExercicioAnterior(percentualExercicioAnterior);
                    item.setPrimeiroQuadrimestre(percentualPrimeiroQuadrimestre);
                    if (mesFinal.equals("08") || mesFinal.equals("12")) {
                        item.setSegundoQuadrimestre(percentualSegundoQuadrimestre);
                    } else {
                        item.setSegundoQuadrimestre(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        item.setTerceiroQuadrimestre(percentualTerceiroQuadrimestre);
                    } else {
                        item.setTerceiroQuadrimestre(BigDecimal.ZERO);
                    }
                } else if (descricao.equals("LIMITE DE ALERTA (inciso III do §1º do art. 59 da LRF) - 19.80%")) {
                    item.setDescricao(descricao);
                    item.setExercicioAnterior(BigDecimal.ZERO);
                    item.setPrimeiroQuadrimestre(BigDecimal.ZERO);
                    item.setSegundoQuadrimestre(BigDecimal.ZERO);
                    item.setTerceiroQuadrimestre(BigDecimal.ZERO);
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
                    item.setExercicioAnterior(calculaExercicioAnterior(itemDemonstrativo));
                    item.setPrimeiroQuadrimestre(calculaPrimeiroQuadrimestre(itemDemonstrativo));
                    if (mesFinal.equals("08") || mesFinal.equals("12")) {
                        item.setSegundoQuadrimestre(calculaSegundoQuadrimestre(itemDemonstrativo));
                    } else {
                        item.setSegundoQuadrimestre(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        item.setTerceiroQuadrimestre(calculaTerceiroQuadrimestre(itemDemonstrativo));
                    } else {
                        item.setTerceiroQuadrimestre(BigDecimal.ZERO);
                    }

                    if (descricao.equals("TOTAL GARANTIAS CONCEDIDAS (III) = (I + II)")) {
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

    public BigDecimal calculaRclPrimeiroQuadrimestre() {
        try {
            BigDecimal toReturn = BigDecimal.ZERO;
            RelatoriosItemDemonst relatorio = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo03Calculator2017.recuperarItemDemonstrativoPeloNomeERelatorio("RECEITA CORRENTE LÍQUIDA", sistemaControlador.getExercicioCorrente(), relatorio);
            GregorianCalendar dataCalendar = new GregorianCalendar(sistemaControlador.getExercicioCorrente().getAno(), new Integer("03"), new Integer(1));
            String data = formataDataMesAno(dataCalendar.getTime());
            for (int i = 1; i <= 12; i++) {
                toReturn = toReturn.add(relatorioRGFAnexo03Calculator2017.getRelatorioRREOAnexo03Calculator().recuperarValorPeloMesAnterior(itemDemonstrativo, data, relatorio, sistemaControlador.getExercicioCorrente()));
                data = alteraMes(data);
            }
            return toReturn;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal calculaRclSegundoQuadrimestre() {
        try {
            BigDecimal toReturn = BigDecimal.ZERO;
            RelatoriosItemDemonst relatorio = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo03Calculator2017.recuperarItemDemonstrativoPeloNomeERelatorio("RECEITA CORRENTE LÍQUIDA", sistemaControlador.getExercicioCorrente(), relatorio);
            GregorianCalendar dataCalendar = new GregorianCalendar(sistemaControlador.getExercicioCorrente().getAno(), new Integer("07"), new Integer(1));
            String data = formataDataMesAno(dataCalendar.getTime());
            for (int i = 1; i <= 12; i++) {
                toReturn = toReturn.add(relatorioRGFAnexo03Calculator2017.getRelatorioRREOAnexo03Calculator().recuperarValorPeloMesAnterior(itemDemonstrativo, data, relatorio, sistemaControlador.getExercicioCorrente()));
                data = alteraMes(data);
            }
            return toReturn;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal calculaRclTerceiroQuadrimestre() {
        try {
            BigDecimal toReturn = BigDecimal.ZERO;
            RelatoriosItemDemonst relatorio = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo03Calculator2017.recuperarItemDemonstrativoPeloNomeERelatorio("RECEITA CORRENTE LÍQUIDA", sistemaControlador.getExercicioCorrente(), relatorio);
            GregorianCalendar dataCalendar = new GregorianCalendar(sistemaControlador.getExercicioCorrente().getAno(), new Integer("11"), new Integer(1));
            String data = formataDataMesAno(dataCalendar.getTime());
            for (int i = 1; i <= 12; i++) {
                toReturn = toReturn.add(relatorioRGFAnexo03Calculator2017.getRelatorioRREOAnexo03Calculator().recuperarValorPeloMesAnterior(itemDemonstrativo, data, relatorio, sistemaControlador.getExercicioCorrente()));
                data = alteraMes(data);
            }
            return toReturn;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal calculaRclExercicioAnterior() {
        try {
            BigDecimal toReturn = BigDecimal.ZERO;
            RelatoriosItemDemonst relatorio = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo03Calculator2017.recuperarItemDemonstrativoPeloNomeERelatorio("RECEITA CORRENTE LÍQUIDA", sistemaControlador.getExercicioCorrente(), relatorio);
            GregorianCalendar dataCalendar = new GregorianCalendar(sistemaControlador.getExercicioCorrente().getAno() - 1, new Integer("11"), new Integer(1));
            String data = formataDataMesAno(dataCalendar.getTime());
            for (int i = 1; i <= 12; i++) {
                toReturn = toReturn.add(relatorioRGFAnexo03Calculator2017.getRelatorioRREOAnexo03Calculator().recuperarValorPeloMesAnterior(itemDemonstrativo, data, relatorio, sistemaControlador.getExercicioCorrente()));
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

    private String formataDataMesAno(Date data) {
        SimpleDateFormat formato = new SimpleDateFormat("MM/yyyy");
        return formato.format(data);
    }

    private List<RGFAnexo03Item> geraDadosRelatorioContragarantia() {
        List<RGFAnexo03Item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo03Item it = new RGFAnexo03Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setExercicioAnterior(calculaExercicioAnterior(itemDemonstrativo));
                it.setPrimeiroQuadrimestre(calculaPrimeiroQuadrimestre(itemDemonstrativo));
                if (mesFinal.equals("08") || mesFinal.equals("12")) {
                    it.setSegundoQuadrimestre(calculaSegundoQuadrimestre(itemDemonstrativo));
                } else {
                    it.setSegundoQuadrimestre(BigDecimal.ZERO);
                }
                if (mesFinal.equals("12")) {
                    it.setTerceiroQuadrimestre(calculaTerceiroQuadrimestre(itemDemonstrativo));
                } else {
                    it.setTerceiroQuadrimestre(BigDecimal.ZERO);
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    public void gerarRelatorio() {
        try {
            relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
            HashMap parameters = recuperarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(geraDados());
            gerarRelatorioComDadosEmCollection(getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarRelatorio() {
        try {
            relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
            HashMap parameters = recuperarParametros();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(geraDados());
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(sistemaControlador.getExercicioCorrente());
            anexo.setNome("Anexo 3 - Demonstrativo das Garantias e Contragarantias de Valores");
            anexo.setMes(Mes.getMesToInt(Integer.valueOf(mesFinal)));
            anexo.setTipo(PortalTransparenciaTipo.RGF);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "RelatorioAnexo03RGFGarantias_2017.jasper";
    }

    private HashMap recuperarParametros() {
        HashMap parameters = new HashMap();
        parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getLogin());
        }
        parameters.put("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno());
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("CONTRAGARANTIAS", geraDadosRelatorioContragarantia());
        parameters.put("GARANTIAS", geraDadosRelatorio());
        parameters.put("MEDIDACORRETIVA", "");
        if (mesFinal.equals("04")) {
            parameters.put("DATAINICIAL", "JANEIRO");
            parameters.put("DATAFINAL", " ABRIL DE " + sistemaControlador.getExercicioCorrente().getAno());
        } else if (mesFinal.equals("08")) {
            parameters.put("DATAINICIAL", "MAIO");
            parameters.put("DATAFINAL", " AGOSTO DE " + sistemaControlador.getExercicioCorrente().getAno());
        } else if (mesFinal.equals("12")) {
            parameters.put("DATAINICIAL", "SETEMBRO");
            parameters.put("DATAFINAL", "DEZEMBRO DE " + sistemaControlador.getExercicioCorrente().getAno());
        }
        return parameters;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    private BigDecimal calculaPrimeiroQuadrimestre(ItemDemonstrativo it) {
        return relatorioRGFAnexo03Calculator2017.calcularQuadrimestre(it, sistemaControlador.getExercicioCorrente(), "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), "30/04/" + sistemaControlador.getExercicioCorrente().getAno(), relatoriosItemDemonst);
    }

    private BigDecimal calculaSegundoQuadrimestre(ItemDemonstrativo it) {
        return relatorioRGFAnexo03Calculator2017.calcularQuadrimestre(it, sistemaControlador.getExercicioCorrente(), "01/05/" + sistemaControlador.getExercicioCorrente().getAno(), "31/08/" + sistemaControlador.getExercicioCorrente().getAno(), relatoriosItemDemonst);
    }

    private BigDecimal calculaTerceiroQuadrimestre(ItemDemonstrativo it) {
        return relatorioRGFAnexo03Calculator2017.calcularQuadrimestre(it, sistemaControlador.getExercicioCorrente(), "01/09/" + sistemaControlador.getExercicioCorrente().getAno(), "31/12/" + sistemaControlador.getExercicioCorrente().getAno(), relatoriosItemDemonst);
    }

    private BigDecimal calculaExercicioAnterior(ItemDemonstrativo it) {
        Exercicio ex = relatorioRGFAnexo03Calculator2017.getExercicioFacade().getExercicioPorAno((sistemaControlador.getExercicioCorrente().getAno() - 1));
        return relatorioRGFAnexo03Calculator2017.calcularQuadrimestre(it, ex, "01/01/" + ex.getAno(), "31/12/" + ex.getAno(), relatoriosItemDemonst);
    }

    public RelatorioRGFAnexo03Calculator2017 getRelatorioRGFAnexo03Calculator2017() {
        return relatorioRGFAnexo03Calculator2017;
    }

    public void setRelatorioRGFAnexo03Calculator2017(RelatorioRGFAnexo03Calculator2017 relatorioRGFAnexo03Calculator2017) {
        this.relatorioRGFAnexo03Calculator2017 = relatorioRGFAnexo03Calculator2017;
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

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public ItemDemonstrativoFacade getItemDemonstrativoFacade() {
        return itemDemonstrativoFacade;
    }

    public void setItemDemonstrativoFacade(ItemDemonstrativoFacade itemDemonstrativoFacade) {
        this.itemDemonstrativoFacade = itemDemonstrativoFacade;
    }
}
