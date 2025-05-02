package br.com.webpublico.negocios.contabil.services;

import br.com.webpublico.entidades.comum.FechamentoMensalMes;
import br.com.webpublico.entidadesauxiliares.contabil.BarraProgressoAssistente;
import br.com.webpublico.entidadesauxiliares.contabil.ReprocessamentoContabil;
import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoSituacaoAgendamento;
import br.com.webpublico.negocios.comum.FechamentoMensalFacade;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoContabilFacade;
import br.com.webpublico.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.util.List;

@Service
@Transactional
public class ServiceReprocessamentoContabil {

    private static final Logger logger = LoggerFactory.getLogger(ServiceReprocessamentoContabil.class.getName());
    private ReprocessamentoContabilFacade reprocessamentoContabilFacade;
    private FechamentoMensalFacade fechamentoMensalFacade;

    public ServiceReprocessamentoContabil() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @PostConstruct
    public void init() {
        try {
            reprocessamentoContabilFacade = (ReprocessamentoContabilFacade) new InitialContext().lookup("java:module/ReprocessamentoContabilFacade");
            fechamentoMensalFacade = (FechamentoMensalFacade) new InitialContext().lookup("java:module/FechamentoMensalFacade");
        } catch (Exception e) {
            logger.error("Erro ao iniciar o service de reprocessamento contábil: ", e);
        }
    }

    public void reprocessar() {
        FechamentoMensalMes fechamentoDaVez = null;
        try {
            List<FechamentoMensalMes> fechamentos = fechamentoMensalFacade.buscarConfiguracoesReprocessamento();
            for (FechamentoMensalMes fechamento : fechamentos) {
                fechamentoDaVez = fechamento;
                BarraProgressoAssistente barra = new BarraProgressoAssistente();
                barra.inicializa();
                ReprocessamentoContabil reprocessamentoContabil = criarReprocessamento(barra, fechamento);
                reprocessamentoContabilFacade.apagar(reprocessamentoContabil);
                reprocessamentoContabilFacade.inicializarSingleton(reprocessamentoContabil);
                reprocessamentoContabilFacade.buscarContasAuxiliaresDetalhadas(reprocessamentoContabil.getExercicio());
                reprocessamentoContabilFacade.reprocessar(reprocessamentoContabil);
                reprocessamentoContabilFacade.reprocessarTransferencias(reprocessamentoContabil);
                fechamento.setTipoSituacaoAgendamento(TipoSituacaoAgendamento.EXECUTADO);
                fechamentoMensalFacade.salvarFechamentoMensalMes(fechamento);
            }
        } catch (Exception e) {
            logger.error("Ocorreu um erro ao reprocessar automaticamente: ", e);
            if (fechamentoDaVez != null) {
                fechamentoDaVez.setTipoSituacaoAgendamento(TipoSituacaoAgendamento.ERRO);
                fechamentoMensalFacade.salvarFechamentoMensalMes(fechamentoDaVez);
            }
        }
    }

    private ReprocessamentoContabil criarReprocessamento(BarraProgressoAssistente barraProgressoAssistente, FechamentoMensalMes fechamentoMensalMes) {
        ReprocessamentoContabil reprocessamento = new ReprocessamentoContabil();
        reprocessamento.setDataInicio(DataUtil.criarDataComMesEAno(fechamentoMensalMes.getMes().getNumeroMes(), fechamentoMensalMes.getFechamentoMensal().getExercicio().getAno()).toDate());
        reprocessamento.setDataFim(DataUtil.criarDataUltimoDiaMes(fechamentoMensalMes.getMes().getNumeroMes(), fechamentoMensalMes.getFechamentoMensal().getExercicio().getAno()).toDate());
        reprocessamento.setExercicio(fechamentoMensalMes.getFechamentoMensal().getExercicio());
        reprocessamento.setBarraProgressoAssistente(barraProgressoAssistente);
        reprocessamento.getTipoBalancetes().add(TipoBalancete.MENSAL);
        reprocessamento.setTipoEventoContabils(TipoEventoContabil.getTiposEventoContabilRetirandoInicialLOA());
        return reprocessamento;
    }
}
