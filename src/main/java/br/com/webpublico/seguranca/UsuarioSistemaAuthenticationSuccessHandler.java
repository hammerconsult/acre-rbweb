/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.seguranca;

import br.com.webpublico.entidades.UsuarioNodeServer;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.seguranca.service.UsuarioNodeServerService;
import br.com.webpublico.seguranca.service.UsuarioSistemaService;
import br.com.webpublico.util.Seguranca;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Transactional(readOnly = true)
public class UsuarioSistemaAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    protected static final Logger logger = LoggerFactory.getLogger(UsuarioSistemaAuthenticationSuccessHandler.class);

    public static final Boolean PARAM_ALTERA_SENHA = true;
    public static final String KEY_ALTERA_SENHA = "KEY_PERMITE_SAIR";
    public static final int MARGEM_SEGUNDOS_MENSAGEM_WEBSOCKET = 60;
    private final String HOME = "/home";
    private final String SEM_UNIDADE = "/acesso-negado/sem-unidade-organizacional/";
    private final String LOGOUT = "/faces/login.xhtml?login_error";
    private final String ALTERA_SENHA = "/usuario/alterar-senha";
    @Autowired
    private SistemaService sistemaService;
    @Autowired
    private SingletonRecursosSistema singletonRecursosSistema;
    @Autowired
    private UsuarioNodeServerService usuarioNodeServerService;
    @Autowired
    private UsuarioSistemaService usuarioSistemaService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication a) throws IOException, ServletException {
        UsuarioSistema usuarioSistema = singletonRecursosSistema.getUsuarioPorCpf(((UsuarioSistema) a.getPrincipal()).getCpf());
        String redirect = request.getContextPath() + HOME;
        if (usuarioSistema != null) {
            if (usuarioSistema.loginIgualSenha(Seguranca.md5(usuarioSistema.getCpf()))) {
                redirect = request.getContextPath() + ALTERA_SENHA;
                request.getSession().setAttribute(KEY_ALTERA_SENHA, PARAM_ALTERA_SENHA);
                response.sendRedirect(redirect);
                return;
            }
            if (usuarioSistema.getExpira() != null) {
                if (usuarioSistema.getExpira()) {
                    redirect = request.getContextPath() + LOGOUT;
                }
            }
        }
        try {
            sistemaService.limparTodasDependenciasDoUsuario(usuarioSistema);
        } catch (Exception e) {
            redirect = request.getContextPath() + SEM_UNIDADE;
        }

        if (!redirect.equals(request.getContextPath() + LOGOUT) || !redirect.equals(request.getContextPath() + SEM_UNIDADE)) {
            atualizarHorarioUltimoAcesso(usuarioSistema);
        }

        gravarLoginDoUsuario(usuarioSistema);
        String recursoSistema = singletonRecursosSistema.getUltimaPaginaUsuario().get(usuarioSistema);
        String urlAnteriorLogin = recursoSistema != null ? recursoSistema : (String) request.getSession().getAttribute("urlAnteriorLogin");
        HttpSession session = request.getSession(false);
        definirTempoMaximoInativo(session, usuarioSistema);
        List<String> urlsPermitidas = Arrays.asList("novo", "editar", "listar", "ver");
        if (!Strings.isNullOrEmpty(urlAnteriorLogin) && !urlAnteriorLogin.contains("erro500")
            && urlsPermitidas.stream().anyMatch(urlAnteriorLogin::contains)) {
            response.sendRedirect(urlAnteriorLogin);
            request.getSession().setAttribute("urlAnteriorLogin", null);
        } else {
            response.sendRedirect(redirect);
        }

    }

    /*
        MARGEM_SEGUNDOS_MENSAGEM_WEBSOCKET: tempo em segundos de folga para conseguir enviar mensagem 'logout' pelo websocket (antes de encerrar a sessão)
        e redirecionar o usuário para tela de login quando atingir tempo máximo de inatividade. Se igual a 0 pode não conseguir redirecionar.
    */
    private void definirTempoMaximoInativo(HttpSession session,  UsuarioSistema usuarioSistema) {
        if (session != null) {
            session.setMaxInactiveInterval((usuarioSistema.getTempoMaximoInativoMinutos() * 60) + MARGEM_SEGUNDOS_MENSAGEM_WEBSOCKET);
        }
    }

    public void gravarLoginDoUsuario(UsuarioSistema usuarioSistema) {
        try {
            UsuarioNodeServer uns = usuarioNodeServerService.recuperaUsuarioNode(usuarioSistema);
            if (uns == null) {
                uns = novoUsuarioNodeServer(usuarioSistema);
            } else {
                uns.setDataHoraLogin(sistemaService.getDataOperacao());
                uns.setNodeServer(System.getProperty("jboss.server.name"));

            }
            usuarioNodeServerService.salvaUsuarioNodeServer(uns);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public UsuarioNodeServer novoUsuarioNodeServer(UsuarioSistema us) {
        UsuarioNodeServer usuario = new UsuarioNodeServer();
        usuario.setNodeServer(System.getProperty("jboss.server.name"));
        usuario.setDataHoraLogin(sistemaService.getDataOperacao());
        usuario.setUsuario(us);
        return usuario;

    }

    private void atualizarHorarioUltimoAcesso(UsuarioSistema usuarioSistema) {
        try {
            usuarioSistema.setUltimoAcesso(new Date());
            usuarioSistemaService.save(usuarioSistema);
        }catch (Exception e){
            Util.loggingError(this.getClass(), "Não foi possível salvar o último acesso do usuário", e);
        }
    }
}
