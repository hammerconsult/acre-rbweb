package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.controle.rh.creditodesalario.RotinaAgendadaRHSingleton;
import br.com.webpublico.negocios.RemessaProtestoFacade;
import br.com.webpublico.negocios.tributario.executores.RetornoRemessaProtestoExecutor;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;

@Singleton
public class SingletonRetornoRemessaProtesto {

    private static final Logger logger = LoggerFactory.getLogger(SingletonRetornoRemessaProtesto.class);
    @Lock(LockType.READ)
    @Schedule(hour = "*/1")
    public void consultarRetornoRemessa() {
        logger.info("Consultando Retorno da Remessa...");
        RemessaProtestoFacade remessaFacade = (RemessaProtestoFacade) Util.getFacadeViaLookup("java:module/RemessaProtestoFacade");
        RetornoRemessaProtestoExecutor.build(remessaFacade).executeFutureWithoutResult();
    }
}
