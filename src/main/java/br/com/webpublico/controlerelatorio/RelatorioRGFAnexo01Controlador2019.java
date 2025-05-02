package br.com.webpublico.controlerelatorio;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.NotaExplicativaRGF;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoFiltros;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.RGFAnexo01Pessoal;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.NotaExplicativaRGFFacade;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo01Facade;
import br.com.webpublico.util.DataUtil;
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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mateus on 23/04/18.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo1-2019", pattern = "/relatorio/rgf/anexo1/2019/", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo01-2019.xhtml")})
public class RelatorioRGFAnexo01Controlador2019 extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private RelatorioRGFAnexo01Facade relatorioRGFAnexo01Facade;
    @EJB
    private NotaExplicativaRGFFacade notaExplicativaRGFFacade;
    private BigDecimal percentual;
    private BigDecimal percentualPrudencial;
    private BigDecimal percentualLimite;
    private BigDecimal percentualDespesa;
    private BigDecimal limiteMaximo;
    private BigDecimal limitePrudencial;
    private BigDecimal limiteAlerta;
    private BigDecimal despesaTotalComPessoal;
    private Mes mesFinal;
    @Enumerated(EnumType.STRING)
    private EsferaDoPoder esferaDoPoder;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private List<ItemDemonstrativoComponente> itens;
    private ItemDemonstrativoFiltros itemDemonstrativoFiltros;
    private String notaExplicativa;

    public RelatorioRGFAnexo01Controlador2019() {
        itens = Lists.newArrayList();
    }

    @URLAction(mappingId = "relatorio-rgf-anexo1-2019", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.esferaDoPoder = null;
        this.percentual = BigDecimal.ZERO;
        this.percentualPrudencial = BigDecimal.ZERO;
        this.percentualDespesa = BigDecimal.ZERO;
        this.limiteMaximo = BigDecimal.ZERO;
        this.limitePrudencial = BigDecimal.ZERO;
        this.despesaTotalComPessoal = BigDecimal.ZERO;
        this.mesFinal = Mes.ABRIL;
        itemDemonstrativoFiltros = new ItemDemonstrativoFiltros();
        buscarNotaPorMes();
    }

    public void buscarNotaPorMes() {
        notaExplicativa = (esferaDoPoder != null ? notaExplicativaRGFFacade.buscarNotaPorMesAndEsferaDoPoder(mesFinal, sistemaControlador.getExercicioCorrente(), AnexoRGF.ANEXO_1, esferaDoPoder) :
            notaExplicativaRGFFacade.buscarNotaPorMes(mesFinal, sistemaControlador.getExercicioCorrente(), AnexoRGF.ANEXO_1)).getNotaExplicativa();
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(Mes.ABRIL, "Jan - Abr"));
        retorno.add(new SelectItem(Mes.AGOSTO, "Mai - Ago"));
        retorno.add(new SelectItem(Mes.DEZEMBRO, "Set - Dez"));
        return retorno;
    }

    public List<SelectItem> getEsferasDoPoder() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "Consolidado"));
        toReturn.add(new SelectItem(EsferaDoPoder.EXECUTIVO, EsferaDoPoder.EXECUTIVO.getDescricao()));
        toReturn.add(new SelectItem(EsferaDoPoder.LEGISLATIVO, EsferaDoPoder.LEGISLATIVO.getDescricao()));
        return toReturn;
    }

    public BigDecimal buscarDespesaTotalComPessoa(List<ParametrosRelatorios> parametros) {
        instanciarItemDemonstrativoFiltros();
        itemDemonstrativoFiltros.setParametros(parametros);
        List<ItemDemonstrativo> itensDemonstrativos = relatorioRGFAnexo01Facade.getItemDemonstrativoFacade().buscarItensPorExercicioAndRelatorio(sistemaControlador.getExercicioCorrente(), "", "Anexo 1", TipoRelatorioItemDemonstrativo.RGF);
        itens = Lists.newArrayList();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
        despesaTotalComPessoal = BigDecimal.ZERO;
        buscarDadosGrupoUm(null);
        return despesaTotalComPessoal;
    }

    private List<RGFAnexo01Pessoal> buscarDadosGrupoUm(HashMap parameters) {
        List<RGFAnexo01Pessoal> toReturn = new ArrayList<>();
        relatoriosItemDemonst = relatorioRGFAnexo01Facade.getItemDemonstrativoFacade().getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        relatorioRGFAnexo01Facade.limparHashItensRecuperados();
        for (ItemDemonstrativoComponente item : itens) {
            if (item.getGrupo() == 1) {
                instanciarItemDemonstrativoFiltros();
                if (parameters != null) {
                    parameters.put("MES", Mes.getMesToInt(Integer.valueOf(itemDemonstrativoFiltros.getDataFinal().substring(3, 5))).getDescricao().substring(0, 3).toUpperCase() + "/" + itemDemonstrativoFiltros.getDataFinal().substring(6, 10));
                }
                ItemDemonstrativo itemDemonstrativo = relatorioRGFAnexo01Facade.recuperarItemDemonstrativoPeloNomeERelatorio(item.getDescricao(), sistemaControlador.getExercicioCorrente(), relatoriosItemDemonst);
                RGFAnexo01Pessoal it = new RGFAnexo01Pessoal();
                it.setDescricao(item.getDescricaoComEspacos());
                it.setMesAtual(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-1", parameters);
                it.setMesMenosUm(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-2", parameters);
                it.setMesMenosDois(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-3", parameters);
                it.setMesMenosTres(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-4", parameters);
                it.setMesMenosQuatro(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-5", parameters);
                it.setMesMenosCinco(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-6", parameters);
                it.setMesMenosSeis(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-7", parameters);
                it.setMesMenosSete(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-8", parameters);
                it.setMesMenosOito(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-9", parameters);
                it.setMesMenosNove(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-10", parameters);
                it.setMesMenosDez(calcularLiquidacao(itemDemonstrativo));
                removerUmMes(itemDemonstrativoFiltros.getDataFinal(), "MES-11", parameters);
                it.setMesMenosOnze(calcularLiquidacao(itemDemonstrativo));
                if (Mes.DEZEMBRO.equals(mesFinal)) {
                    it.setInscritasRestos(relatorioRGFAnexo01Facade.calcularRestos(itemDemonstrativo,
                        getExercicioCorrente(),
                        "01/01/" + getExercicioCorrente().getAno(),
                        Util.getDiasMes(mesFinal.getNumeroMes(), getExercicioCorrente().getAno()) + "/" + mesFinal.getNumeroMesString() + "/" + getExercicioCorrente().getAno(),
                        "",
                        esferaDoPoder == null ? "" : " AND VW.esferadopoder = '" + esferaDoPoder.name() + "'",
                        itemDemonstrativoFiltros.getRelatorio()));
                } else {
                    instanciarItemDemonstrativoFiltros();
                    itemDemonstrativoFiltros.setDataInicial("01/01/" + sistemaControlador.getExercicioCorrente().getAno());
                    it.setInscritasRestos(calcularInscricaoRestos(itemDemonstrativo));
                }
                it.setTotalUltimosMeses(it.getMesMenosOnze().add(it.getMesMenosDez()).add(it.getMesMenosNove()).add(it.getMesMenosOito()).add(it.getMesMenosSete()).add(
                    it.getMesMenosSeis()).add(it.getMesMenosCinco()).add(it.getMesMenosQuatro()).add(it.getMesMenosTres()).add(it.getMesMenosDois()).add(it.getMesMenosUm()).add(it.getMesAtual()));
                if (item.getOrdem() == 16) {
                    despesaTotalComPessoal = it.getTotalUltimosMeses().add(it.getInscritasRestos());
                }
                toReturn.add(it);
            }
        }
        return toReturn;
    }

    private void instanciarItemDemonstrativoFiltros() {
        itemDemonstrativoFiltros.setDataInicial("01/" + mesFinal.getNumeroMesString() + "/" + sistemaControlador.getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setDataFinal(Util.getDiasMes(mesFinal.getNumeroMes(), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal.getNumeroMesString() + "/" + sistemaControlador.getExercicioCorrente().getAno());
        itemDemonstrativoFiltros.setRelatorio(relatoriosItemDemonst);
        itemDemonstrativoFiltros.setExercicio(sistemaControlador.getExercicioCorrente());
        itemDemonstrativoFiltros.setParametros(criarParametros());
    }

    private List<ParametrosRelatorios> criarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        if (esferaDoPoder != null) {
            parametros.add(new ParametrosRelatorios(" vw.esferadopoder ", ":esferaDoPoder", null, OperacaoRelatorio.IGUAL, esferaDoPoder.name(), null, 1, false));
        }
        return parametros;
    }

    private BigDecimal calcularLiquidacao(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRGFAnexo01Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.DESPESAS_LIQUIDADAS_ATE_BIMESTRE);
    }

    private BigDecimal calcularInscricaoRestos(ItemDemonstrativo itemDemonstrativo) {
        return relatorioRGFAnexo01Facade.calcularValor(itemDemonstrativo.toDto(), itemDemonstrativoFiltros.toDto(), TipoCalculoItemDemonstrativo.RESTOS_A_PAGAR_INSCRITOS_NAO_PROCESSADOS);
    }

    private void removerUmMes(String data, String parametroMes, HashMap parameters) {
        data = Util.formatterMesAno.format(DataUtil.getDateParse(data));
        Integer mes = Integer.parseInt(data.substring(0, 2));
        Integer ano = Integer.parseInt(data.substring(3, 7));
        if (mes != 0 && mes != 1) {
            mes -= 2;
        } else {
            mes = 11;
            ano -= 1;
        }
        itemDemonstrativoFiltros.setDataInicial(DataUtil.getDataFormatada(DataUtil.montaData(
            1, mes, ano).getTime()));
        itemDemonstrativoFiltros.setDataFinal(DataUtil.getDataFormatada(DataUtil.montaData(
            DataUtil.getDiasNoMes((mes + 1), ano), mes, ano).getTime()));
        if (ano < getExercicioCorrente().getAno() && ano < itemDemonstrativoFiltros.getExercicio().getAno()) {
            itemDemonstrativoFiltros.setExercicio(getExercicioFacade().getExercicioPorAno(ano));
        }
        if (parameters != null) {
            parameters.put(parametroMes, Mes.getMesToInt((mes + 1)).getDescricao().substring(0, 3).toUpperCase() + "/" + ano);
        }
    }

    public void gerarRelatorio() {
        try {
            HashMap parameters = new HashMap();
            List<RGFAnexo01Pessoal> resultado = buscarDadosGrupoUm(parameters);
            montarParametros(parameters);
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(resultado);
            gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeArquivo(), getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeArquivo() {
        return "LRF-RGF-ANEXO-1-" + getSistemaControlador().getExercicioCorrente().getAno() + "-" + mesFinal.getNumeroMesString() + "-" + (esferaDoPoder != null ? esferaDoPoder.getDescricao().substring(0, 1) : "C");
    }

    public void salvarRelatorio() {
        try {
            HashMap parameters = new HashMap();
            List<RGFAnexo01Pessoal> lista = buscarDadosGrupoUm(parameters);
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(lista);
            montarParametros(parameters);
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(sistemaControlador.getExercicioCorrente());
            anexo.setNome("Anexo 1 - Demonstrativo da Despesa com Pessoal");
            anexo.setMes(mesFinal);
            anexo.setTipo(PortalTransparenciaTipo.RGF);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "RelatorioRGFAnexo01-2019.jasper";
    }

    private void montarParametros(HashMap parameters) {
        if (EsferaDoPoder.EXECUTIVO.equals(esferaDoPoder)) {
            percentual = BigDecimal.valueOf(54);
            percentualPrudencial = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.05)));
            percentualLimite = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.1)));
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER EXECUTIVO");
            parameters.put("NOME_RELATORIO", "DEMONSTRATIVO DA DESPESA COM PESSOAL");
        } else if (EsferaDoPoder.LEGISLATIVO.equals(esferaDoPoder)) {
            percentual = BigDecimal.valueOf(6);
            percentualPrudencial = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.05)));
            percentualLimite = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.1)));
            parameters.put("NOME_RELATORIO", "DEMONSTRATIVO DA DESPESA COM PESSOAL");
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER LEGISLATIVO");
        } else {
            percentual = BigDecimal.valueOf(60);
            percentualPrudencial = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.05)));
            percentualLimite = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.1)));
            parameters.put("NOME_RELATORIO", "DEMONSTRATIVO CONSOLIDADO DA DESPESA COM PESSOAL");
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        }
        parameters.put("PERCENTUAL", percentual);
        parameters.put("PERCENTUALPRUD", percentualPrudencial);
        parameters.put("PERCENTUALALERTA", percentualLimite);
        BigDecimal rcl = relatorioRGFAnexo01Facade.getRelatorioRREOAnexo03Calculator().calcularRcl(mesFinal, sistemaControlador.getExercicioCorrente());
        BigDecimal transferenciasObrigatorias = recuperarValorTransferencias();
        parameters.put("RCL", rcl);
        parameters.put("TRANSFERENCIAS", transferenciasObrigatorias);
        BigDecimal rclAjustada = rcl.subtract(transferenciasObrigatorias);
        parameters.put("RCLAJUSTADA", rclAjustada);
        if (rclAjustada.compareTo(BigDecimal.ZERO) != 0) {
            percentualDespesa = despesaTotalComPessoal.divide(rclAjustada, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100));
            limiteMaximo = rclAjustada.multiply(percentual).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
            limitePrudencial = rclAjustada.multiply(percentualPrudencial).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
            limiteAlerta = rclAjustada.multiply(percentualLimite).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
        }
        parameters.put("PERCENTUALDESP", percentualDespesa);
        parameters.put("LIMITEMAXIMO", limiteMaximo);
        parameters.put("LIMITEPRUDENCIAL", limitePrudencial);
        parameters.put("LIMITEALERTA", limiteAlerta);
        parameters.put("DESPESA", despesaTotalComPessoal);

        parameters.put("IMAGEM", getCaminhoImagem());
        parameters.put("USER", sistemaControlador.getUsuarioCorrente().getNome());
        parameters.put("DATAINICIAL", getMesInicialParaCabecalho().getDescricao().toUpperCase());
        parameters.put("DATAFINAL", mesFinal.getDescricao().toUpperCase() + " DE " + sistemaControlador.getExercicioCorrente().getAno());
        parameters.put("NOTAEXPLICATIVA", "NOTA: " + notaExplicativa.trim());
        NotaExplicativaRGF notaExplicativaRGF = (esferaDoPoder != null ? notaExplicativaRGFFacade.buscarNotaPorMesAndEsferaDoPoder(mesFinal, sistemaControlador.getExercicioCorrente(), AnexoRGF.ANEXO_1, esferaDoPoder) :
            notaExplicativaRGFFacade.buscarNotaPorMes(mesFinal, sistemaControlador.getExercicioCorrente(), AnexoRGF.ANEXO_1));
        notaExplicativaRGF.setNotaExplicativa(notaExplicativa);
        notaExplicativaRGF.setAnexoRGF(AnexoRGF.ANEXO_1);
        notaExplicativaRGF.setEsferaDoPoder(esferaDoPoder);
        notaExplicativaRGF.setExercicio(sistemaControlador.getExercicioCorrente());
        notaExplicativaRGFFacade.salvar(notaExplicativaRGF);
    }

    private BigDecimal recuperarValorTransferencias() {
        return relatorioRGFAnexo01Facade.recuperarValorTransferencias(mesFinal, sistemaControlador.getExercicioCorrente());
    }

    private Mes getMesInicialParaCabecalho() {
        if (Mes.ABRIL.equals(mesFinal)) {
            return Mes.JANEIRO;
        } else if (Mes.AGOSTO.equals(mesFinal)) {
            return Mes.MAIO;
        } else {
            return Mes.SETEMBRO;
        }
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public BigDecimal getPercentualPrudencial() {
        return percentualPrudencial;
    }

    public void setPercentualPrudencial(BigDecimal percentualPrudencial) {
        this.percentualPrudencial = percentualPrudencial;
    }

    public BigDecimal getLimiteMaximo() {
        return limiteMaximo;
    }

    public void setLimiteMaximo(BigDecimal limiteMaximo) {
        this.limiteMaximo = limiteMaximo;
    }

    public BigDecimal getLimitePrudencial() {
        return limitePrudencial;
    }

    public void setLimitePrudencial(BigDecimal limitePrudencial) {
        this.limitePrudencial = limitePrudencial;
    }

    public BigDecimal getPercentualDespesa() {
        return percentualDespesa;
    }

    public void setPercentualDespesa(BigDecimal percentualDespesa) {
        this.percentualDespesa = percentualDespesa;
    }

    public BigDecimal getDespesaTotalComPessoal() {
        return despesaTotalComPessoal;
    }

    public void setDespesaTotalComPessoal(BigDecimal despesaTotalComPessoal) {
        this.despesaTotalComPessoal = despesaTotalComPessoal;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public void setMesFinal(Integer mesFinal) {
        this.mesFinal = Mes.getMesToInt(mesFinal);
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

    public BigDecimal getPercentualLimite() {
        return percentualLimite;
    }

    public void setPercentualLimite(BigDecimal percentualLimite) {
        this.percentualLimite = percentualLimite;
    }

    public BigDecimal getLimiteAlerta() {
        return limiteAlerta;
    }

    public void setLimiteAlerta(BigDecimal limiteAlerta) {
        this.limiteAlerta = limiteAlerta;
    }

    public EsferaDoPoder getEsferaDoPoder() {
        return esferaDoPoder;
    }

    public void setEsferaDoPoder(EsferaDoPoder esferaDoPoder) {
        this.esferaDoPoder = esferaDoPoder;
    }

    public RelatorioRGFAnexo01Facade getRelatorioRGFAnexo01Facade() {
        return relatorioRGFAnexo01Facade;
    }

    public void setRelatorioRGFAnexo01Facade(RelatorioRGFAnexo01Facade relatorioRGFAnexo01Facade) {
        this.relatorioRGFAnexo01Facade = relatorioRGFAnexo01Facade;
    }

    public String getNotaExplicativa() {
        return notaExplicativa;
    }

    public void setNotaExplicativa(String notaExplicativa) {
        this.notaExplicativa = notaExplicativa;
    }
}

