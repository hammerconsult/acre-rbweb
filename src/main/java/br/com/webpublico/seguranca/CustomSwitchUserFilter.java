package br.com.webpublico.seguranca;

import br.com.webpublico.seguranca.service.SistemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

import javax.servlet.http.HttpServletRequest;

public class CustomSwitchUserFilter extends SwitchUserFilter {

    @Autowired
    private SistemaService sistemaService;

    protected Authentication attemptSwitchUser(HttpServletRequest request) throws AuthenticationException {
        Authentication switchTo = super.attemptSwitchUser(request);
        sistemaService.limparUsuarioCorrente();
        return switchTo;
    }

}
