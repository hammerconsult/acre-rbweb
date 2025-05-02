package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.esocial.service.RegistroESocialService;
import org.springframework.beans.factory.annotation.Autowired;

public class AtualizarRegistrosESocialJob extends WPJob {

    @Autowired
    protected RegistroESocialService registroESocialService;

    @Override
    public void execute() {
    }

    @Override
    public String toString() {
        return "Atualizando registros ESocial";
    }
}
