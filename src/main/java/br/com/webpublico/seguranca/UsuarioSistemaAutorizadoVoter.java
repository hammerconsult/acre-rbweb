package br.com.webpublico.seguranca;

import br.com.webpublico.entidades.*;
import br.com.webpublico.seguranca.service.RecursoSistemaService;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.Seguranca;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.PrettyConfigurator;
import com.ocpsoft.pretty.faces.url.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class UsuarioSistemaAutorizadoVoter implements AccessDecisionVoter<Object> {

    private static final String INDEX = "/index.xhtml";
    private static final String DOTXHTML = ".xhtml";
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private static final HashSet urlsDiponiveis = Sets.newHashSet(
        "/tributario/emDesenvolvimento.xhtml",
        "/troca.xhtml",
        INDEX,
        "/erro500.xhtml",
        "/alterasenha.xhtml",
        "/admin/controleusuario/seguranca/semunidade.xhtml",
        "/admin/controleusuario/seguranca/sessaoexpirou.xhtml",
        "/admin/notificacao/portipo.xhtml");

    @Autowired
    RecursoSistemaService recursoSistemaService;
    @Autowired
    SistemaService sistemaService;
    PrettyContext ctx;
    @Autowired
    SingletonRecursosSistema singletonRecursosSistema;
    @Autowired
    MensagemTodosUsuariosService mensagemTodosUsuariosService;

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    @Transactional(readOnly = true)
    @Override
    public int vote(Authentication authentication, Object url, Collection<ConfigAttribute> attributes) {
        if (notIsFilterInvocations(url)) return ACCESS_DENIED;
        if (notIsUsuarioSistema(authentication)) return ACCESS_GRANTED;
        String uri = extractUri((FilterInvocation) url);
        if (notIsXhtml(uri)) return ACCESS_GRANTED;
        UsuarioSistema usuarioSistema = getUsuarioSistema(authentication);
        guardarUltimoAcesso((FilterInvocation) url, usuarioSistema);
        if (isAdmin(usuarioSistema)) return ACCESS_GRANTED;
        if (isUrlDisponivelParaTodos(uri)) return ACCESS_GRANTED;
        if (isSenhaLoginIguais(usuarioSistema)) return ACCESS_DENIED;
        if (hasMensagemBloqueioAcesso()) return ACCESS_DENIED;
        RecursoSistema recursoSistema = singletonRecursosSistema.getRecursoPorUri(uri);
        if (hasRecursoParaUsuario(recursoSistema, usuarioSistema)) return voteFases(recursoSistema);

        return ACCESS_DENIED;
    }

    private void guardarUltimoAcesso(FilterInvocation url, UsuarioSistema usuarioSistema) {
        singletonRecursosSistema.guardarUltimoAcesso(usuarioSistema, LocalDateTime.now(), url.getRequestUrl());
    }

    private boolean hasRecursoParaUsuario(RecursoSistema recursoSistema, UsuarioSistema usuarioSistema) {
        return singletonRecursosSistema.hasRecursoParaUsuario(recursoSistema, usuarioSistema);
    }

    private UsuarioSistema getUsuarioSistema(Authentication authentication) {
        UsuarioSistema usuarioSistema = singletonRecursosSistema
            .getUsuarioPorCpf(((UsuarioSistema) authentication.getPrincipal()).getCpf());
        return usuarioSistema;
    }

    private boolean hasMensagemBloqueioAcesso() {
        return mensagemTodosUsuariosService.getMensagens().stream().anyMatch(MensagemUsuario::getBloqueiaAcesso);
    }

    private static boolean isAdmin(UsuarioSistema usuarioSistema) {
        return usuarioSistema.isAdmin();
    }

    private boolean notIsXhtml(String uri) {
        return !isXhtml(uri);
    }

    private static boolean notIsUsuarioSistema(Authentication authentication) {
        return !(authentication.getPrincipal() instanceof UsuarioSistema);
    }

    private static boolean notIsFilterInvocations(Object url) {
        return !(url instanceof FilterInvocation);
    }

    private boolean isSenhaLoginIguais(UsuarioSistema usuarioSistema) {
        return usuarioSistema.loginIgualSenha(Seguranca.md5(usuarioSistema.getCpf()));
    }

    private boolean isXhtml(String uri) {
        return (uri.endsWith(DOTXHTML));
    }

    private boolean isUrlDisponivelParaTodos(String uri) {
        return urlsDiponiveis.contains(uri);
    }

    private int voteFases(RecursoSistema recurso) {
        List<Fase> lf = recursoSistemaService.listaFasesRecurso(recurso);
        if (lf.isEmpty()) {
            return ACCESS_GRANTED;
        }
        sistemaService.atribuirFaseBloqueando(lf.get(0).toString());
        List<PeriodoFaseUnidade> pfu = Lists.newArrayList();
        try {
            pfu = recursoSistemaService.listaPeriodoFaseUnidades(recurso);
        } catch (Exception e) {
            logger.error("Não foi possível definir o voteFases ", e);
        }
        if (pfu == null || pfu.isEmpty()) {
            return ACCESS_DENIED;
        }
        return ACCESS_GRANTED;
    }

    private String extractUri(FilterInvocation filterInvocation) {
        return extractUri(filterInvocation.getHttpRequest()).replaceAll("/faces/", "/");
    }

    private String extractUri(HttpServletRequest request) {
        String uri = request.getServletPath();
        if (request.getPathInfo() != null) {
            return String.format("%s%s", request.getServletPath(), request.getPathInfo());
        } else {
            URL url = new URL(uri);
            if (ctx == null) {
                ctx = PrettyContext.getCurrentInstance(request);
            }
            if (ctx.getConfig().getMappings().isEmpty()) {
                initPrettyFacesFromServlet(request);
            }
            if (ctx.getConfig().isURLMapped(url)) {
                return getViewID(url);
            }
            return request.getServletPath();
        }
    }

    public String getViewID(URL url) {
        return ctx.getConfig().getMappingForUrl(url).getViewId();
    }

    public void initPrettyFacesFromServlet(HttpServletRequest request) {
        PrettyConfigurator pc = new PrettyConfigurator(request.getServletContext());
        pc.configure();
        ctx.getConfig().setMappings(pc.getConfig().getMappings());
    }
}
