package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.ProcessoParcelamentoFacade;
import br.com.webpublico.negocios.tributario.services.ServiceProcessoParcelamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class CancelamentoParcelamentoJob extends WPJob {

    private static final int QTD = 300;

    @Autowired
    protected ServiceProcessoParcelamento serviceProcessoParcelamento;

    @Override
    public void execute() {
        List<ProcessoParcelamentoFacade.ParcelamentoCancelar> idsParcelamentos =
            serviceProcessoParcelamento.buscarProcessosParcelamentoParaCancelamentoAutomatico(0, QTD);
        logger.debug("Cancelamento automático de parcelamento, buscou: " + idsParcelamentos.size());
        if (!idsParcelamentos.isEmpty()) {
            processarParcelamentos(idsParcelamentos, QTD);
        }
        serviceProcessoParcelamento.executarCorrecaoDosCancelamentosErrados();
    }

    @Transactional(timeout = 70000, propagation = Propagation.REQUIRES_NEW)
    public void processarParcelamentos(List<ProcessoParcelamentoFacade.ParcelamentoCancelar> idsParcelamentos, int inicio) {
        for (ProcessoParcelamentoFacade.ParcelamentoCancelar parcelamentoCancelar : idsParcelamentos) {
            try {
                logger.debug("Cancelando automaticamente o parcelamento com id: " + parcelamentoCancelar.getId());
                serviceProcessoParcelamento.cancelarParcelamentos(parcelamentoCancelar);
            } catch (Exception e) {
                logger.error("Erro ao cancelar o parcelamento com id: {}. Motivo: {}.", parcelamentoCancelar.getId(), e.getMessage());
                logger.debug("Detalhes do erro ao cancelar o parcelamento com id: {}.", parcelamentoCancelar.getId(), e);
            }
        }
        idsParcelamentos = serviceProcessoParcelamento.buscarProcessosParcelamentoParaCancelamentoAutomatico(inicio, QTD);
        if (!idsParcelamentos.isEmpty()) {
            processarParcelamentos(idsParcelamentos, inicio + QTD);
        }
    }

    @Override
    public String toString() {
        return "Cancelamento de Parcelamento";
    }
}
