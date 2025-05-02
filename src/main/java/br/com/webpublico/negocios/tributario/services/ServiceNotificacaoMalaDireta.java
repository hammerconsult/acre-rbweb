package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.negocios.MalaDiretaGeralFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Wellington on 05/02/2016.
 */
@Service
@Transactional
public class ServiceNotificacaoMalaDireta {

    private static final Logger logger = LoggerFactory.getLogger(ServiceNotificacaoMalaDireta.class.getName());
    public MalaDiretaGeralFacade malaDiretaGeralFacade;
    @PersistenceContext
    protected transient EntityManager em;

    @PostConstruct
    private void init() {
        try {
            malaDiretaGeralFacade = (MalaDiretaGeralFacade) new InitialContext().lookup("java:module/MalaDiretaGeralFacade");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public void lancar() {
        malaDiretaGeralFacade.lancarNotificacoes();
    }

}
