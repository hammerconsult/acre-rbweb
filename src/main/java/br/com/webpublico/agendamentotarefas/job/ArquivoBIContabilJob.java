package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceArquivoBI;
import org.springframework.beans.factory.annotation.Autowired;

public class ArquivoBIContabilJob extends WPJob {

    @Autowired
    protected ServiceArquivoBI serviceArquivoBI;

    @Override
    public void execute() {
        try {
            serviceArquivoBI.enviarArquivosContabeis();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Envio dos Arquivos Contábeis para o BI";
    }
}
