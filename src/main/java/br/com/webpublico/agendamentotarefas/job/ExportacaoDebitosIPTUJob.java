package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceExportacaoDebitosIPTU;
import org.springframework.beans.factory.annotation.Autowired;

public class ExportacaoDebitosIPTUJob extends WPJob {

    @Autowired
    private ServiceExportacaoDebitosIPTU serviceExportacaoDebitosIPTU;

    @Override
    public void execute() {
        serviceExportacaoDebitosIPTU.gerarArquivoDeExportacao(null);
    }
}
