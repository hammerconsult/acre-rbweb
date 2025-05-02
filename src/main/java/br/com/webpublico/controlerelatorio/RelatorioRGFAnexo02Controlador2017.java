package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.RGFAnexo02Item;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.relatoriofacade.ItemDemonstrativoCalculator;
import br.com.webpublico.relatoriofacade.RelatorioRREOAnexo03Calculator;
import br.com.webpublico.relatoriofacade.RelatorioRREOAnexo05Calculator;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
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
 * Created by Mateus on 28/08/2014.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo2-2017", pattern = "/relatorio/rgf/anexo2/2017", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo02-2017.xhtml")
})
public class RelatorioRGFAnexo02Controlador2017 extends AbstractReport implements Serializable {
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private RelatorioRREOAnexo05Calculator relatorioRREOAnexo05Calculator;
    @EJB
    private ItemDemonstrativoCalculator itemDemonstrativoCalculator;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @EJB
    private RelatorioRREOAnexo03Calculator relatorioRREOAnexo03Calculator;
    private String mesInicial;
    private String mesFinal;
    private List<ItemDemonstrativoComponente> itens;
    private RelatoriosItemDemonst relatoriosItemDemonst;

    public RelatorioRGFAnexo02Controlador2017() {
        itens = new ArrayList<ItemDemonstrativoComponente>();
    }

    @URLAction(mappingId = "relatorio-rgf-anexo2-2017", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        mesInicial = "";
        mesFinal = "";
    }

    private List<RGFAnexo02Item> geraDados() {
        List<RGFAnexo02Item> lista = new ArrayList<>();
        RGFAnexo02Item it = new RGFAnexo02Item();
        it.setDescricao("");
        lista.add(it);
        return lista;
    }

