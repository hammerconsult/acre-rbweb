package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceSolicitacaoAlvaraImediato;
import org.springframework.beans.factory.annotation.Autowired;

public class EnviarAlvaraImediatoPorEmailJob extends WPJob {

    @Autowired
    private ServiceSolicitacaoAlvaraImediato serviceSolicitacaoAlvaraImediato;

    @Override
    public void execute() {
        try {
            serviceSolicitacaoAlvaraImediato.getSolicitacaoAlvaraImediatoFacade().enviarAlvarasImediatoPorEmail();
        } catch (Exception e) {
            logger.debug("Erro no job EnvioAlvaraImediatoJob.", e);
            logger.error("Erro no job EnvioAlvaraImediatoJob.");
        }
    }

    @Override
    public String toString() {
        return "Enviar Alvará(s) Imediato por E-mail";
    }
}
