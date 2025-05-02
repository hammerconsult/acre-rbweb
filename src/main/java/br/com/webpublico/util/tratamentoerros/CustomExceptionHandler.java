package br.com.webpublico.util.tratamentoerros;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.ExceptionLog;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.negocios.tributario.AuditoriaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

/**
 * @author Gustavo
 */
public class CustomExceptionHandler extends ExceptionHandlerWrapper {

    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);
    private ExceptionHandler wrapped;

    CustomExceptionHandler(ExceptionHandler exception) {
        this.wrapped = exception;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;

    }

    @Override
    public void handle() throws FacesException {
        try {
            final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
            while (i.hasNext()) {
                ExceptionQueuedEvent event = i.next();
                ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
                Throwable t = context.getException();
                try {
                    if (event.getContext().getException().getClass().equals(ViewExpiredException.class)) {
                        FacesUtil.redirecionamentoInterno("/acesso-negado/sem-sessao/");
                        break;
                    }
                    if(t.getCause() instanceof EntityNotFoundException){
                        criarAuditoriaEntityNotFound(t.getCause().getMessage());
                        break;
                    }
                    lancaLogNoConsole(t);
                    ExceptionLog log = geraExceptionLog(t, context);
                    lancaLogNaView(log);
                    break;
                } finally {
                    i.remove();
                }
            }
            getWrapped().handle();
        } catch (Exception e) {
            FacesUtil.addError("Foi detectado um problema no tratamento de Exceções", "Por favor contate o suporte");
        }
    }

    private void criarAuditoriaEntityNotFound(String msg) throws NamingException {
        AuditoriaFacade auditoriaFacade = (AuditoriaFacade) new InitialContext().lookup("java:module/" + AuditoriaFacade.class.getSimpleName());
        auditoriaFacade.criarAuditoriaFromEntityNotFound(msg);
        FacesUtil.recarregarPaginAtual();
    }

    private ExceptionLog geraExceptionLog(Throwable t, ExceptionQueuedEventContext context) {
        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        ExceptionLog exceptionLog = instanciaExceptionLog(sistemaControlador, t, context);
        return sistemaControlador.geraExceptionLog(exceptionLog);
    }

    private void lancaLogNaView(ExceptionLog log) {
        final FacesContext fc = FacesContext.getCurrentInstance();
        final NavigationHandler nav = fc.getApplication().getNavigationHandler();
        fc.getExternalContext().getSessionMap().put("exceptionMessage", log);
//        try {
//            FacesUtil.executaJavaScript("mostraErro500()");
//        } catch (Exception e) {
        nav.handleNavigation(fc, " ", "/erro500.xhtml");
        fc.renderResponse();
//        }

    }

    private void lancaLogNoConsole(Throwable t) {
        logger.error("Erro não tratado :", t);
    }

    private ExceptionLog instanciaExceptionLog(SistemaControlador sistemaControlador, Throwable t, ExceptionQueuedEventContext context) {
        UsuarioSistema usuarioCorrente = sistemaControlador.getUsuarioCorrente();
        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setDataRegistro(new Date());
        exceptionLog.setIp(sistemaControlador.getIpCorrente());
        exceptionLog.setStackTrace(Util.getStackTraceDaException(t));
        exceptionLog.setTipoException(BuscaCausa.desenrolarException(t).getClass().getSimpleName());
        exceptionLog.setCausedBy(BuscaCausa.getCausedBy(t));
        if (context != null && context.getContext() != null && context.getContext().getViewRoot() != null && context.getContext().getViewRoot().getViewId() != null) {
            exceptionLog.setPagina(context.getContext().getViewRoot().getViewId());
        }
        if (usuarioCorrente.getUsuarioUnidadeOrganizacional() != null && !usuarioCorrente.getUsuarioUnidadeOrganizacional().isEmpty()) {
            exceptionLog.setUnidadeOrganizacionalLogada(usuarioCorrente.getUsuarioUnidadeOrganizacional().get(0).getUnidadeOrganizacional());
        }
        exceptionLog.setUsuarioLogado(usuarioCorrente);
        return exceptionLog;
    }
}
