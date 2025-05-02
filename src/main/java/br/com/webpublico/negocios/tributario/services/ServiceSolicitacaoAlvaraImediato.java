package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.negocios.SolicitacaoAlvaraImediatoFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Service
public class ServiceSolicitacaoAlvaraImediato {

    private static final Logger logger = LoggerFactory.getLogger(ServiceSolicitacaoAlvaraImediato.class);

    private SolicitacaoAlvaraImediatoFacade solicitacaoAlvaraImediatoFacade;

    @PostConstruct
    private void init() {
        try {
            solicitacaoAlvaraImediatoFacade = (SolicitacaoAlvaraImediatoFacade) new InitialContext().lookup("java:module/SolicitacaoAlvaraImediatoFacade");
        } catch (NamingException e) {
            logger.debug("Erro ao injetar os facades no ServiceSolicitacaoAlvaraImediato.", e);
            logger.error("Erro ao injetar os facades no ServiceSolicitacaoAlvaraImediato.");
        }
    }

    public SolicitacaoAlvaraImediatoFacade getSolicitacaoAlvaraImediatoFacade() {
        return solicitacaoAlvaraImediatoFacade;
    }
}
