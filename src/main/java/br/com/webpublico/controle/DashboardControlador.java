package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LOA;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.LOAFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 22/01/15
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMapping(id = "dashboard", pattern = "/dashboard/", viewId = "/faces/dashboard.xhtml")
public class DashboardControlador implements Serializable {

    @EJB
    private LOAFacade facade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private DashBoard selecionado;
    private List<HierarquiaOrganizacional> unidades;
    private CartesianChartModel model;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private String filtroUnidade;

    public DashboardControlador() {

    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "dashboard", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        SistemaControlador sistemaControlador = getSistemaControlador();

        filtroUnidade = "";

        recuperarValores(sistemaControlador);

        unidades = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaControlador.getUsuarioCorrente(), sistemaControlador.getExercicioCorrente(), sistemaControlador.getDataOperacao(), "ORCAMENTARIA", 3);
        if (!unidades.isEmpty()) {
            hierarquiaOrganizacional = unidades.get(0);
        }

        criarChart();
    }

    private void recuperarValores(SistemaControlador sistemaControlador) {
        Exercicio exercicioCorrente = sistemaControlador.getExercicioCorrente();
        LOA loa = facade.listaUltimaLoaPorExercicio(exercicioCorrente);
        Date dataOperacao = sistemaControlador.getDataOperacao();
        Calendar inicial = getDataInicial(sistemaControlador);

        BigDecimal valorReceitaArrecadada = facade.getValorReceitaArrecadada(inicial.getTime(), dataOperacao, null);
        BigDecimal valorDespesaEmpenhada = facade.getValorDespesaEmpenhada(inicial.getTime(), dataOperacao, null);
        BigDecimal valorDespesaLiquidada = facade.getValorDespesaLiquidada(inicial.getTime(), dataOperacao, null);
        BigDecimal valorDespesaPaga = facade.getValorDespesaPagamento(inicial.getTime(), dataOperacao, null);

        selecionado = new DashBoard();
        selecionado.setValorOrçamento(loa.getValorDaDespesa());
        selecionado.setDespesaEmpenhada(valorDespesaEmpenhada);
        selecionado.setDespesaLiquidada(valorDespesaLiquidada);
        selecionado.setDespesaPaga(valorDespesaPaga);
        selecionado.setReceitaArrecadada(valorReceitaArrecadada);
    }

    private Calendar getDataInicial(SistemaControlador sistemaControlador) {
        Calendar inicial = Calendar.getInstance();
        inicial.set(Calendar.YEAR, DataUtil.getAno(sistemaControlador.getDataOperacao()));
        inicial.set(Calendar.MONTH, 0);
        inicial.set(Calendar.DAY_OF_MONTH, 0);
        return inicial;
    }

    private void criarChart() {
        model = new CartesianChartModel();
        montarValores();

    }

    private void montarValores() {
        SistemaControlador sistemaControlador = getSistemaControlador();

        ChartSeries despesa = new ChartSeries();
        despesa.setLabel("Despesa");

        ChartSeries receita = new ChartSeries();
        receita.setLabel("Receita");

        for (Mes mes : Mes.values()) {
            Calendar inicial = Calendar.getInstance();

            int ano = DataUtil.getAno(sistemaControlador.getDataOperacao());
            Integer numeroMes = mes.getNumeroMes() - 2;
            int dia = DataUtil.ultimoDiaDoMes(numeroMes);

            inicial.set(Calendar.YEAR, ano);
            inicial.set(Calendar.MONTH, numeroMes);
            inicial.set(Calendar.DAY_OF_MONTH, dia);

            Calendar fim = Calendar.getInstance();
            fim.set(Calendar.YEAR, ano);
            fim.set(Calendar.MONTH, (numeroMes + 1));
            fim.set(Calendar.DAY_OF_MONTH, DataUtil.ultimoDiaDoMes(numeroMes + 1));

            BigDecimal valorDespesa = facade.getValorDespesaEmpenhada(inicial.getTime(), fim.getTime(), hierarquiaOrganizacional);
            despesa.set(mes.name(), valorDespesa);

            BigDecimal valorReceita = facade.getValorReceitaArrecadada(inicial.getTime(), fim.getTime(), hierarquiaOrganizacional);
            receita.set(mes.name(), valorReceita);
        }

        model.addSeries(despesa);
        model.addSeries(receita);
    }

    public void alterarHierarquia(HierarquiaOrganizacional ho) {
        hierarquiaOrganizacional = ho;
        criarChart();
    }

    public DashBoard getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(DashBoard selecionado) {
        this.selecionado = selecionado;
    }

    public List<HierarquiaOrganizacional> getUnidades() {
        if (filtroUnidade.trim().isEmpty()) {
            return unidades;
        } else {
            List<HierarquiaOrganizacional> retorno = new ArrayList<HierarquiaOrganizacional>();
            for (HierarquiaOrganizacional unidade : unidades) {
                if (unidade.getCodigo().contains(filtroUnidade.trim())
                        || unidade.getSubordinada().getDescricao().toLowerCase().contains(filtroUnidade.trim().toLowerCase())) {
                    retorno.add(unidade);
                }
            }
            return retorno;
        }
    }

    public void setUnidades(List<HierarquiaOrganizacional> unidades) {
        this.unidades = unidades;
    }

    public CartesianChartModel getModel() {
        return model;
    }

    public void setModel(CartesianChartModel model) {
        this.model = model;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public String getFiltroUnidade() {
        return filtroUnidade;
    }

    public void setFiltroUnidade(String filtroUnidade) {
        this.filtroUnidade = filtroUnidade;
    }

    public class ItemDashBoard {
        private Date data;
        private BigDecimal valor;

        public Date getData() {
            return data;
        }

        public void setData(Date data) {
            this.data = data;
        }

        public BigDecimal getValor() {
            return valor;
        }

        public void setValor(BigDecimal valor) {
            this.valor = valor;
        }
    }

    public class DashBoard {
        private BigDecimal valorOrçamento;
        private BigDecimal receitaArrecadada;
        private BigDecimal despesaEmpenhada;
        private BigDecimal despesaLiquidada;
        private BigDecimal despesaPaga;

        public BigDecimal getDespesaPaga() {
            return despesaPaga;
        }

        public void setDespesaPaga(BigDecimal despesaPaga) {
            this.despesaPaga = despesaPaga;
        }

        public BigDecimal getDespesaLiquidada() {
            return despesaLiquidada;
        }

        public void setDespesaLiquidada(BigDecimal despesaLiquidada) {
            this.despesaLiquidada = despesaLiquidada;
        }

        public BigDecimal getValorOrçamento() {
            return valorOrçamento;
        }

        public void setValorOrçamento(BigDecimal valorOrçamento) {
            this.valorOrçamento = valorOrçamento;
        }

        public BigDecimal getReceitaArrecadada() {
            return receitaArrecadada;
        }

        public void setReceitaArrecadada(BigDecimal receitaArrecadada) {
            this.receitaArrecadada = receitaArrecadada;
        }

        public BigDecimal getDespesaEmpenhada() {
            return despesaEmpenhada;
        }

        public void setDespesaEmpenhada(BigDecimal despesaEmpenhada) {
            this.despesaEmpenhada = despesaEmpenhada;
        }
    }
}


