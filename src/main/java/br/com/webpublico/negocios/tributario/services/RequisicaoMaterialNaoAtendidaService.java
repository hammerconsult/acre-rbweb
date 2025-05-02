package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.negocios.RequisicaoMaterialFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class RequisicaoMaterialNaoAtendidaService {

    private static final Logger logger = LoggerFactory.getLogger(RequisicaoMaterialNaoAtendidaService.class.getName());
    public RequisicaoMaterialFacade requisicaoMaterialFacade;
    @PersistenceContext
    protected transient EntityManager em;

    @PostConstruct
    private void init() {
        try {
            requisicaoMaterialFacade = (RequisicaoMaterialFacade) new InitialContext().lookup("java:module/RequisicaoMaterialFacade");
        } catch (NamingException e) {
            logger.debug(e.getMessage());
        }
    }

    public void lancarNotificacao() {
        requisicaoMaterialFacade.lancarNotificacaoRequisicaoNaoAtentida();
    }
}
