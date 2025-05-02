package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceProcessoParcelamento;
import org.springframework.beans.factory.annotation.Autowired;

public class DescontoParcelamentoJob extends WPJob {

    @Autowired
    protected ServiceProcessoParcelamento serviceProcessoParcelamento;

    @Override
    public void execute() {
        try {
            serviceProcessoParcelamento.carregarDescontosParcelamentos();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Cancelamento de Parcelamento";
    }
}
