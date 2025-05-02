package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.CancelamentoParcelamento;
import br.com.webpublico.entidades.ParcelasCancelamentoParcelamento;
import br.com.webpublico.entidades.ProcessoParcelamento;
import br.com.webpublico.entidades.ValorDivida;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class ServiceProcessoParcelamento {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProcessoParcelamento.class.getName());
    @PersistenceContext
    protected transient EntityManager em;
    public ProcessoParcelamentoFacade processoParcelamentoFacade;
    public CancelamentoParcelamentoFacade cancelamentoParcelamentoFacade;
    public DAMFacade damFacade;
    public ExercicioFacade exercicioFacade;

    @PostConstruct
    private void init() {
        try {
            processoParcelamentoFacade = (ProcessoParcelamentoFacade) new InitialContext().lookup("java:module/ProcessoParcelamentoFacade");
            cancelamentoParcelamentoFacade = (CancelamentoParcelamentoFacade) new InitialContext().lookup("java:module/CancelamentoParcelamentoFacade");
            damFacade = (DAMFacade) new InitialContext().lookup("java:module/DAMFacade");
            exercicioFacade = (ExercicioFacade) new InitialContext().lookup("java:module/ExercicioFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar os facades: ", e);
        }
    }

    @Transactional(timeout = 70000, propagation = Propagation.REQUIRES_NEW)
    public List<ProcessoParcelamentoFacade.ParcelamentoCancelar> buscarProcessosParcelamentoParaCancelamentoAutomatico(int inicio, int max) {
        return processoParcelamentoFacade.buscarProcessosParcelamentoParaCancelamentoAutomatico(inicio, max);
    }


    @Transactional(timeout = 70000, propagation = Propagation.REQUIRES_NEW)
    public void cancelarParcelamentos(ProcessoParcelamentoFacade.ParcelamentoCancelar parcelamentoCancelar) throws Exception {
        try {
            Integer anoCorrente = Calendar.getInstance().get(Calendar.YEAR);
            if (parcelamentoCancelar.isInadimplenciaSucessiva()
                && !Util.isConsecutive(parcelamentoCancelar.getSequenciaParcelas(), parcelamentoCancelar.getParcelasInadiplencia())
                && parcelamentoCancelar.getSequenciaParcelas().size() > 1) {
                throw new ExcecaoNegocioGenerica("Parcelas não sequenciais vencidas");

            }
            if (parcelamentoCancelar.isInadimplenciaIntercadala()
                && Util.isConsecutive(parcelamentoCancelar.getSequenciaParcelas(), parcelamentoCancelar.getParcelasInadiplencia())
                && parcelamentoCancelar.getSequenciaParcelas().size() > 1) {
                throw new ExcecaoNegocioGenerica("Parcelas não intercaladas vencidas");

            }
            ProcessoParcelamento parcelamento = processoParcelamentoFacade.recuperar(parcelamentoCancelar.getId());
            CancelamentoParcelamento cancelamentoParcelamento = new CancelamentoParcelamento();
            cancelamentoParcelamento.setProcessoParcelamento(parcelamento);
            parcelamento.setCancelamentoParcelamento(cancelamentoParcelamento);
            processoParcelamentoFacade.inicializarCancelametoDoParcelamento(cancelamentoParcelamento, null, exercicioFacade.recuperarExercicioPeloAno(anoCorrente));
            cancelamentoParcelamento.setCodigo(processoParcelamentoFacade.getCancelamentoParcelamentoFacade().buscarProximoCodigoPorExercicio(cancelamentoParcelamento.getExercicio()));
            parcelamento.getCancelamentoParcelamento().setMotivo(processoParcelamentoFacade.MENSAGEM_CANCELAMENTO);
            Collections.sort(cancelamentoParcelamento.getParcelas());
            if (!cancelamentoParcelamento.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_EM_ABERTO).isEmpty()) {
                cancelamentoParcelamento.setReferencia(cancelamentoParcelamento.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_EM_ABERTO).get(0).getReferencia());
            }
            parcelamento = processoParcelamentoFacade.cancelarProcessoParcelamento(parcelamento);
            processoParcelamentoFacade.salvarDataDePrescricaoParcelaValorDivida(parcelamento);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Async
    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    public void carregarDescontosParcelamentos() {
        processoParcelamentoFacade.cancelarDescontosParcelamentos();
        logger.debug("carregarDescontosParcelamentos terminou");
    }

    @Transactional(timeout = 70000, propagation = Propagation.REQUIRES_NEW)
    public void executarCorrecaoDosCancelamentosErrados() {
        List<BigDecimal> ids = cancelamentoParcelamentoFacade.buscarIdsCancelamentosQueNaoGerouResidual();
        for (BigDecimal id : ids) {
            CancelamentoParcelamento cancelamento = cancelamentoParcelamentoFacade.recuperar(id.longValue());
            ValorDivida valorDivida = damFacade.buscarUltimoValorDividaDoCalculo(cancelamento.getId());
            cancelamentoParcelamentoFacade.corrigirCancelamentoQuandoNaoGerouDebitoResidual(cancelamento, valorDivida);
        }
    }
}
