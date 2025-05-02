package br.com.webpublico.ws.portalweb;

import br.com.webpublico.seguranca.service.SistemaService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

public abstract class WSPortal {

    public final String PORTAL_WEB = "Portal Web";

    protected void definirUsuarioAlternativo(String nome) {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        SistemaService sistemaService = (SistemaService) ap.getBean("sistemaService");
        sistemaService.setUsuarioAlternativo(nome);
    }

    protected void definirUsuarioAlternativo() {
        definirUsuarioAlternativo(PORTAL_WEB);
    }

}
