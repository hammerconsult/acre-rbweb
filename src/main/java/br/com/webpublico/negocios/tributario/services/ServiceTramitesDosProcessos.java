package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.negocios.ProcessoFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
@Transactional
public class ServiceTramitesDosProcessos {

    private static final Logger logger = LoggerFactory.getLogger(ServiceTramitesDosProcessos.class.getName());
    @PersistenceContext
    protected transient EntityManager em;
    public ProcessoFacade processoFacade;

    @PostConstruct
    private void init() {
        try {
            processoFacade = (ProcessoFacade) new InitialContext().lookup("java:module/ProcessoFacade");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public void lancaNotificacoesDosPrazosDosProcessos() {
        processoFacade.lancaNotificacoesDosPrazosDosProcessos();
    }

    public void lancaNotificacoesDosProcessosNaoFinalizados(){
        processoFacade.lancaNotificacoesDosProcessosNaoFinalizados();
    }
}
