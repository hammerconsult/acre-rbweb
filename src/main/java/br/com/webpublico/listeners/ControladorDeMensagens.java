package br.com.webpublico.listeners;

import br.com.webpublico.entidades.LogSistema;
import br.com.webpublico.seguranca.MensagemTodosUsuariosService;
import br.com.webpublico.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.PrettyContext;
import org.primefaces.context.RequestContext;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import java.io.IOException;
import java.util.*;

public class ControladorDeMensagens implements PhaseListener {

    public static final String KEY_LOGS_SISTEMA = "LOG_MESSAGES_SUPPORT";
    public static final String MENSAGENS_DO_LOG_DO_SISTEMA = "MULTI_PAGE_MESSAGES_SUPPORT"; // Log do sistema - todas as mensagens
    public static final String CAMINHOS_NAVEGADOS = "CAMINHOS_NAVEGADOS";

    private MensagemTodosUsuariosService mensagemTodosUsuariosService;


    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    @Override
    public void afterPhase(final PhaseEvent event) {
        FacesContext facesContext = event.getFacesContext();

        if (PhaseId.INVOKE_APPLICATION.equals(event.getPhaseId())) {
            this.salvarFacesMessages(facesContext);
        }
    }

    private void carregarSingletonParaComunicacao() {
        if (mensagemTodosUsuariosService != null) {
            return;
        }
        mensagemTodosUsuariosService = (MensagemTodosUsuariosService) Util.getSpringBeanPeloNome("mensagemTodosUsuariosService");
    }

    private void exibirMensagensGeraisDoSistema() {
        carregarSingletonParaComunicacao();
        if (mensagemTodosUsuariosService.getMensagens() == null || mensagemTodosUsuariosService.getMensagens().isEmpty()) {
            return;
        }
        RequestContext.getCurrentInstance().update("panel-mensagem-do-sistema");
    }

    @Override
    public void beforePhase(final PhaseEvent event) {
        FacesContext facesContext = event.getFacesContext();
        if (PhaseId.RENDER_RESPONSE.equals(event.getPhaseId())) {
            try {
                salvarFacesMessages(facesContext);
                this.tratarExibicaoMensagens(facesContext);
            } catch (IOException e) {
                e.printStackTrace();
            }
            exibirMensagensGeraisDoSistema();
        }
    }

    private void tratarExibicaoMensagens(final FacesContext facesContext) throws IOException {
        List<LogSistema> mensagensNaoLidas = (List<LogSistema>) getSessionMap(facesContext).get(recuperarUrl());
        if (mensagensNaoLidas == null || mensagensNaoLidas.isEmpty()) {
            return;
        }

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(mensagensNaoLidas);
        RequestContext.getCurrentInstance().execute("exibirMensagensJsonString(" + json.toString() + ")");
//        getSessionMap(facesContext).put(recuperarUrl(), Lists.newArrayList());
    }

    private String recuperarUrl() {
        return PrettyContext.getCurrentInstance().getContextPath() + PrettyContext.getCurrentInstance().getRequestURL();
    }

    @SuppressWarnings("unchecked")
    private void salvarFacesMessages(final FacesContext facesContext) {
        List<LogSistema> mensagensDeAgora = new ArrayList<>();

        for (Iterator<FacesMessage> iter = facesContext.getMessages(); iter.hasNext(); ) {
            LogSistema log = new LogSistema();
            log.setFoiVisualizada(Boolean.FALSE);
            log.setMensagem(iter.next());
            log.setOcorridoEm(new Date());
            mensagensDeAgora.add(log);
            iter.remove();
        }

        if (mensagensDeAgora.isEmpty()) {
            return;
        }

        List<LogSistema> mensagensDaSessao = (List<LogSistema>) getSessionMap(facesContext).get(MENSAGENS_DO_LOG_DO_SISTEMA);
        List<LogSistema> mensagensNaoLidas = Lists.<LogSistema>newArrayList();

        mensagensDaSessao = mensagensDaSessao == null ? Lists.<LogSistema>newArrayList() : mensagensDaSessao;

        mensagensDaSessao.addAll(mensagensDeAgora);
        mensagensNaoLidas.addAll(mensagensDeAgora);

        getSessionMap(facesContext).put(MENSAGENS_DO_LOG_DO_SISTEMA, mensagensDaSessao);
        getSessionMap(facesContext).put(recuperarUrl(), mensagensNaoLidas);
    }

    private Map<String, Object> getSessionMap(final FacesContext facesContext) {
        return facesContext.getExternalContext().getSessionMap();
    }
}

