/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CategoriaServico;
import br.com.webpublico.negocios.CategoriaServicoFacade;
import br.com.webpublico.enums.Operacoes;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

/**
 * @author terminal1
 */
@ManagedBean
@SessionScoped
public class CategoriaServicoControlador implements Serializable {

    @EJB
    private CategoriaServicoFacade facade;
    private CategoriaServico selecionado;
    private List<CategoriaServico> lista;
    private String caminho;
    private Operacoes operacao;

    public CategoriaServicoFacade getFacade() {
        return facade;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public List<CategoriaServico> getLista() {
        if (lista == null) {
            lista = facade.lista();
        }
        return lista;
    }

    public CategoriaServico getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(CategoriaServico selecionado) {
        this.selecionado = selecionado;
    }

    public void novo() {
        selecionado = new CategoriaServico();

    }

    public void selecionar(ActionEvent ae) {
        selecionado = facade.recuperar(((CategoriaServico) ae.getComponent().getAttributes().get("objeto")).getId());

    }

    public String cancelar() {
        return caminho;
    }

    public String salvar() {

        if (validaCampos() == true) {
            try {

                if (operacao == Operacoes.NOVO) {
                    facade.salvarNovo(selecionado);
                } else {
                    facade.salvar(selecionado);
                }
                lista = null;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com Sucesso", ""));
                //System.out.println("Caminho : " + caminho);
                return caminho;

            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage().toString()));
                return "edita.xhtml";
            }
        } else {
            return "edita.xhtml";
        }
    }

    public void excluirSelecionado() {
        try {
            facade.remover(selecionado);
            lista = null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "O Registro não pode ser excluido, o mesmo possui dependencias."));
        }
    }

    private boolean validaCampos() {
        boolean resultado = true;
        if (facade.codigoJaExite(selecionado)) {
            resultado = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "O código já existe"));
        }
        if (selecionado.getCodigo() == null || selecionado.getCodigo().isEmpty()) {
            resultado = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "O código deve ser informado"));
        }
        if (selecionado.getNome() == null || selecionado.getNome().isEmpty()) {
            resultado = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "O nome deve ser informado"));
        }
        return resultado;
    }

    public String caminho() {
        return caminho;
    }
}
