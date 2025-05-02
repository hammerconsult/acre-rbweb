/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.HierarquiaOrganizacionalDTO;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonArquivosInterface;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.tratamentoerros.BuscaCausa;
import com.google.common.base.Strings;
import org.hibernate.engine.spi.SessionImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.ContextLoader;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * @author webpublico
 */
@Stateless//NÂO MUDE SEM CONSULTAR TODOS OS TIMES, PLISSS.
public class SistemaFacade {

    private static final Logger logger = LoggerFactory.getLogger(SistemaFacade.class);
    public static ThreadLocal<String> threadLocalUsuario = new ThreadLocal<>();
    private static final String ENV_PERFIL_APP = System.getenv("PERFIL_APP");
    public static PerfilApp PERFIL_APP = ENV_PERFIL_APP != null ?
        PerfilApp.valueOf(ENV_PERFIL_APP) : PerfilApp.DEV;
    public static boolean logar = true;
    public static String SEM_LOGIN = "WebPúblico (Integração de Dados)";
    public static String LOGIN_PORTAL = "Portal Web";
    private static String bancoAtual = "";
    private static String usuarioDB = "";
    private static String urlBanco = "";
    @Resource
    SessionContext myContext;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    private SistemaService sistemaService;
    private NotificacaoService notificacaoService;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private SingletonArquivosInterface singletonArquivosInterface;
    private static Map<Thread, SistemaService> servicesPorThread = Collections.synchronizedMap(new HashMap<Thread, SistemaService>());

    public static void registrarSistemaServiceParaThread(SistemaService sistemaService) {
        synchronized (servicesPorThread) {
            logger.debug("Quantidade de Threads" + servicesPorThread.size());
            servicesPorThread.put(Thread.currentThread(), sistemaService);
        }
    }

    public static SistemaService buscarSistemaServiceParaThread() {
        synchronized (servicesPorThread) {
            if (servicesPorThread.containsKey(Thread.currentThread())) {
                return servicesPorThread.get(Thread.currentThread());
            }
            return null;
        }
    }

    public static void removerSistemaServiceParaThread() {
        synchronized (servicesPorThread) {
            servicesPorThread.remove(Thread.currentThread());
        }
    }

    public static Date getDataCorrente() {
        return new Date();
    }

