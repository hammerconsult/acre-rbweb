package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceArquivoBI;
import org.springframework.beans.factory.annotation.Autowired;

public class ArquivoBIDebitoGeralTributarioJob extends WPJob {

    @Autowired
    protected ServiceArquivoBI serviceArquivoBI;

    @Override
    public void execute() {
        try {
            serviceArquivoBI.enviarArquivoDebitosGeraisTributarios();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Envio do Arquivo Debitos Gerais Tributários para o BI";
    }
}
