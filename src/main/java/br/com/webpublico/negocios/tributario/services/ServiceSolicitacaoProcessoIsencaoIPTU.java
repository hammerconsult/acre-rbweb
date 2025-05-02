package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.negocios.ProcessoIsencaoIPTUFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class ServiceSolicitacaoProcessoIsencaoIPTU {

    private static final Logger logger = LoggerFactory.getLogger(ServiceSolicitacaoProcessoIsencaoIPTU.class.getName());
    public ProcessoIsencaoIPTUFacade  processoIsencaoIPTUFacade;
    @PersistenceContext
    protected transient EntityManager em;

    @PostConstruct
    private void init() {
        try {
            processoIsencaoIPTUFacade = (ProcessoIsencaoIPTUFacade) new InitialContext().lookup("java:module/ProcessoIsencaoIPTUFacade");
        } catch (NamingException e) {
            logger.debug(e.getMessage());
        }
    }

    public void atualizarSituacaoProcessoIsencaoIPTU() {
        processoIsencaoIPTUFacade.atualizarSituacaoProcessoIsencaoIPTU();
    }
}
