package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.negocios.ParecerProcIsencaoIPTUFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Service
public class ServiceNotificacaoSolicitacaoIsencoesIPTU {
    private static final Logger logger = LoggerFactory.getLogger(ServiceNotificacaoSolicitacaoIsencoesIPTU.class.getName());
    private ParecerProcIsencaoIPTUFacade parecerFacade;

    @PostConstruct
    private void init() {
        try {
            parecerFacade = (ParecerProcIsencaoIPTUFacade) new InitialContext().lookup("java:module/ParecerProcIsencaoIPTUFacade");
        } catch (NamingException e) {
            logger.debug(e.getMessage());
        }
    }

    public void notificarSolicitacoesDeIsencaoEmAberto() {
        parecerFacade.notificarSolicitacoesDeIsencaoEmAberto();
    }
}
