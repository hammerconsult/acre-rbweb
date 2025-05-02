package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.RequisicaoMaterialNaoAtendidaService;
import org.springframework.beans.factory.annotation.Autowired;

public class RequisicaoMaterialNaoAtendidaJob extends WPJob {

    @Autowired
    private RequisicaoMaterialNaoAtendidaService service;


    @Override
    public void execute() {
        service.lancarNotificacao();
    }
}
