package br.com.webpublico.nfse.facades;


import br.com.webpublico.nfse.domain.dtos.DashboardPrestadorDTO;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class DashboardPrestadorFacade {

    @EJB
    private DeclaracaoMensalServicoFacade declaracaoMensalServicoFacade;

    public DashboardPrestadorFacade() {

    }

    @Asynchronous
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public Future<DashboardPrestadorDTO> buscarInformacoes(DashboardPrestadorDTO selecionado) {
        selecionado.setEstatisticas(declaracaoMensalServicoFacade.buscarServicosPrestadosNaReferencia(selecionado.getDataInicial(), selecionado.getDataFinal(), selecionado.getCadastroEconomico().getId()));
        selecionado.setEstatisticasPorServico(declaracaoMensalServicoFacade.buscarEstatisticasServicosPrestadosNaReferencia(selecionado.getDataInicial(), selecionado.getDataFinal(), selecionado.getCadastroEconomico().getId()));
        return new AsyncResult<>(selecionado);
    }
}
