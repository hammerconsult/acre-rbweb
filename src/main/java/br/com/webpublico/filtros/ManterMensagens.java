/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.filtros;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Felipe Marinzeck
 */
@Deprecated
public class ManterMensagens implements PhaseListener {

    private static final long serialVersionUID = 1250469273857785274L;
    private static final String codigoSessao = "MANTER_MENSAGENS";

    @Override
    public void afterPhase(PhaseEvent pe) {
        if (!PhaseId.RENDER_RESPONSE.equals(pe.getPhaseId())) {
            FacesContext facesContext = pe.getFacesContext();
            this.salvarMensagens(facesContext);
        }
    }

    @Override
    public void beforePhase(final PhaseEvent pe) {
        FacesContext facesContext = pe.getFacesContext();
        this.salvarMensagens(facesContext);

        if (PhaseId.RENDER_RESPONSE.equals(pe.getPhaseId())) {
            if (!facesContext.getResponseComplete()) {
                this.restaurarMensagens(facesContext);
            }
        }
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    private int salvarMensagens(final FacesContext facesContext) {
        List<Object[]> mensagens = new ArrayList<>();
//        for (FacesMessage facesMessage : facesContext.getMessageList()) {
//            Object obj1 = facesMessage;
//            Object obj2 = ;
//        }

        for (Iterator<String> iterClient = facesContext.getClientIdsWithMessages(); iterClient.hasNext(); ) {
            String nomeCampo = iterClient.next();
            for (Iterator<FacesMessage> iterMess = facesContext.getMessages(nomeCampo); iterMess.hasNext(); ) {
                Object[] obj = new Object[2];

                obj[0] = nomeCampo;
                obj[1] = iterMess.next();

//                mensagens.add(obj);
                iterMess.remove();
            }

            iterClient.remove();
        }

        if (mensagens.isEmpty()) {
            return 0;
        }

        Map<String, Object> sessionMap = facesContext.getExternalContext().getSessionMap();
        List<Object[]> mensagensExistentes = (List<Object[]>) sessionMap.get(codigoSessao);
        if (mensagensExistentes != null) {
            mensagensExistentes.addAll(mensagens);
        } else {
            sessionMap.put(codigoSessao, mensagens);
        }
        return mensagens.size();
    }

    private int restaurarMensagens(final FacesContext facesContext) {
        Map<String, Object> mapSessao = facesContext.getExternalContext().getSessionMap();
        List<Object[]> mensagens = (List<Object[]>) mapSessao.remove(codigoSessao);

        if (mensagens == null) {
            return 0;
        }

        int count = mensagens.size();
        for (Object[] elemento : mensagens) {
            facesContext.addMessage((String) elemento[0], (FacesMessage) elemento[1]);
        }
        return count;
    }
}
