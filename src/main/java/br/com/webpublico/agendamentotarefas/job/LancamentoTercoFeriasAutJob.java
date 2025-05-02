package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.rh.administracaodepagamento.ServiceLancamentoTercoFeriasAut;
import org.springframework.beans.factory.annotation.Autowired;

public class LancamentoTercoFeriasAutJob extends WPJob {
    @Autowired
    private ServiceLancamentoTercoFeriasAut serviceNotificacao;

    @Override
    public void execute() {
        try {
            serviceNotificacao.lancarTercoFerias();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Pagamento Automático 1/3 Férias";
    }
}
