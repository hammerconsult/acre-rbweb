package br.com.webpublico.negocios.tributario.executores;

import br.com.webpublico.nfse.domain.ConfiguracaoNfse;
import br.com.webpublico.nfse.facades.ConfiguracaoNfseFacade;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executors;

public class ClearCacheNfseExecutor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ClearCacheNfseExecutor.class);

    private RestTemplate restTemplate;
    private ConfiguracaoNfseFacade configuracaoNfseFacade;
    private String completeUrl;

    public ClearCacheNfseExecutor(String completeUrl) {
        this.configuracaoNfseFacade = (ConfiguracaoNfseFacade) Util.getFacadeViaLookup("java:module/ConfiguracaoNfseFacade");
        this.restTemplate = new RestTemplate();
        this.completeUrl = completeUrl;
    }

    @Override
    public void run() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ConfiguracaoNfse configuracaoNfse = configuracaoNfseFacade.recuperarConfiguracao();
            if (configuracaoNfse.getUrlRest() != null) {
                restTemplate.delete(configuracaoNfse.getUrlRest() +
                    completeUrl);
            }
        } catch (Exception e) {
            logger.error("Não foi possível remover o cadastro no sistema de nota fiscal: ", e);
        }
    }

    public void execute() {
        Executors.newSingleThreadExecutor().execute(this);
    }

    public static ClearCacheNfseExecutor buid(String completeUrl) {
        return new ClearCacheNfseExecutor(completeUrl);
    }
}
