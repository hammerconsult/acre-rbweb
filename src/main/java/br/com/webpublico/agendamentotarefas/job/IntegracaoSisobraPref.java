package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.IntegracaoSisobraPrefService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class IntegracaoSisobraPref extends WPJob {

    private static final Logger logger = LoggerFactory.getLogger(IntegracaoSisobraPref.class);

    @Autowired
    private IntegracaoSisobraPrefService service;

    @Override
    public void execute() {
        try {
            service.consultarAndProcessarIntegracaoSisObraPref();
        } catch (Exception e) {
            logger.error("Erro ao executar o método consultarAndProcessarIntegracaoSisObraPref {}", e);
        }
    }

    @Override
    public String toString() {
        return "Integração SisObra Pref";
    }
}
