package br.com.webpublico.seguranca;

import br.com.webpublico.negocios.comum.dao.JdbcApiKeyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ApiKeyAuthenticationFilter extends GenericFilterBean {

    private static final String AUTH_TOKEN_HEADER_NAME = "API-KEY";
    private static final String AUTH_TOKEN = "senha10";

    @Autowired
    private JdbcApiKeyDAO apiKeyDAO;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {
        try {
            Authentication authentication = getAuthentication((HttpServletRequest) request);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (BadCredentialsException e) {
            throw e;
        }
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        if (apiKey != null) {
            if (apiKeyDAO.hasApiKeyRegistered(apiKey)) {
                return new ApiKeyAuthentication(apiKey, AuthorityUtils.createAuthorityList("ROLE_API"));
            } else {
                throw new BadCredentialsException("API Key invalida.");
            }
        }
        return null;
    }

}
