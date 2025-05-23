/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.controle.Web;
import br.com.webpublico.entidades.ExceptionLog;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoMensagemValidacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.listeners.ControladorDeMensagens;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.servlet.PrettyFacesWrappedRequest;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;

import javax.faces.FactoryFinder;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author peixe
 * @author gecen
 */
public abstract class FacesUtil {

    private static final Logger logger = LoggerFactory.getLogger(FacesUtil.class);

    /**
     *
     */
    private static final String ID_CLIENTE_PADRAO = "msgs";

    /**
     * @deprecated Use o método addWarn
     */
    @Deprecated
    public static void addMessageWarn(String summary, String detail) {
        addMessageWarn(ID_CLIENTE_PADRAO, summary, detail);
    }

    /**
     * @deprecated Use o método addWarn
     */
    @Deprecated
    public static void addMessageWarn(String clientId, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(clientId, getMessageWarn(summary, detail));
    }

    /**
     * @deprecated Use o método addInfo
     */
    @Deprecated
    public static void addMessageInfo(String clientId, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(clientId, getMessageInfo(summary, detail));
    }

    /**
     * @deprecated Use o método addInfo
     */
    @Deprecated
    public static void addMessageInfo(String summary, String detail) {
        addMessageInfo(ID_CLIENTE_PADRAO, summary, detail);
    }

    /**
     * @deprecated Use o método addError
     */
    @Deprecated
    public static void addMessageError(String summary, String detail) {
        addMessageError(ID_CLIENTE_PADRAO, summary, detail);
    }

    /**
     * @deprecated Use o método addError
     */
    @Deprecated
    public static void addMessageError(String clientId, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(clientId, getMessageError(summary, detail));
    }

    public static void printAllMessages(List<MensagemValidacao> lista) {
        Severity severity;
        for (MensagemValidacao msg : lista) {
            if (msg.getTipo() == TipoMensagemValidacao.ALERTA) {
                severity = FacesMessage.SEVERITY_WARN;
            } else if (msg.getTipo() == TipoMensagemValidacao.ERRO) {
                severity = FacesMessage.SEVERITY_ERROR;
            } else if (msg.getTipo() == TipoMensagemValidacao.ERRO_FATAL) {
                severity = FacesMessage.SEVERITY_FATAL;
            } else {
                severity = FacesMessage.SEVERITY_INFO;
            }
            FacesContext.getCurrentInstance().addMessage(msg.getClientId(), new FacesMessage(severity, msg.getSummary(), msg.getDetail()));
        }
    }

    public static void printAllFacesMessages(List<FacesMessage> lista) {
        for (FacesMessage msg : lista) {
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    /* SOMENTE AS IMPLEMENTAÇÕES DAQUI PRA BAIXO DEVEM SER UTILIZADAS
     * A DEFINIÇÃO DO NOVO LAYOUT DO SISTEMA, FEZ COM QUE OS MÉTODOS LISTADOS
     * ACIMA SE TORNASSEM DESNECESSÁRIOS
     */
    public static void executaJavaScript(String script) {
        RequestContext.getCurrentInstance().execute(script);
    }

    public static void atualizarComponente(String nomeComponente) {
        RequestContext.getCurrentInstance().update(nomeComponente);
    }

    public static UIComponent buscarComponente(String idComponente) {
        return FacesContext.getCurrentInstance().getViewRoot().findComponent(idComponente);
    }

    public static void addWarn(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, getMessageWarn(summary, detail));
    }

    private static FacesMessage getMessageWarn(String summary, String detail) {
        return new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail);
    }

