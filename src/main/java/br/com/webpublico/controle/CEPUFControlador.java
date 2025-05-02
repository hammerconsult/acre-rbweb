/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CEPUF;
import br.com.webpublico.negocios.CEPUFFacade;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

/**
 * @author Terminal-2
 */
@SessionScoped
@ManagedBean
public class CEPUFControlador implements Serializable {

    private CEPUF selecionado;
    @EJB
    private CEPUFFacade cepUfFacade;
    private String caminho;

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public CEPUF getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(CEPUF selecionado) {
        this.selecionado = selecionado;
    }


    public List<CEPUF> getListaCeps() {
        return cepUfFacade.lista();
    }

    public void novo() {
        selecionado = new CEPUF();
    }

    public String salvar() {
        cepUfFacade.salvar(selecionado);
        return "lista.xhtml";
    }

    public void selecionar(ActionEvent event) {
        selecionado = (CEPUF) event.getComponent().getAttributes().get("objeto");
    }

    public void remover() {
        cepUfFacade.remover(selecionado);
    }
}
