package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.VencimentoContratoService;
import org.springframework.beans.factory.annotation.Autowired;

public class AvisoVencimentoContrato extends WPJob {

    @Autowired
    protected VencimentoContratoService vencimentoContratoService;

    @Override
    public void execute() {
        vencimentoContratoService.getContratosParaNotificacoes();
    }

    @Override
    public String toString() {
        return "Aviso de vencimento de contratos";
    }
}
