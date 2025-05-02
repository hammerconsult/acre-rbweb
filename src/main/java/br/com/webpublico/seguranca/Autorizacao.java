package br.com.webpublico.seguranca;

import br.com.webpublico.entidades.RecursoSistema;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.Direito;
import br.com.webpublico.seguranca.service.RecursoSistemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Component
@Transactional(readOnly = true)
public class Autorizacao {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RecursoSistemaService recursoSistemaService;
    @Autowired
    private SingletonRecursosSistema singletonRecursosSistema;

    public boolean isPodeEditar(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return isPodeEditar(request, authentication);
    }

    public boolean isPodeExcluir(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return isPodeExcluir(request, authentication);
    }

    public boolean isPodeEditar(HttpServletRequest request, Authentication authentication) {
        return isPossuiDireito(request, authentication, Direito.ESCRITA);
    }

    public boolean isPodeExcluir(HttpServletRequest request, Authentication authentication) {
        return isPossuiDireito(request, authentication, Direito.EXCLUSAO);
    }

    private boolean isPossuiDireito(HttpServletRequest request, Authentication authentication, Direito direito) {
        UsuarioSistema usuarioSistema = (UsuarioSistema) authentication.getPrincipal();
        usuarioSistema = singletonRecursosSistema.getUsuarioPorCpf(usuarioSistema.getCpf());
        String uri = extractUri(request);
        uri = extractFaces(uri);
        if (!uri.endsWith(".xhtml")) {
            return true;
        }
        RecursoSistema recursoSistema = recursoSistemaService.findByUri(uri);
        if (recursoSistema == null) {
            return false;
        }

        if (direito.equals(Direito.LEITURA)) {
            return singletonRecursosSistema.hasRecursoParaUsuario(recursoSistema, usuarioSistema);
        }
        if (direito.equals(Direito.ESCRITA)) {
            return singletonRecursosSistema.hasPermissaoEditar(recursoSistema, usuarioSistema);
        }
        if (direito.equals(Direito.EXCLUSAO)) {
            return singletonRecursosSistema.hasPermissaoExcluir(recursoSistema, usuarioSistema);
        }

        return false;
    }

    private String extractFaces(String uri) {
        return uri.replaceAll("/faces/", "/");
    }

    private String extractUri(HttpServletRequest request) {
        if (request.getPathInfo() != null) {
            return String.format("%s%s", request.getServletPath(), request.getPathInfo());
        } else {
            return request.getServletPath();
        }
    }
}
