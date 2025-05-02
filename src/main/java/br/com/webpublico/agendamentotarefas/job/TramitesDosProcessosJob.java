package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceTramitesDosProcessos;
import org.springframework.beans.factory.annotation.Autowired;

public class TramitesDosProcessosJob extends WPJob {
    @Autowired
    protected ServiceTramitesDosProcessos serviceTramitesDosProcessos;


    @Override
    public void execute() {
        try {
            serviceTramitesDosProcessos.lancaNotificacoesDosPrazosDosProcessos();
            serviceTramitesDosProcessos.lancaNotificacoesDosProcessosNaoFinalizados();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Prazo dos Trâmites dos Processos";
    }
}
