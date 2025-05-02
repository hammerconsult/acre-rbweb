package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.rh.AtualizacaoDadosRBPontoService;
import org.springframework.beans.factory.annotation.Autowired;

public class ReprocessamentoAtualizacaoDadosRBPontoJob extends WPJob {

    @Autowired
    private AtualizacaoDadosRBPontoService atualizacaoDadosRBPontoService;

    @Override
    public void execute() {
        atualizacaoDadosRBPontoService.reprocessarIntegradosComErro();
    }

    @Override
    public String toString() {
        return "Reprocessamento das Atualizações de Dados do RB Ponto que falharam";
    }
}
