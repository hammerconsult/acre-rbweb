package br.com.webpublico.negocios.contabil.services;

import br.com.webpublico.negocios.ConfiguracaoContabilFacade;
import br.com.webpublico.negocios.PropostaConcessaoDiariaFacade;
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
public class ServiceNotificacaoDiaria {

    public static final Logger logger = LoggerFactory.getLogger(ServiceNotificacaoDiaria.class);
    @PersistenceContext
    protected transient EntityManager em;
    private PropostaConcessaoDiariaFacade propostaConcessaoDiariaFacade;
    private ConfiguracaoContabilFacade configuracaoContabilFacade;

    @PostConstruct
    private void init() {
        try {
            propostaConcessaoDiariaFacade = (PropostaConcessaoDiariaFacade) new InitialContext().lookup("java:module/PropostaConcessaoDiariaFacade");
            configuracaoContabilFacade = (ConfiguracaoContabilFacade) new InitialContext().lookup("java:module/ConfiguracaoContabilFacade");
        } catch (NamingException e) {
            logger.debug("Erro ao inicializar ServiceNotificacaoDiaria.", e.getMessage());
        }
    }

    public void notificar() {
        propostaConcessaoDiariaFacade.notificarParaControladoria(configuracaoContabilFacade.configuracaoContabilVigente());
    }

}
