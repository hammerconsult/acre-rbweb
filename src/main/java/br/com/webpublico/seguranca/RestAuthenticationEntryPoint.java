package br.com.webpublico.seguranca;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by rodolfo on 30/06/17.
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        request.getSession().setAttribute("urlAnteriorLogin", request.getRequestURL().toString());
        response.sendRedirect(request.getContextPath() + "/faces/login.xhtml");
    }
}
