package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceAtualizarCacheValoresParcelas;
import org.springframework.beans.factory.annotation.Autowired;

public class AtualizarCacheValoresParcelasJob extends WPJob {

    @Autowired
    protected ServiceAtualizarCacheValoresParcelas serviceAtualizarCacheValoresParcelas;

    @Override
    public void execute() {
        try {
            serviceAtualizarCacheValoresParcelas.atualizarViaMaioresDevedores();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Integração com o sistema PGM.net";
    }
}
