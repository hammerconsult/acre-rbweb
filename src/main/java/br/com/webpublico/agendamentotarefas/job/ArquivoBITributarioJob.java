package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceArquivoBI;
import br.com.webpublico.negocios.tributario.services.ServiceIntegracaoSofPlan;
import org.springframework.beans.factory.annotation.Autowired;

public class ArquivoBITributarioJob extends WPJob {

    @Autowired
    protected ServiceArquivoBI serviceArquivoBI;

    @Override
    public void execute() {
        try {
            serviceArquivoBI.enviarArquivosTributarios();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Envio dos Arquivos Tributários para o BI";
    }
}
