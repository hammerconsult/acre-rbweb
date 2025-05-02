package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.negocios.NotificacaoCobrancaAdministrativaFacade;
import br.com.webpublico.seguranca.service.SistemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

/**
 * @author octavio
 */
@Service
public class ServiceNotificacaoAvisoCobrancaAdministrativa {
    private static final Logger logger = LoggerFactory.getLogger(ServiceNotificacaoAvisoCobrancaAdministrativa.class.getName());
    public NotificacaoCobrancaAdministrativaFacade notificacaoCobrancaAdmFacade;

    @PersistenceContext
    protected transient EntityManager em;

    @PostConstruct
    private void init() {
        try {
            notificacaoCobrancaAdmFacade = (NotificacaoCobrancaAdministrativaFacade) new InitialContext().lookup("java:module/NotificacaoCobrancaAdministrativaFacade");
        } catch (NamingException ne) {
            logger.debug(ne.getMessage());
        }
    }

    public void lancarNotificacoesCobrancaAdministrativa(Date dataLogada) {
        notificacaoCobrancaAdmFacade.buscarAvisosAndNotificacoesAdministrativasComPrazoVencido(dataLogada);
    }

}
