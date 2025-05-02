package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.negocios.RelacaoDebitosAPrescreverFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author octavio
 * Date : 26/04/2019
 */
@Service
public class ServiceNotificacaoDebitosAPrescrever {

    private static final Logger logger = LoggerFactory.getLogger(ServiceNotificacaoDebitosAPrescrever.class.getName());
    public RelacaoDebitosAPrescreverFacade relacaoDebitosAPrescreverFacade;
    @PersistenceContext
    protected transient EntityManager em;

    @PostConstruct
    private void init() {
        try {
            relacaoDebitosAPrescreverFacade = (RelacaoDebitosAPrescreverFacade) new InitialContext().lookup("java:module/RelacaoDebitosAPrescreverFacade");
        } catch (NamingException e) {
            logger.debug(e.getMessage());
        }
    }

    public void lancarNotificacoesDeDebitosAPrescrever() {
        relacaoDebitosAPrescreverFacade.lancarNotificacaoDebitosAPrescrever();
    }
}
