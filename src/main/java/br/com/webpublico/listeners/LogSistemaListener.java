/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.listeners;

import br.com.webpublico.entidades.LogSistema;
import org.primefaces.context.RequestContext;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@Deprecated
public class LogSistemaListener implements PhaseListener, Serializable {

    @Override
    public void beforePhase(PhaseEvent pe) {
        if (PhaseId.RESTORE_VIEW.equals(pe.getPhaseId())) {
            if (getFromSession("mensagensLogSistema") == null) {
                ArrayList<LogSistema> lista = new ArrayList<>();
                putToSession("mensagensLogSistema", lista);
            }
        }
        if (PhaseId.RENDER_RESPONSE.equals(pe.getPhaseId())) {
            if (RequestContext.getCurrentInstance() != null) {
                RequestContext.getCurrentInstance().update("rodape");
                RequestContext.getCurrentInstance().update("dialogoMensagensNaoLidas");
                RequestContext.getCurrentInstance().update("dialogoMensagensNaoLidas");

            }
        }
    }

    @Override
    public void afterPhase(PhaseEvent pe) {
        if (PhaseId.PROCESS_VALIDATIONS.equals(pe.getPhaseId()) || PhaseId.INVOKE_APPLICATION.equals(pe.getPhaseId())) {
            FacesContext facesContext = pe.getFacesContext();
            this.transferirMensagens(facesContext);
            this.atribuirPrimeiraMensagem();
        }
    }

    private void atribuirPrimeiraMensagem() {
        try {
            ArrayList<LogSistema> listaMensagens = (ArrayList<LogSistema>) getFromSession("mensagensLogSistema");
            if (listaMensagens != null && !listaMensagens.isEmpty()) {
                putToSession("primeiraMensagem", listaMensagens.get(0));
            }
        } catch (Exception e) {
        }
    }

    private void transferirMensagens(final FacesContext facesContext) {
        ArrayList<LogSistema> listaMensagens = (ArrayList<LogSistema>) getFromSession("mensagensLogSistema");

        for (FacesMessage mensagemDaVez : facesContext.getMessageList()) {
            LogSistema log = new LogSistema(new Date(), mensagemDaVez, Boolean.FALSE);
            listaMensagens.add(log);
        }
        if (listaMensagens != null) {
            Collections.sort(listaMensagens, new Comparator<LogSistema>() {
                @Override
                public int compare(LogSistema o1, LogSistema o2) {
                    return o2.getOcorridoEm().compareTo(o1.getOcorridoEm());
                }
            });
            putToSession("mensagensLogSistema", listaMensagens);
        }
    }

    public void putToSession(String chave, Object objeto) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(chave, objeto);
    }

    public Object getFromSession(String chave) {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(chave);
    }

    public void removeFromSession(String chave) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(chave);
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }
}
