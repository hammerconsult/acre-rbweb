/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.LogSistema;
import br.com.webpublico.listeners.ControladorDeMensagens;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Map;

/**
 * @author Felipe Marinzeck
 */
@ManagedBean
@SessionScoped
public class TemplateControlador implements Serializable {

    private Boolean mostraCabecalho;
    private Boolean mostraRodape;
    private String pesquisaMenu;

    /**
     * Creates a new instance of TemplateControlador
     */
    public TemplateControlador() {
        limparTodasMensagens();
    }

    public void limparTodasMensagens() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ControladorDeMensagens.KEY_LOGS_SISTEMA, null);
    }

    public String getPesquisaMenu() {
        return pesquisaMenu;
    }

    public void setPesquisaMenu(String pesquisaMenu) {
        this.pesquisaMenu = pesquisaMenu;
    }

    public Boolean getMostraCabecalho() {
        return mostraCabecalho;
    }

    public void setMostraCabecalho(Boolean mostraCabecalho) {
        this.mostraCabecalho = mostraCabecalho;
    }

    public Boolean getMostraRodape() {
        return mostraRodape;
    }

    public void setMostraRodape(Boolean mostraRodape) {
        this.mostraRodape = mostraRodape;
    }

    public String recuperarClasseGravidadeMensagem(FacesMessage mensagem) {
        String retorno = "";

        if (mensagem == null) {
            return retorno;
        }

        if (mensagem.getSeverity().equals(FacesMessage.SEVERITY_INFO)) {
            retorno = "info";
        }
        if ((mensagem.getSeverity().equals(FacesMessage.SEVERITY_WARN))) {
            retorno = "warn";
        }
        if ((mensagem.getSeverity().equals(FacesMessage.SEVERITY_ERROR))) {
            retorno = "error";
        }
        if ((mensagem.getSeverity().equals(FacesMessage.SEVERITY_FATAL))) {
            retorno = "fatal";
        }

        return retorno;
    }

    public String recuperarStyleMensagem(LogSistema mess) {
        if (mess.getFoiVisualizada()) {
            return "color : gray!important;";
        }
        return "";
    }


    public boolean renderizaSeparadorDaMensagem(LogSistema ls) {
        try {
            if (ls.getMensagem().getSummary().equals(ls.getMensagem().getDetail())) {
                return false;
            }
            if (ls.getMensagem().getSummary() == null || ls.getMensagem().getSummary().isEmpty()) {
                return false;
            }
            if (ls.getMensagem().getDetail() == null || ls.getMensagem().getDetail().isEmpty()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public void limpaSessaoPesquisaGenerico() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        for (String key : sessionMap.keySet()) {
            if (key.startsWith(ComponentePesquisaGenerico.SESSION_KEY_PESQUISAGENERICO)) {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().values().remove(sessionMap.get(key));
            }
        }
        Web.limpaNavegacao();
    }
}
