package br.com.webpublico.nfse.controladores;


import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.nfse.domain.dtos.FiltroEvolucaoEmissaoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.RowEvolucaoEmissaoNfseDTO;
import br.com.webpublico.nfse.facades.DashboardEvolucaoEmissaoFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean(name = "dashboardEvolucaoEmissaoNfseControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-dashboard-evolucao-emissao-nfse",
        pattern = "/nfse/dashboard-evolucao-emissao/",
        viewId = "/faces/tributario/nfse/dashboard-evolucao-emissao/edita.xhtml")
})
public class DashboardEvolucaoEmissaoNfseControlador implements Serializable {

    @EJB
    private DashboardEvolucaoEmissaoFacade facade;
    private FiltroEvolucaoEmissaoNfseDTO filtro;
    private Future<List<RowEvolucaoEmissaoNfseDTO>> futureRows;
    private List<RowEvolucaoEmissaoNfseDTO> rows;
    private CartesianChartModel graficoUsuarios;
    private CartesianChartModel graficoLinhasNotas;
    private PieChartModel graficoPizzaNotas;
    private PieChartModel graficoPizzaUsuarios;

    public DashboardEvolucaoEmissaoNfseControlador() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    public FiltroEvolucaoEmissaoNfseDTO getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroEvolucaoEmissaoNfseDTO filtro) {
        this.filtro = filtro;
    }

    public CartesianChartModel getGraficoUsuarios() {
        return graficoUsuarios;
    }

    public CartesianChartModel getGraficoLinhasNotas() {
        return graficoLinhasNotas;
    }

    public PieChartModel getGraficoPizzaNotas() {
        return graficoPizzaNotas;
    }

    public PieChartModel getGraficoPizzaUsuarios() {
        return graficoPizzaUsuarios;
    }

    public List<RowEvolucaoEmissaoNfseDTO> getRows() {
        return rows;
    }

    public void setRows(List<RowEvolucaoEmissaoNfseDTO> rows) {
        this.rows = rows;
    }

    @URLAction(mappingId = "novo-dashboard-evolucao-emissao-nfse", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroEvolucaoEmissaoNfseDTO();
    }

    public void buscarInformacoes() {
        try {
            validarFiltros();
            futureRows = facade.
                buscarInformacoes(filtro);
            FacesUtil.executaJavaScript("pollBuscaInformacoes.start()");
            FacesUtil.executaJavaScript("aguarde.show()");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getDataInicial() == null || filtro.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Período deve ser informado.");
        }
        ve.lancarException();
    }

    private void criarDashboard() throws ValidacaoException {
        if (rows != null && !rows.isEmpty()) {
            graficoUsuarios = new CartesianChartModel();
            graficoLinhasNotas = new CartesianChartModel();
            graficoPizzaNotas = new PieChartModel();
            graficoPizzaUsuarios = new PieChartModel();

            ChartSeries chartQtdUsuarios = new ChartSeries("Quantidade Usuários");
            ChartSeries chartQtdNotas = new ChartSeries("Quantidade Notas Emitidas");

            for (RowEvolucaoEmissaoNfseDTO row : rows) {
                chartQtdUsuarios.set(row.getMes() + "/" + row.getAno(), row.getQuantidadeUsuarios());
                chartQtdNotas.set(row.getMes() + "/" + row.getAno(), row.getQuantidadeNotas());
                graficoPizzaNotas.getData().put(row.getMes() + "/" + row.getAno() + ": " + row.getQuantidadeNotas(), row.getQuantidadeNotas());
                graficoPizzaUsuarios.getData().put(row.getMes() + "/" + row.getAno() + ": " + row.getQuantidadeUsuarios(), row.getQuantidadeUsuarios());
            }

            graficoUsuarios.addSeries(chartQtdUsuarios);
            graficoLinhasNotas.addSeries(chartQtdNotas);
        } else {
            ValidacaoException operacaoNaoPermitidaException = new ValidacaoException();
            operacaoNaoPermitidaException.adicionarMensagemDeOperacaoNaoPermitida("Nenhum registro foi encontrado.");
            operacaoNaoPermitidaException.lancarException();
        }
    }

    public void acompanharBuscaInformacoes() throws ExecutionException, InterruptedException {
        if (futureRows.isDone() || futureRows.isCancelled()) {
            FacesUtil.executaJavaScript("pollBuscaInformacoes.stop()");
            rows = futureRows.get();
            criarDashboard();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("Formulario");
        }
    }
}