    private List<RGFAnexo02Item> geraDadosGrupo1() {
        BigDecimal rclAnterior = BigDecimal.ZERO;
        BigDecimal rclPrimeiroQuadrimestre = BigDecimal.ZERO;
        BigDecimal rclSegundoQuadrimestre = BigDecimal.ZERO;
        BigDecimal rclTerceiroQuadrimestre = BigDecimal.ZERO;
        List<RGFAnexo02Item> lista = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo02Item it = new RGFAnexo02Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (it.getDescricao().equals("RECEITA CORRENTE LÍQUIDA - RCL")) {
                    rclAnterior = calculaRclExAnterior();
                    rclPrimeiroQuadrimestre = calculaRcl("04");
                    it.setSaldoAnterior(rclAnterior);
                    it.setPrimeiroQuadrimestre(rclPrimeiroQuadrimestre);
                    if (mesFinal.equals("08") || mesFinal.equals("12")) {
                        rclSegundoQuadrimestre = calculaRcl("08");
                        it.setSegundoQuadrimestre(rclSegundoQuadrimestre);
                    } else {
                        it.setSegundoQuadrimestre(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        rclTerceiroQuadrimestre = calculaRcl("12");
                        it.setTerceiroQuadrimestre(rclTerceiroQuadrimestre);
                    } else {
                        it.setTerceiroQuadrimestre(BigDecimal.ZERO);
                    }
                } else if (it.getDescricao().equals("% da DC sobre a RCL (I / RCL)") || it.getDescricao().equals("% da DCL sobre a RCL (III / RCL)")) {
                    it.setSaldoAnterior(recuperaValorExercicioAnterior(itemDemonstrativo, relatoriosItemDemonst).divide(rclAnterior, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
                    it.setPrimeiroQuadrimestre(recuperaValorPrimeiroQuadrimestre(itemDemonstrativo, relatoriosItemDemonst).divide(rclPrimeiroQuadrimestre, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
                    if (mesFinal.equals("08") || mesFinal.equals("12")) {
                        it.setSegundoQuadrimestre(recuperaValorSegundoQuadrimestre(itemDemonstrativo, relatoriosItemDemonst).divide(rclSegundoQuadrimestre, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
                    } else {
                        it.setSegundoQuadrimestre(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        it.setTerceiroQuadrimestre(recuperaValorTerceiroQuadrimestre(itemDemonstrativo, relatoriosItemDemonst).divide(rclTerceiroQuadrimestre, 6, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)));
                    } else {
                        it.setTerceiroQuadrimestre(BigDecimal.ZERO);
                    }
                } else if (it.getDescricao().equals("LIMITE DEFINIDO POR RESOLUÇÃO DO SENADO FEDERAL - 120%")) {
                    it.setSaldoAnterior(rclAnterior.add(rclAnterior.multiply(BigDecimal.valueOf(0.20))));
                    it.setPrimeiroQuadrimestre(rclPrimeiroQuadrimestre.add(rclPrimeiroQuadrimestre.multiply(BigDecimal.valueOf(0.20))));
                    if (mesFinal.equals("08") || mesFinal.equals("12")) {
                        it.setSegundoQuadrimestre(rclSegundoQuadrimestre.add(rclSegundoQuadrimestre.multiply(BigDecimal.valueOf(0.20))));
                    } else {
                        it.setSegundoQuadrimestre(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        it.setTerceiroQuadrimestre(rclTerceiroQuadrimestre.add(rclTerceiroQuadrimestre.multiply(BigDecimal.valueOf(0.20))));
                    } else {
                        it.setTerceiroQuadrimestre(BigDecimal.ZERO);
                    }
                } else if (it.getDescricao().equals("LIMITE DE ALERTA (§ 1º do art. 59 da LRF) - 108%")) {
                    it.setSaldoAnterior(rclAnterior.add(rclAnterior.multiply(BigDecimal.valueOf(0.08))));
                    it.setPrimeiroQuadrimestre(rclPrimeiroQuadrimestre.add(rclPrimeiroQuadrimestre.multiply(BigDecimal.valueOf(0.08))));
                    if (mesFinal.equals("08") || mesFinal.equals("12")) {
                        it.setSegundoQuadrimestre(rclSegundoQuadrimestre.add(rclSegundoQuadrimestre.multiply(BigDecimal.valueOf(0.08))));
                    } else {
                        it.setSegundoQuadrimestre(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        it.setTerceiroQuadrimestre(rclTerceiroQuadrimestre.add(rclTerceiroQuadrimestre.multiply(BigDecimal.valueOf(0.08))));
                    } else {
                        it.setTerceiroQuadrimestre(BigDecimal.ZERO);
                    }
                } else {
                    it.setSaldoAnterior(recuperaValorExercicioAnterior(itemDemonstrativo, relatoriosItemDemonst));
                    it.setPrimeiroQuadrimestre(recuperaValorPrimeiroQuadrimestre(itemDemonstrativo, relatoriosItemDemonst));
                    if (mesFinal.equals("08") || mesFinal.equals("12")) {
                        it.setSegundoQuadrimestre(recuperaValorSegundoQuadrimestre(itemDemonstrativo, relatoriosItemDemonst));
                    } else {
                        it.setSegundoQuadrimestre(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        it.setTerceiroQuadrimestre(recuperaValorTerceiroQuadrimestre(itemDemonstrativo, relatoriosItemDemonst));
                    } else {
                        it.setTerceiroQuadrimestre(BigDecimal.ZERO);
                    }
                }
                lista.add(it);
            }
        }
        return lista;
    }

    public List<RGFAnexo02Item> buscaDadosGrupo1() {
        relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 2", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativo> itensDemonstrativos = itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaControlador.getExercicioCorrente(), "", "Anexo 2", TipoRelatorioItemDemonstrativo.RGF);
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
        BigDecimal rclAnterior = BigDecimal.ZERO;
        BigDecimal rclPrimeiroQuadrimestre = BigDecimal.ZERO;
        BigDecimal rclSegundoQuadrimestre = BigDecimal.ZERO;
        BigDecimal rclTerceiroQuadrimestre = BigDecimal.ZERO;
        List<RGFAnexo02Item> lista = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo02Item it = new RGFAnexo02Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                if (it.getDescricao().equals("RECEITA CORRENTE LÍQUIDA - RCL")) {
                    rclAnterior = calculaRclExAnterior();
                    rclPrimeiroQuadrimestre = calculaRcl("04");
                    it.setSaldoAnterior(rclAnterior);
                    it.setPrimeiroQuadrimestre(rclPrimeiroQuadrimestre);
                    if (mesFinal.equals("08") || mesFinal.equals("12")) {
                        rclSegundoQuadrimestre = calculaRcl("08");
                        it.setSegundoQuadrimestre(rclSegundoQuadrimestre);
                    } else {
                        it.setSegundoQuadrimestre(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        rclTerceiroQuadrimestre = calculaRcl("12");
                        it.setTerceiroQuadrimestre(rclTerceiroQuadrimestre);
                    } else {
                        it.setTerceiroQuadrimestre(BigDecimal.ZERO);
                    }
                } else if (it.getDescricao().equals("% da DC sobre a RCL (I / RCL)") || it.getDescricao().equals("% da DCL sobre a RCL (III / RCL)")) {
                    it.setSaldoAnterior(recuperaValorExercicioAnterior(itemDemonstrativo, relatoriosItemDemonst).divide(rclAnterior, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                    it.setPrimeiroQuadrimestre(recuperaValorPrimeiroQuadrimestre(itemDemonstrativo, relatoriosItemDemonst).divide(rclPrimeiroQuadrimestre, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                    if (mesFinal.equals("08") || mesFinal.equals("12")) {
                        it.setSegundoQuadrimestre(recuperaValorSegundoQuadrimestre(itemDemonstrativo, relatoriosItemDemonst).divide(rclSegundoQuadrimestre, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                    } else {
                        it.setSegundoQuadrimestre(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        it.setTerceiroQuadrimestre(recuperaValorTerceiroQuadrimestre(itemDemonstrativo, relatoriosItemDemonst).divide(rclTerceiroQuadrimestre, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100)));
                    } else {
                        it.setTerceiroQuadrimestre(BigDecimal.ZERO);
                    }
                } else if (it.getDescricao().equals("LIMITE DEFINIDO POR RESOLUÇÃO DO SENADO FEDERAL - 120%")) {
                    it.setSaldoAnterior(rclAnterior.add(rclAnterior.multiply(BigDecimal.valueOf(0.20))));
                    it.setPrimeiroQuadrimestre(rclPrimeiroQuadrimestre.add(rclPrimeiroQuadrimestre.multiply(BigDecimal.valueOf(0.20))));
                    if (mesFinal.equals("08") || mesFinal.equals("12")) {
                        it.setSegundoQuadrimestre(rclSegundoQuadrimestre.add(rclSegundoQuadrimestre.multiply(BigDecimal.valueOf(0.20))));
                    } else {
                        it.setSegundoQuadrimestre(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        it.setTerceiroQuadrimestre(rclTerceiroQuadrimestre.add(rclTerceiroQuadrimestre.multiply(BigDecimal.valueOf(0.20))));
                    } else {
                        it.setTerceiroQuadrimestre(BigDecimal.ZERO);
                    }
                } else if (it.getDescricao().equals("LIMITE DE ALERTA (§ 1º do art. 59 da LRF) - 108%")) {
                    it.setSaldoAnterior(rclAnterior.add(rclAnterior.multiply(BigDecimal.valueOf(0.08))));
                    it.setPrimeiroQuadrimestre(rclPrimeiroQuadrimestre.add(rclPrimeiroQuadrimestre.multiply(BigDecimal.valueOf(0.08))));
                    if (mesFinal.equals("08") || mesFinal.equals("12")) {
                        it.setSegundoQuadrimestre(rclSegundoQuadrimestre.add(rclSegundoQuadrimestre.multiply(BigDecimal.valueOf(0.08))));
                    } else {
                        it.setSegundoQuadrimestre(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        it.setTerceiroQuadrimestre(rclTerceiroQuadrimestre.add(rclTerceiroQuadrimestre.multiply(BigDecimal.valueOf(0.08))));
                    } else {
                        it.setTerceiroQuadrimestre(BigDecimal.ZERO);
                    }
                } else {
                    it.setSaldoAnterior(recuperaValorExercicioAnterior(itemDemonstrativo, relatoriosItemDemonst));
                    it.setPrimeiroQuadrimestre(recuperaValorPrimeiroQuadrimestre(itemDemonstrativo, relatoriosItemDemonst));
                    if (mesFinal.equals("08") || mesFinal.equals("12")) {
                        it.setSegundoQuadrimestre(recuperaValorSegundoQuadrimestre(itemDemonstrativo, relatoriosItemDemonst));
                    } else {
                        it.setSegundoQuadrimestre(BigDecimal.ZERO);
                    }
                    if (mesFinal.equals("12")) {
                        it.setTerceiroQuadrimestre(recuperaValorTerceiroQuadrimestre(itemDemonstrativo, relatoriosItemDemonst));
                    } else {
                        it.setTerceiroQuadrimestre(BigDecimal.ZERO);
                    }
                }
                lista.add(it);
            }
        }
        return lista;
    }

    private List<RGFAnexo02Item> geraDadosGrupo2() {
        List<RGFAnexo02Item> lista = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo02Item it = new RGFAnexo02Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setSaldoAnterior(recuperaValorExercicioAnterior(itemDemonstrativo, relatoriosItemDemonst));
                it.setPrimeiroQuadrimestre(recuperaValorPrimeiroQuadrimestre(itemDemonstrativo, relatoriosItemDemonst));
                if (mesFinal.equals("08") || mesFinal.equals("12")) {
                    it.setSegundoQuadrimestre(recuperaValorSegundoQuadrimestre(itemDemonstrativo, relatoriosItemDemonst));
                } else {
                    it.setSegundoQuadrimestre(BigDecimal.ZERO);
                }
                if (mesFinal.equals("12")) {
                    it.setTerceiroQuadrimestre(recuperaValorTerceiroQuadrimestre(itemDemonstrativo, relatoriosItemDemonst));
                } else {
                    it.setTerceiroQuadrimestre(BigDecimal.ZERO);
                }
                lista.add(it);
            }
        }
        return lista;
    }

    private List<RGFAnexo02Item> geraDadosGrupo3() {
        List<RGFAnexo02Item> lista = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 3);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo02Item it = new RGFAnexo02Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setSaldoAnterior(recuperaValorExercicioAnterior(itemDemonstrativo, relatoriosItemDemonst));
                it.setPrimeiroQuadrimestre(recuperaValorPrimeiroQuadrimestre(itemDemonstrativo, relatoriosItemDemonst));
                if (mesFinal.equals("08") || mesFinal.equals("12")) {
                    it.setSegundoQuadrimestre(recuperaValorSegundoQuadrimestre(itemDemonstrativo, relatoriosItemDemonst));
                } else {
                    it.setSegundoQuadrimestre(BigDecimal.ZERO);
                }
                if (mesFinal.equals("12")) {
                    it.setTerceiroQuadrimestre(recuperaValorTerceiroQuadrimestre(itemDemonstrativo, relatoriosItemDemonst));
                } else {
                    it.setTerceiroQuadrimestre(BigDecimal.ZERO);
                }
                lista.add(it);
            }
        }
        return lista;
    }

    private List<RGFAnexo02Item> geraDadosGrupo4() {
        List<RGFAnexo02Item> lista = new ArrayList<>();
        for (ItemDemonstrativoComponente item : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(item.getItemDemonstrativo(), 4);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo02Item it = new RGFAnexo02Item();
                String descricao = StringUtil.preencheString("", item.getEspaco(), ' ') + item.getNome();
                it.setDescricao(descricao);
                it.setSaldoAnterior(recuperaValorExercicioAnterior(itemDemonstrativo, relatoriosItemDemonst));
                it.setPrimeiroQuadrimestre(recuperaValorPrimeiroQuadrimestre(itemDemonstrativo, relatoriosItemDemonst));
                if (mesFinal.equals("08") || mesFinal.equals("12")) {
                    it.setSegundoQuadrimestre(recuperaValorSegundoQuadrimestre(itemDemonstrativo, relatoriosItemDemonst));
                } else {
                    it.setSegundoQuadrimestre(BigDecimal.ZERO);
                }
                if (mesFinal.equals("12")) {
                    it.setTerceiroQuadrimestre(recuperaValorTerceiroQuadrimestre(itemDemonstrativo, relatoriosItemDemonst));
                } else {
                    it.setTerceiroQuadrimestre(BigDecimal.ZERO);
                }
                lista.add(it);
            }
        }
        return lista;
    }

    public void gerarRelatorio() {
        try {
            relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 2", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(geraDados());
            HashMap parameters = montarParametros();
            gerarRelatorioComDadosEmCollection(getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarRelatorio() {
        try {
            relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 2", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(geraDados());
            HashMap parameters = montarParametros();
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(sistemaControlador.getExercicioCorrente());
            anexo.setNome("Anexo 2 - Demonstrativo da Dívida Consolidada Líquida");
            anexo.setMes(Mes.getMesToInt(Integer.valueOf(mesFinal)));
            anexo.setTipo(PortalTransparenciaTipo.RGF);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "RelatorioRGFAnexo02_2017.jasper";
    }

    private HashMap montarParametros() {
        HashMap parameters = new HashMap();
        parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno());
        parameters.put("GRUPO1", geraDadosGrupo1());
        parameters.put("GRUPO2", geraDadosGrupo2());
        parameters.put("GRUPO3", geraDadosGrupo3());
        parameters.put("GRUPO4", geraDadosGrupo4());
        parameters.put("SUBREPORT_DIR", getCaminho());
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getLogin());
        }
        parameters.put("DATAINICIAL", retornaDescricaoMes(mesInicial));
        parameters.put("DATAFINAL", retornaDescricaoMes(mesFinal) + " DE " + sistemaControlador.getExercicioCorrente().getAno());
        return parameters;
    }

    public BigDecimal calculaRcl(String mes) {
        try {
            BigDecimal toReturn = BigDecimal.ZERO;
            RelatoriosItemDemonst relatorio = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoCalculator.recuperarItemDemonstrativoPeloNomeERelatorio("RECEITA CORRENTE LÍQUIDA", sistemaControlador.getExercicioCorrente(), relatorio);
            GregorianCalendar dataCalendar = new GregorianCalendar(sistemaControlador.getExercicioCorrente().getAno(), new Integer(mes) - 1, new Integer(1));
            String data = formataDataMesAno(dataCalendar.getTime());
            for (int i = 1; i <= 12; i++) {
                toReturn = toReturn.add(relatorioRREOAnexo03Calculator.recuperarValorPeloMesAnterior(itemDemonstrativo, data, relatorio, sistemaControlador.getExercicioCorrente()));
                data = alteraMes(data);
            }
            return toReturn;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal calculaRclExAnterior() {
        try {
            BigDecimal toReturn = BigDecimal.ZERO;
            RelatoriosItemDemonst relatorio = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoCalculator.recuperarItemDemonstrativoPeloNomeERelatorio("RECEITA CORRENTE LÍQUIDA", sistemaControlador.getExercicioCorrente(), relatorio);
            GregorianCalendar dataCalendar = new GregorianCalendar((sistemaControlador.getExercicioCorrente().getAno() - 1), new Integer("11"), new Integer(31));
            String data = formataDataMesAno(dataCalendar.getTime());
            for (int i = 1; i <= 12; i++) {
                toReturn = toReturn.add(relatorioRREOAnexo03Calculator.recuperarValorPeloMesAnterior(itemDemonstrativo, data, relatorio, sistemaControlador.getExercicioCorrente()));
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

    private String retornaDescricaoMes(String mes) {
        String toReturn = "";
        switch (mes) {
            case "01":
                toReturn = "JANEIRO";
                break;
            case "02":
                toReturn = "FEVEREIRO";
                break;
            case "03":
                toReturn = "MARÇO";
                break;
            case "04":
                toReturn = "ABRIL";
                break;
            case "05":
                toReturn = "MAIO";
                break;
            case "06":
                toReturn = "JUNHO";
                break;
            case "07":
                toReturn = "JULHO";
                break;
            case "08":
                toReturn = "AGOSTO";
                break;
            case "09":
                toReturn = "SETEMBRO";
                break;
            case "10":
                toReturn = "OUTUBRO";
                break;
            case "11":
                toReturn = "NOVEMBRO";
                break;
            case "12":
                toReturn = "DEZEMBRO";
                break;
        }
        return toReturn;
    }

    private BigDecimal recuperaValorPrimeiroQuadrimestre(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo05Calculator.calcular(itDemonstrativo, "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), Util.getDiasMes(new Integer("04"), sistemaControlador.getExercicioCorrente().getAno()) + "/04/" + sistemaControlador.getExercicioCorrente().getAno(), sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst);
    }

    private BigDecimal recuperaValorSegundoQuadrimestre(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo05Calculator.calcular(itDemonstrativo, "01/05/" + sistemaControlador.getExercicioCorrente().getAno(), Util.getDiasMes(new Integer("08"), sistemaControlador.getExercicioCorrente().getAno()) + "/08/" + sistemaControlador.getExercicioCorrente().getAno(), sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst);
    }

    private BigDecimal recuperaValorTerceiroQuadrimestre(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo05Calculator.calcular(itDemonstrativo, "01/09/" + sistemaControlador.getExercicioCorrente().getAno(), Util.getDiasMes(new Integer("12"), sistemaControlador.getExercicioCorrente().getAno()) + "/12/" + sistemaControlador.getExercicioCorrente().getAno(), sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst);
    }

    private BigDecimal recuperaValorExercicioAnterior(ItemDemonstrativo itDemonstrativo, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRREOAnexo05Calculator.calcularExAnteriorAnexo2(itDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst);
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
        Integer mes = Integer.parseInt(this.mesFinal);
        if (mes == 4) {
            this.mesInicial = "01";
        } else {
            this.mesInicial = "0" + (mes - 3);
        }
    }

    public String getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
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

    public RelatorioRREOAnexo05Calculator getRelatorioRREOAnexo05Calculator() {
        return relatorioRREOAnexo05Calculator;
    }

    public void setRelatorioRREOAnexo05Calculator(RelatorioRREOAnexo05Calculator relatorioRREOAnexo05Calculator) {
        this.relatorioRREOAnexo05Calculator = relatorioRREOAnexo05Calculator;
    }

    public ItemDemonstrativoCalculator getItemDemonstrativoCalculator() {
        return itemDemonstrativoCalculator;
    }

    public void setItemDemonstrativoCalculator(ItemDemonstrativoCalculator itemDemonstrativoCalculator) {
        this.itemDemonstrativoCalculator = itemDemonstrativoCalculator;
    }

    public ItemDemonstrativoFacade getItemDemonstrativoFacade() {
        return itemDemonstrativoFacade;
    }

    public void setItemDemonstrativoFacade(ItemDemonstrativoFacade itemDemonstrativoFacade) {
        this.itemDemonstrativoFacade = itemDemonstrativoFacade;
    }

    public RelatorioRREOAnexo03Calculator getRelatorioRREOAnexo03Calculator() {
        return relatorioRREOAnexo03Calculator;
    }

    public void setRelatorioRREOAnexo03Calculator(RelatorioRREOAnexo03Calculator relatorioRREOAnexo03Calculator) {
        this.relatorioRREOAnexo03Calculator = relatorioRREOAnexo03Calculator;
    }
}
