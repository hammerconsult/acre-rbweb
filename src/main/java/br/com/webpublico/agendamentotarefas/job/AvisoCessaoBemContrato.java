package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.VencimentoCessaoBemService;
import org.springframework.beans.factory.annotation.Autowired;

public class AvisoCessaoBemContrato extends WPJob {

    @Autowired
    protected VencimentoCessaoBemService vencimentoCessaoBemService;

    @Override
    public void execute() {
        vencimentoCessaoBemService.gerarNotificacoesDasCessoes();
    }

    @Override
    public String toString() {
        return "Aviso de vencimento de cessão de bens";
    }
}
