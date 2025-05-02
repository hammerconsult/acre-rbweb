/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.negocios.AATeste;
import br.com.webpublico.negocios.SistemaFacade;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

/**
 * @author Munif
 */
@ManagedBean
@SessionScoped
public class TesteSegurancaControlador implements Serializable {

    @EJB
    private AATeste aATeste;
    @EJB
    private SistemaFacade sistemaFacade;

    public List<UsuarioSistema> getLista() {
        try {
            return aATeste.listaTodosUsuarios();
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage("tabelaUsuario", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Usuário não tem direitos", "O usuário logado não tem direito para acessar esta funão"));
        }
        return null;
    }

    public String getQuem() {
        return aATeste.getLoginCorrente();
    }

    public UsuarioSistema getUsuario() {
        return sistemaFacade.getUsuarioCorrente();
    }
}
