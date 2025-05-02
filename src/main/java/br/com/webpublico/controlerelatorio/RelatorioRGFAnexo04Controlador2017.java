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
import br.com.webpublico.entidadesauxiliares.RGFAnexo04ItemApuracao;
import br.com.webpublico.entidadesauxiliares.RGFAnexo04item;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo04Calculator2017;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo4-2017", pattern = "/relatorio/rgf/anexo4/2017", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo04-2017.xhtml")})
public class RelatorioRGFAnexo04Controlador2017 extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private RelatorioRGFAnexo04Calculator2017 relatorioRGFAnexo04Calculator2017;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    private Integer quadrimestre;
    private String dataInicial;
    private String dataFinal;
    private String mesFinal;
    private BigDecimal totalAteQuadrimestreSujeita;
    private BigDecimal totalAteQuadrimestreNaoSujeita;
    private BigDecimal totalCorrenteLiquida;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private List<ItemDemonstrativoComponente> itens;

    public RelatorioRGFAnexo04Controlador2017() {
        itens = new ArrayList<ItemDemonstrativoComponente>();
    }

    private List<RGFAnexo04item> prepararDados() throws ParseException {
        List<RGFAnexo04item> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente it : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(it.getItemDemonstrativo(), 1);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo04item item = new RGFAnexo04item();
                String descricao = StringUtil.preencheString("", it.getEspaco(), ' ') + it.getNome();
                item.setDescricao(descricao);
                item.setValorNoQuadrimestre(calcularValorNoQuadrimestre(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                item.setValorAteQuadrimestre(calcularValorAteQuadrimestre(itemDemonstrativo, sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst));
                if (item.getDescricao().trim().equals("SUJEITAS AO LIMITE PARA FINS DE CONTRATAÇÃO (I)")) {
                    totalAteQuadrimestreSujeita = item.getValorAteQuadrimestre();
                }
                if (item.getDescricao().equals("NÃO SUJEITAS AO LIMITE PARA FINS DE CONTRATAÇÃO (II)")) {
                    totalAteQuadrimestreNaoSujeita = item.getValorAteQuadrimestre();
                }
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    private List<RGFAnexo04ItemApuracao> preparaDadosApuracao() {
        List<RGFAnexo04ItemApuracao> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente it : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(it.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo04ItemApuracao item = new RGFAnexo04ItemApuracao();
                String descricao = StringUtil.preencheString("", it.getEspaco(), ' ') + it.getNome();
                item.setDescricao(descricao);
                switch (descricao) {
                    case "RECEITA CORRENTE LÍQUIDA – RCL":
                        item.setValor(calculaValorLiquidaMes(quadrimestre == 1 ? "03" : quadrimestre == 2 ? "07" : "11"));
                        totalCorrenteLiquida = item.getValor();
                        item.setPercentual(BigDecimal.ZERO);
                        break;
                    case "OPERAÇÕES VEDADAS":
                        item.setValor(BigDecimal.ZERO);
                        item.setPercentual(BigDecimal.ZERO);
                        break;
                    case "   Do Período de Referência (III)":
                        item.setValor(BigDecimal.ZERO);
                        item.setPercentual(BigDecimal.ZERO);
                        break;
                    case "   De Períodos Anteriores ao de Referência":
                        item.setValor(BigDecimal.ZERO);
                        item.setPercentual(BigDecimal.ZERO);
                        break;
                    case "TOTAL CONSIDERADO PARA FINS DA APURAÇÃO DO CUMPRIMENTO DO LIMITE (IV)= (Ia + III)":
                        item.setValor(totalAteQuadrimestreSujeita);
                        if (totalCorrenteLiquida != BigDecimal.ZERO) {
                            item.setPercentual((item.getValor().divide(totalCorrenteLiquida, 6, BigDecimal.ROUND_HALF_EVEN)));
                        } else {
                            item.setPercentual(BigDecimal.ZERO);
                        }
                        break;
                    case "LIMITE GERAL DEFINIDO POR RESOLUÇÃO DO SENADO FEDERAL PARA AS OPERAÇÕES DE CRÉDITO INTERNAS E EXTERNAS":
                        if (totalCorrenteLiquida != BigDecimal.ZERO) {
                            item.setValor(totalCorrenteLiquida.multiply(BigDecimal.valueOf(16).divide(BigDecimal.valueOf(100))));
                        } else {
                            item.setValor(BigDecimal.ZERO);
                        }
                        item.setPercentual(BigDecimal.valueOf(16).divide(BigDecimal.valueOf(100), 6, BigDecimal.ROUND_HALF_EVEN));
                        break;
                    case "OPERAÇÕES DE CRÉDITO POR ANTECIPAÇÃO DA RECEITA ORÇAMENTÁRIA":
                        item.setValor(BigDecimal.ZERO);
                        item.setPercentual(BigDecimal.ZERO);
                        break;
                    case "LIMITE DEFINIDO POR RESOLUÇÃO DO SENADO FEDERAL PARA AS OPERAÇÕES DE CRÉDITO POR ANTECIPAÇÃO DA RECEITA ORÇAMENTÁRIA":
                        if (totalCorrenteLiquida != BigDecimal.ZERO) {
                            item.setValor(totalCorrenteLiquida.multiply(BigDecimal.valueOf(7).divide(BigDecimal.valueOf(100))));
                        } else {
                            item.setValor(BigDecimal.ZERO);
                        }
                        item.setPercentual(BigDecimal.valueOf(7).divide(BigDecimal.valueOf(100)));
                        break;
                    case "TOTAL CONSIDERADO PARA CONTRATAÇÃO DE NOVAS OPERAÇÕES DE CRÉDITO (V) = (IV + IIa)":
                        item.setValor(totalAteQuadrimestreSujeita.add(totalAteQuadrimestreNaoSujeita));
                        if (totalCorrenteLiquida != BigDecimal.ZERO) {
                            item.setPercentual(item.getValor().divide(totalCorrenteLiquida, 6, BigDecimal.ROUND_HALF_EVEN));
                        } else {
                            item.setPercentual(BigDecimal.ZERO);
                        }
                        break;
                    case "LIMITE DE ALERTA (inciso III do §1º do art. 59 da LRF) - 14,40 %":
                        if (totalCorrenteLiquida != BigDecimal.ZERO) {
                            item.setValor(totalCorrenteLiquida.multiply(BigDecimal.valueOf(14.4).divide(BigDecimal.valueOf(100))));
                        } else {
                            item.setValor(BigDecimal.ZERO);
                        }
                        item.setPercentual(BigDecimal.valueOf(14.4).divide(BigDecimal.valueOf(100)));
                        break;
                }
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    public List<RGFAnexo04ItemApuracao> preparaDadosAnexo4() throws ParseException {
        relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativo> itensDemonstrativos = itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaControlador.getExercicioCorrente(), "", "Anexo 4", TipoRelatorioItemDemonstrativo.RGF);
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
        totalAteQuadrimestreSujeita = BigDecimal.ZERO;
        totalAteQuadrimestreNaoSujeita = BigDecimal.ZERO;
        totalCorrenteLiquida = BigDecimal.ZERO;
        prepararDados();
        List<RGFAnexo04ItemApuracao> toReturn = new ArrayList<>();
        for (ItemDemonstrativoComponente it : itens) {
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoFacade.recuperaPorGrupo(it.getItemDemonstrativo(), 2);
            if (itemDemonstrativo.getId() != null) {
                RGFAnexo04ItemApuracao item = new RGFAnexo04ItemApuracao();
                String descricao = StringUtil.preencheString("", it.getEspaco(), ' ') + it.getNome();
                item.setDescricao(descricao);
                switch (descricao) {
                    case "RECEITA CORRENTE LÍQUIDA – RCL":
                        item.setValor(calculaValorLiquidaMes(quadrimestre == 1 ? "03" : quadrimestre == 2 ? "07" : "11"));
                        totalCorrenteLiquida = item.getValor();
                        item.setPercentual(BigDecimal.ZERO);
                        break;
                    case "OPERAÇÕES VEDADAS":
                        item.setValor(BigDecimal.ZERO);
                        item.setPercentual(BigDecimal.ZERO);
                        break;
                    case "   Do Período de Referência (III)":
                        item.setValor(BigDecimal.ZERO);
                        item.setPercentual(BigDecimal.ZERO);
                        break;
                    case "   De Períodos Anteriores ao de Referência":
                        item.setValor(BigDecimal.ZERO);
                        item.setPercentual(BigDecimal.ZERO);
                        break;
                    case "TOTAL CONSIDERADO PARA FINS DA APURAÇÃO DO CUMPRIMENTO DO LIMITE (IV)= (Ia + III)":
                        item.setValor(totalAteQuadrimestreSujeita);
                        if (totalCorrenteLiquida != BigDecimal.ZERO) {
                            item.setPercentual((item.getValor().divide(totalCorrenteLiquida, 6, BigDecimal.ROUND_HALF_EVEN)));
                        } else {
                            item.setPercentual(BigDecimal.ZERO);
                        }
                        break;
                    case "LIMITE GERAL DEFINIDO POR RESOLUÇÃO DO SENADO FEDERAL PARA AS OPERAÇÕES DE CRÉDITO INTERNAS E EXTERNAS":
                        if (totalCorrenteLiquida != BigDecimal.ZERO) {
                            item.setValor(totalCorrenteLiquida.multiply(BigDecimal.valueOf(16).divide(BigDecimal.valueOf(100))));
                        } else {
                            item.setValor(BigDecimal.ZERO);
                        }
                        item.setPercentual(BigDecimal.valueOf(16).divide(BigDecimal.valueOf(100), 6, BigDecimal.ROUND_HALF_EVEN));
                        break;
                    case "OPERAÇÕES DE CRÉDITO POR ANTECIPAÇÃO DA RECEITA ORÇAMENTÁRIA":
                        item.setValor(BigDecimal.ZERO);
                        item.setPercentual(BigDecimal.ZERO);
                        break;
                    case "LIMITE DEFINIDO POR RESOLUÇÃO DO SENADO FEDERAL PARA AS OPERAÇÕES DE CRÉDITO POR ANTECIPAÇÃO DA RECEITA ORÇAMENTÁRIA":
                        if (totalCorrenteLiquida != BigDecimal.ZERO) {
                            item.setValor(totalCorrenteLiquida.multiply(BigDecimal.valueOf(7).divide(BigDecimal.valueOf(100))));
                        } else {
                            item.setValor(BigDecimal.ZERO);
                        }
                        item.setPercentual(BigDecimal.valueOf(7).divide(BigDecimal.valueOf(100)));
                        break;
                    case "TOTAL CONSIDERADO PARA CONTRATAÇÃO DE NOVAS OPERAÇÕES DE CRÉDITO (V) = (IV + IIa)":
                        item.setValor(totalAteQuadrimestreSujeita.add(totalAteQuadrimestreNaoSujeita));
                        if (totalCorrenteLiquida != BigDecimal.ZERO) {
                            item.setPercentual(item.getValor().divide(totalCorrenteLiquida, 6, BigDecimal.ROUND_HALF_EVEN));
                        } else {
                            item.setPercentual(BigDecimal.ZERO);
                        }
                        break;
                    case "LIMITE DE ALERTA (inciso III do §1º do art. 59 da LRF) - 14,40 %":
                        if (totalCorrenteLiquida != BigDecimal.ZERO) {
                            item.setValor(totalCorrenteLiquida.multiply(BigDecimal.valueOf(14.4).divide(BigDecimal.valueOf(100))));
                        } else {
                            item.setValor(BigDecimal.ZERO);
                        }
                        item.setPercentual(BigDecimal.valueOf(14.4).divide(BigDecimal.valueOf(100)));
                        break;
                }
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    public void gerarRelatorio() {
        try {
            relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
            Exercicio e = sistemaControlador.getExercicioCorrente();
            if (quadrimestre == 1) {
                dataInicial = "01/01/" + e.getAno();
                dataFinal = "30/04/" + e.getAno();
                mesFinal = "4";
            } else if (quadrimestre == 2) {
                dataInicial = "01/05/" + e.getAno();
                dataFinal = "31/08/" + e.getAno();
                mesFinal = "8";
            } else if (quadrimestre == 3) {
                dataInicial = "01/09/" + e.getAno();
                dataFinal = "31/12/" + e.getAno();
                mesFinal = "12";
            }
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(prepararDados());
            HashMap parameters = montarParametros();
            gerarRelatorioComDadosEmCollection(getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarRelatorio() {
        try {
            relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(prepararDados());
            HashMap parameters = montarParametros();
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(sistemaControlador.getExercicioCorrente());
            anexo.setNome("Anexo 4 - Demonstrativo das Operações de Crédito");
            anexo.setMes(Mes.getMesToInt(Integer.valueOf(mesFinal)));
            anexo.setTipo(PortalTransparenciaTipo.RGF);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "RelatorioAnexo04RGFOperacoes_2017.jasper";
    }

    private HashMap montarParametros() {
        HashMap parameters = new HashMap();
        parameters.put("SUBREPORT_DIR", getCaminho());
        parameters.put("ANO_EXERCICIO", sistemaControlador.getExercicioCorrente().getAno().toString());
        if (quadrimestre == 1) {
            parameters.put("DATAINICIAL", "JANEIRO");
            parameters.put("DATAFINAL", " ABRIL");
        } else if (quadrimestre == 2) {
            parameters.put("DATAINICIAL", "MAIO");
            parameters.put("DATAFINAL", " AGOSTO");
        } else if (quadrimestre == 3) {
            parameters.put("DATAINICIAL", "SETEMBRO");
            parameters.put("DATAFINAL", "DEZEMBRO");
        }
        parameters.put("APURACAO", preparaDadosApuracao());
        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getUsername());
        }
        return parameters;
    }

    @URLAction(mappingId = "relatorio-rgf-anexo4-2017", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.totalAteQuadrimestreSujeita = BigDecimal.ZERO;
        this.totalAteQuadrimestreNaoSujeita = BigDecimal.ZERO;
        this.totalCorrenteLiquida = BigDecimal.ZERO;
    }

    private BigDecimal calculaValorLiquidaMes(String mes) {
        try {
            BigDecimal toReturn = BigDecimal.ZERO;
            RelatoriosItemDemonst relatorio = relatorioRGFAnexo04Calculator2017.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo04Calculator2017.recuperarItemDemonstrativoPeloNomeERelatorio("RECEITA CORRENTE LÍQUIDA", sistemaControlador.getExercicioCorrente(), relatorio);
            GregorianCalendar dataCalendar = new GregorianCalendar(sistemaControlador.getExercicioCorrente().getAno(), new Integer(mes), new Integer(1));
            String data = formataDataMesAno(dataCalendar.getTime());
            for (int i = 1; i <= 12; i++) {
                toReturn = toReturn.add(relatorioRGFAnexo04Calculator2017.getRelatorioRREOAnexo03Calculator().recuperarValorPeloMesAnterior(itemDemonstrativo, data, relatorio, sistemaControlador.getExercicioCorrente()));
                data = alteraMes(data);
            }
            return toReturn;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    private String formataDataMesAno(Date data) {
        SimpleDateFormat formato = new SimpleDateFormat("MM/yyyy");
        return formato.format(data);
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


    private BigDecimal calcularValorNoQuadrimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) {
//        BigDecimal retorno = relatorioRGFAnexo04Calculator2017.calcularPrevisaoInicialAlterado(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst);
        return relatorioRGFAnexo04Calculator2017.calcularPrevisaoAtualizadaAlterado(itemDemonstrativo, exercicioCorrente, dataInicial, dataFinal, relatoriosItemDemonst);
//        return retorno;
    }

    private BigDecimal calcularValorAteQuadrimestre(ItemDemonstrativo itemDemonstrativo, Exercicio exercicioCorrente, RelatoriosItemDemonst relatoriosItemDemonst) throws ParseException {
        BigDecimal retorno = relatorioRGFAnexo04Calculator2017.calcularPrevisaoInicialAlterado(itemDemonstrativo, exercicioCorrente, relatoriosItemDemonst);
        retorno = retorno.add(relatorioRGFAnexo04Calculator2017.calcularPrevisaoAtualizadaAlterado(itemDemonstrativo, exercicioCorrente, "01/01/" + exercicioCorrente.getAno(), dataFinal, relatoriosItemDemonst));
        return retorno;
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

    public RelatorioRGFAnexo04Calculator2017 getRelatorioRGFAnexo04Calculator2017() {
        return relatorioRGFAnexo04Calculator2017;
    }

    public void setRelatorioRGFAnexo04Calculator2017(RelatorioRGFAnexo04Calculator2017 relatorioRGFAnexo04Calculator2017) {
        this.relatorioRGFAnexo04Calculator2017 = relatorioRGFAnexo04Calculator2017;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public BigDecimal getTotalAteQuadrimestreSujeita() {
        return totalAteQuadrimestreSujeita;
    }

    public void setTotalAteQuadrimestreSujeita(BigDecimal totalAteQuadrimestreSujeita) {
        this.totalAteQuadrimestreSujeita = totalAteQuadrimestreSujeita;
    }

    public BigDecimal getTotalAteQuadrimestreNaoSujeita() {
        return totalAteQuadrimestreNaoSujeita;
    }

    public void setTotalAteQuadrimestreNaoSujeita(BigDecimal totalAteQuadrimestreNaoSujeita) {
        this.totalAteQuadrimestreNaoSujeita = totalAteQuadrimestreNaoSujeita;
    }

    public BigDecimal getTotalCorrenteLiquida() {
        return totalCorrenteLiquida;
    }

    public void setTotalCorrenteLiquida(BigDecimal totalCorrenteLiquida) {
        this.totalCorrenteLiquida = totalCorrenteLiquida;
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

    public ItemDemonstrativoFacade getItemDemonstrativoFacade() {
        return itemDemonstrativoFacade;
    }

    public void setItemDemonstrativoFacade(ItemDemonstrativoFacade itemDemonstrativoFacade) {
        this.itemDemonstrativoFacade = itemDemonstrativoFacade;
    }
}