    public static String obtemLogin() {
        try {
            String usuarioLogado = SistemaFacade.threadLocalUsuario.get();
            if (!Strings.isNullOrEmpty(usuarioLogado)) {
                return usuarioLogado;
            }
            UsuarioSistema user = (UsuarioSistema) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return user.getLogin();
        } catch (Exception ex) {
            logger.trace("Não foi possível recuperar o bean através de uma chamada assíncrona.");
        }
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        try {
            SistemaService sistemaService = (SistemaService) ap.getBean("sistemaService");
            if (sistemaService != null && sistemaService.getUsuarioAlternativo() != null) {
                return sistemaService.getUsuarioAlternativo();
            }
        } catch (Exception ex) {
            logger.trace("Não foi possível recuperar o bean através de uma chamada assíncrona.");
        }
        SistemaService sistemaService = buscarSistemaServiceParaThread();
        if (sistemaService != null && sistemaService.getUsuarioAlternativo() != null) {
            return sistemaService.getUsuarioAlternativo();
        }
        String login = SEM_LOGIN;
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            Principal principal = facesContext.getExternalContext().getUserPrincipal();
            if (principal != null) {
                login = principal.getName();
            }
        } catch (Exception ex) {
            if (logar) {
//                //System.out.println("Impossível determinar o Usuário");
            }
        }
        return login;
    }

    public static String obtemIp() {
        String ip = "Local";
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            ip = httpServletRequest.getRemoteAddr();
        } catch (Exception ex) {
            if (logar) {
//                //System.out.println("Impossível determinar o IP");
            }
            SistemaService sistemaService = buscarSistemaServiceParaThread();
            if (sistemaService != null && !Strings.isNullOrEmpty(sistemaService.getIpAlternativo())) {
                return sistemaService.getIpAlternativo();
            }
        }
        return ip;
    }

    public static String getBancoDeDadosAtual() {
        if (bancoAtual.isEmpty()) {
            Connection conexao = null;
            try {
                conexao = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
                bancoAtual = conexao.getMetaData().getDatabaseProductName();
            } catch (SQLException ex) {
                logger.error("Erro:", ex);
            } finally {
                if (conexao != null) {
                    try {
                        conexao.close();
                    } catch (SQLException ex) {
                        logger.error("Erro:", ex);
                    }
                }
            }
        }
        return bancoAtual;
    }

    public String getUsuarioBancoDeDados() {
        if (usuarioDB.isEmpty()) {
            try {

                SessionImplementor si = (SessionImplementor) em.getDelegate();
                DatabaseMetaData metaData = si.connection().getMetaData();
                usuarioDB = metaData.getUserName();
            } catch (Exception ex) {
                usuarioDB = "Não Encontrado";
            }
        }
        return usuarioDB;
    }

    public String getURLBanco() {
        if (urlBanco.isEmpty()) {
            try {
                SessionImplementor si = (SessionImplementor) em.getDelegate();
                DatabaseMetaData metaData = si.connection().getMetaData();
                urlBanco = metaData.getURL();
            } catch (Exception ex) {
                urlBanco = "Não Encontrado";
            }
        }
        return urlBanco;
    }

    public Object findById(Object obj) {
        return em.find(obj.getClass(), Persistencia.getId(obj));
    }

    public String getLogin() {
        return obtemLogin();
    }

    public String getIp() {
        return obtemIp();
    }

    public UsuarioSistema getUsuarioCorrente() {
        try {
            return getSistemaService().getUsuarioCorrente();
        } catch (Exception e) {
            return null;
        }
    }

    public String getUsuarioDB() {
        return getUsuarioBancoDeDados();
    }

    public String getURL() {
        return getUsuarioBancoDeDados();
    }

    public UsuarioSistema recuperaUsuarioPorLogin(String login) {
        Query q = em.createQuery(" from UsuarioSistema usuario "
            + " where usuario.login = :login ");
        q.setParameter("login", login.trim());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            UsuarioSistema user = (UsuarioSistema) q.getSingleResult();
//            user.getUnidadeOrganizacional().size();
            user.getUsuarioUnidadeOrganizacional().size();
            return user;
        } else {
            return null;
        }
    }


    public Exercicio getExercicioCorrente() {
        Exercicio exercicio = null;
        try {
            exercicio = getSistemaService().getExercicioCorrente();
            if (exercicio == null) {
                Calendar c = GregorianCalendar.getInstance();
                getSistemaService().trocarExercicio(exercicioFacade.getExercicioPorAno(c.get(Calendar.YEAR)));
                exercicio = getSistemaService().getExercicioCorrente();
            }
            return exercicio;
        } catch (Exception e) {
        }
        try {
            Calendar c = GregorianCalendar.getInstance();
            return exercicioFacade.getExercicioPorAno(c.get(Calendar.YEAR));
        } catch (Exception e) {
            logger.debug("Impossível recuperar exercício corrente.", e);
            logger.error("Impossível recuperar exercício corrente.");
            return null;
        }
    }

    public void setExercicioCorrente(Exercicio exercicioCorrente) {
        getSistemaService().trocarExercicio(exercicioCorrente);
    }

    public String getMunicipio() {
        return "Rio Branco - AC";
    }

    public ExceptionLog geraExceptionLog(ExceptionLog exceptionLog) {
        return em.merge(exceptionLog);
    }

    public Date getDataOperacao() {
        return getSistemaService().getDataOperacao();
    }

    public void setDataOperacao(Date dataOperacao) {
        getSistemaService().setDataOperacao(dataOperacao);
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalOrcamentoCorrente() {
        return getSistemaService().getOrcamentariaCorrente();
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdministrativaCorrente() {
        return getSistemaService().getAdministrativaCorrente();
    }

    public List<HierarquiaOrganizacional> getTodasHierarquiaOrcamentariasDaAdminstrativa() {
        return getSistemaService().getTodasHierarquiaOrcamentariasDaAdminstrativa();
    }

    public List<HierarquiaOrganizacionalDTO> getUnidadesOrcamentariasDoUsuario() {
        return getSistemaService().getOrcamentarias();
    }

    public SistemaService getSistemaService() {
        if (sistemaService == null) {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            sistemaService = (SistemaService) ap.getBean("sistemaService");
        }
        return sistemaService;
    }

    public NotificacaoService getNotificacaoService() {
        if (notificacaoService == null) {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            notificacaoService = (NotificacaoService) ap.getBean("notificacaoService");
        }
        return notificacaoService;
    }

    public PerfilApp getPerfilAplicacao() {
        return PERFIL_APP;
    }

    public ConfiguracaoPerfilApp getConfiguracaoPerfilApp() {
        return singletonArquivosInterface.getConfiguracaoPerfil();
    }

    public boolean isPerfilDev() {
        return PerfilApp.DEV.equals(getPerfilAplicacao());
    }

    public boolean isPerfilProd() {
        return PerfilApp.PROD.equals(getPerfilAplicacao());
    }

    public ExceptionLog salvarExcecao(Exception t) {
        UsuarioSistema usuarioCorrente = getUsuarioCorrente();
        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setDataRegistro(new Date());
        exceptionLog.setIp(getIp());
        exceptionLog.setStackTrace(Util.getStackTraceDaException(t));
        exceptionLog.setTipoException(BuscaCausa.desenrolarException(t).getClass().getSimpleName());
        exceptionLog.setCausedBy(BuscaCausa.getCausedBy(t));
        if (usuarioCorrente.getUsuarioUnidadeOrganizacional() != null && !usuarioCorrente.getUsuarioUnidadeOrganizacional().isEmpty()) {
            exceptionLog.setUnidadeOrganizacionalLogada(usuarioCorrente.getUsuarioUnidadeOrganizacional().get(0).getUnidadeOrganizacional());
        }
        exceptionLog.setUsuarioLogado(usuarioCorrente);
        return em.merge(exceptionLog);
    }

    public ConfiguracaoDeRelatorio buscarConfiguracaoDeRelatorioPorChave() {
        return configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
    }

    public static enum PerfilApp {
        DEV, PROD;
    }
}
