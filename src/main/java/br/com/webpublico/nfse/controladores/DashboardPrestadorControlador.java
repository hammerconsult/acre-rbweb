package br.com.webpublico.nfse.controladores;


import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.nfse.domain.dtos.DashboardPrestadorDTO;
import br.com.webpublico.nfse.domain.dtos.EstatisticaMensalNfseDTO;
import br.com.webpublico.nfse.facades.DashboardPrestadorFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Renato on 25/01/2019.
 */


@ManagedBean(name = "dashboardPrestadorControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-dashboard-prestador", pattern = "/nfse/dashboard-prestador/",
        viewId = "/faces/tributario/nfse/dashboard-prestador/edita.xhtml")
})
public class DashboardPrestadorControlador implements Serializable {

    @EJB
    private DashboardPrestadorFacade facade;
    private DashboardPrestadorDTO selecionado;
    private CartesianChartModel model;
    private Future<DashboardPrestadorDTO> future;

    public DashboardPrestadorControlador() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @URLAction(mappingId = "novo-dashboard-prestador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado = new DashboardPrestadorDTO();
    }

    public void buscarInformacoes() {
        try {
            validar();
            future = facade.buscarInformacoes(selecionado);
            FacesUtil.executaJavaScript("pollBuscaInformacoes.start()");
            FacesUtil.executaJavaScript("aguarde.show()");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validar() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataInicial() == null || selecionado.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Período deve ser informado.");
        }
        if (selecionado.getCadastroEconomico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Cadastro Econômico deve ser informado.");
        }
        ve.lancarException();
    }

    private void criarChart() throws ValidacaoException {
        if (selecionado.getEstatisticas() != null && !selecionado.getEstatisticas().isEmpty()) {
            model = new CartesianChartModel();

            ChartSeries servico = new ChartSeries();
            servico.setLabel("Total Serviço");

            ChartSeries iss = new ChartSeries();
            iss.setLabel("ISS Calculado");

            for (EstatisticaMensalNfseDTO estatistica : selecionado.getEstatisticas()) {
                servico.set(estatistica.getMes() + "/" + estatistica.getAno(), estatistica.getTotalServicos());
                iss.set(estatistica.getMes() + "/" + estatistica.getAno(), estatistica.getIss());
            }
            model.addSeries(servico);
            model.addSeries(iss);

        } else {
            ValidacaoException operacaoNaoPermitidaException = new ValidacaoException();
            operacaoNaoPermitidaException.adicionarMensagemDeOperacaoNaoPermitida("Nenhum registro foi encontrado.");
            operacaoNaoPermitidaException.lancarException();
        }
    }

    public DashboardPrestadorDTO getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(DashboardPrestadorDTO selecionado) {
        this.selecionado = selecionado;
    }

    public CartesianChartModel getModel() {
        return model;
    }

    public void setModel(CartesianChartModel model) {
        this.model = model;
    }

    public void acompanharBuscaInformacoes() throws ExecutionException, InterruptedException {
        if (future.isDone() || future.isCancelled()) {
            FacesUtil.executaJavaScript("pollBuscaInformacoes.stop()");
            selecionado = future.get();
            criarChart();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("Formulario");
        }
    }
}
