package br.com.webpublico.negocios.contabil.services;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.negocios.SistemaFacade;
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
 * Created by renat on 30/08/2016.
 */
@Service
@Transactional
public class ServiceIntegracaoPortalTransparencia {

    public static final Logger logger = LoggerFactory.getLogger(ServiceIntegracaoPortalTransparencia.class);
    @PersistenceContext
    protected transient EntityManager em;
    public PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;

    @PostConstruct
    private void init() {
        try {
            portalTransparenciaNovoFacade = (PortalTransparenciaNovoFacade) new InitialContext().lookup("java:module/PortalTransparenciaNovoFacade");
        } catch (NamingException e) {
            logger.debug("Erro ao inicializar portalTransparenciaNovoFacade.", e.getMessage());
        }
    }

    public void sincronizar() {
        SistemaFacade sistemaFacade = portalTransparenciaNovoFacade.getSistemaFacade();
        portalTransparenciaNovoFacade.sincronizarLancamento(sistemaFacade.getPerfilAplicacao());
    }
}