    public static void addInfo(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, getMessageInfo(summary, detail));
    }

    private static FacesMessage getMessageInfo(String summary, String detail) {
        return new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
    }

    public static void addError(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, getMessageError(summary, detail));
    }

    private static FacesMessage getMessageError(String summary, String detail) {
        return new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
    }

    public static void addCampoObrigatorio(String detail) {
        FacesContext.getCurrentInstance().addMessage(null, getMessageError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), detail));
    }

    public static void addOperacaoNaoRealizada(String detail) {
        FacesContext.getCurrentInstance().addMessage(null, getMessageError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), detail));
    }

    public static void addOperacaoNaoPermitida(String detail) {
        FacesContext.getCurrentInstance().addMessage(null, getMessageError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), detail));
    }

    public static void addOperacaoRealizada(String detail) {
        FacesContext.getCurrentInstance().addMessage(null, getMessageInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), detail));
    }

    public static void addAtencao(String detail) {
        FacesContext.getCurrentInstance().addMessage(null, getMessageWarn(SummaryMessages.ATENCAO.getDescricao(), detail));
    }

    public static void addFatal(String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, summary, detail));
    }

    public static void addErrorPadrao(Throwable ex) {
        addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), ex.getMessage());
    }

    public static void addErrorGenerico(Throwable ex) {
        addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Por favor tente novamente, se o problema persistir entre em contato com o suporte técnico. Detalhes do Erro: " + ex.getMessage());
    }

    public static void addErroAoGerarRelatorio(String detail) {
        addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Ocorreu um erro ao gerar o relatório, se o problema persistir entre em contato com o suporte técnico. Detalhes do Erro: " + detail);
    }

    public static void addExceptionLog(ExceptionLog exceptionLog) {
        FacesContext.getCurrentInstance().addMessage(null, getMessageError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " Sistema gerou esse erro: " + exceptionLog.getId() + ", encaminhe para o  suporte."));
    }

    public static void navegaEmbora(Object selecionado, String caminhoPadrao) {
        String origem = Web.getCaminhoOrigem();
        if (!origem.isEmpty()) {
            if (Web.getEsperaRetorno()) {
                Web.poeNaSessao(selecionado);
            }
            redirecionamentoInterno(origem);
        } else {
            origem = caminhoPadrao + "listar/";
            redirecionamentoInterno(origem);
        }
    }

    private static Map<String, Object> getSessionMap() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    }

    public static void guardarCaminhosParaPossiveisMensagensAposNavegacao(String url) {
        HashMap<String, String> caminhos = (HashMap) getSessionMap().get(ControladorDeMensagens.CAMINHOS_NAVEGADOS);
        caminhos = caminhos == null ? new HashMap<String, String>() : caminhos;

        String urlDestino = PrettyContext.getCurrentInstance().getContextPath() + url;
        String urlOrigem = PrettyContext.getCurrentInstance().getContextPath() + PrettyContext.getCurrentInstance().getRequestURL().toString();

        caminhos.put(urlDestino, urlOrigem);
        getSessionMap().put(ControladorDeMensagens.CAMINHOS_NAVEGADOS, caminhos);
    }

    public static void redirecionamentoInterno(String url) {
        if (!url.trim().endsWith("/")) {
            url += "/";
        }
        guardarCaminhosParaPossiveisMensagensAposNavegacao(url);
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(getRequestContextPath() + url);
        } catch (IOException ex) {
            logger.error("Erro: ", ex);
        }
    }

    public static void redirecionamentoExterno(String url, boolean novaAba) {
        if (!url.trim().endsWith("/")) {
            url += "/";
        }
        if (novaAba) {
            RequestContext.getCurrentInstance().execute("window.open('" + url + "', '_blank');");
        } else {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect(url);
            } catch (IOException e) {
                logger.error("Erro: ", e);
            }
        }
    }

    public static String getRequestContextPath() {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
    }

    // Ex: http://localhost:8080/webpublico/
    public static String geraUrlImagemDir() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        final StringBuffer url = request.getRequestURL();
        String test = url.toString();
        String[] quebrado = test.split("/");
        StringBuilder b = new StringBuilder();
        b.append(quebrado[0]);
        b.append("/").append(quebrado[1]);
        b.append("/").append(quebrado[2]).append("/");
        return b.toString();
    }

    public static String alteraURLImagens(String conteudo) {
        if (!conteudo.contains("src=\"data:image/png;base64")) {
            conteudo = conteudo.replace("../", "");
            try {
                //O replace é utilizado para caso a aplicação não tenha contexto
                String caminho = geraUrlImagemDir().replace("faces/", "");
                String[] quebrado = caminho.split("/");
                StringBuilder b = new StringBuilder();
                b.append(quebrado[0]);
                b.append("/").append(quebrado[1]);
                b.append("/").append(quebrado[2]);
                if (quebrado.length > 3) {
                    b.append("/").append(quebrado[3]);
                }
                caminho = b.toString() + "/";
                conteudo = conteudo.replace("src=\"", "src=\"" + caminho);

            } catch (Exception e) {
                conteudo = conteudo.replace("../../../", "../");
            }
            Pattern pattern = Pattern.compile(".*<img[^>]*src=\"([^\"]*)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(conteudo);
            while (matcher.find()) {
                String src = matcher.group(1).replace("img src=", "").replace("\"", "");
                if (!src.contains("base64")) {
                    conteudo = conteudo.replace(src, "data:image/png;base64," + Util.converterImgUrlParaBase64(src));
                }
            }
        }
        return conteudo;
    }

    public static String localUsuarioAtualmente() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        // URL Completa
        String urlFinal = request.getRequestURL().toString();
        // Geralmente /webpublico/faces
        String path = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + FacesContext.getCurrentInstance().getExternalContext().getRequestServletPath();
        urlFinal = urlFinal.substring(urlFinal.lastIndexOf(path) + path.length(), urlFinal.length());
        return urlFinal;
    }

    public static void addOperacaoOptimisticLock() {
        FacesUtil.addOperacaoNaoPermitida("O registro que você está alterando acabou de ser modificado por outro usuário.");
        FacesUtil.addOperacaoNaoPermitida("Atualize a página e tente novamente.");
    }

    public static String getBaseUrl(HttpServletRequest request) {
        String baseUrl = request.getScheme() + // "http"
            "://" +                                // "://"
            request.getServerName() +              // "myhost"
            ":" +                                  // ":"
            request.getServerPort() +               // "80"

            request.getContextPath() +              // webpublico

            request.getServletPath();               // spring

        return baseUrl;
    }

    public static void printAllFacesMessages(ValidacaoException op) {
        printAllFacesMessages(op.getMensagens());
    }

    public static void addMensagemRelatorioSegundoPlano() {
        addInfo("Aguarde!", "O Relatório está sendo gerado em segundo Plano");
    }

    public static void recarregarPaginAtual() {
        PrettyFacesWrappedRequest prettyFacesRequest = getPrettyFacesRequest();
        SecurityContextHolderAwareRequestWrapper securityContextHolderAwareRequestWrapper = (SecurityContextHolderAwareRequestWrapper) prettyFacesRequest.getRequest();
        HttpServletRequest requestWrapper = (HttpServletRequest) securityContextHolderAwareRequestWrapper.getRequest();
        try {
            Field[] fields = requestWrapper.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals("strippedServletPath")) {
                    field.setAccessible(true);
                    String url = (String) field.get(requestWrapper);
                    field.setAccessible(false);
                    FacesUtil.redirecionamentoInterno(url);
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            logger.error("Erro ao recarregar a pagina atual", e);
        }
    }

    private static PrettyFacesWrappedRequest getPrettyFacesRequest() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        ServletRequest wrapper = request;
        while (!wrapper.getClass().equals(PrettyFacesWrappedRequest.class)) {
            if (wrapper instanceof ServletRequestWrapper) {
                wrapper = ((ServletRequestWrapper) wrapper).getRequest();
            } else {
                break;
            }
        }
        return (PrettyFacesWrappedRequest) wrapper;
    }

    public static void ciarFacesContext(HttpServletRequest request, HttpServletResponse response) {

        LifecycleFactory lifecycleFactory = (LifecycleFactory)
            FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

        FacesContextFactory contextFactory = (FacesContextFactory)
            FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
        contextFactory.getFacesContext(
            request.getSession().getServletContext(), request, response, lifecycle);
    }
}
