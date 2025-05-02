/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.TipoIsencao;
import br.com.webpublico.negocios.TipoIsencaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

/**
 * @author Leonardo
 */
@ManagedBean
@SessionScoped
public class TipoIsencaoControlador implements Serializable {

    @EJB
    private TipoIsencaoFacade tipoIsencaoFacade;
    private TipoIsencao selecionado;
    private String caminho;
    private ConverterAutoComplete converterAtoLegal;

    public TipoIsencao getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(TipoIsencao selecionado) {
        this.selecionado = selecionado;
    }

    public void novo() {
        this.selecionado = new TipoIsencao();
        this.selecionado.setCodigo(this.tipoIsencaoFacade.retornaUltimoCodigoLong());
    }

    public String caminho() {
        return this.caminho;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public List<TipoIsencao> getLista() {
        return this.tipoIsencaoFacade.lista();
    }

    public void selecionar(ActionEvent evento) {

        this.selecionado = (TipoIsencao) evento.getComponent().getAttributes().get("objeto");
    }

    public void excluirSelecionado() {
        this.tipoIsencaoFacade.remover(this.selecionado);
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return this.tipoIsencaoFacade.getAtoLegalFacade().listaPorNomeIsNotNull(parte.trim());
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (this.converterAtoLegal == null) {
            this.converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, this.tipoIsencaoFacade.getAtoLegalFacade());
        }
        return this.converterAtoLegal;
    }

    public String salvar() {
        if (validaCampos()) {
            try {
                if (selecionado.getId() == null) {
                    this.tipoIsencaoFacade.salvarNovo(selecionado);
                } else {
                    this.tipoIsencaoFacade.salvar(selecionado);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com sucesso!", ""));
                return this.caminho;

            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema: ", e.getMessage().toString()));
                return "edita.xhtml";
            }
        } else {
            return "edita";
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;

        if ("".equals(this.selecionado.getNome()) || this.selecionado.getNome() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção!", "O campo Nome é obrigatório."));
            retorno = false;
        }
        if ("".equals(this.selecionado.getAtoLegal()) || this.selecionado.getNome() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção!", "O campo Ato Legal é obrigatório."));
            retorno = false;
        }
        if (!this.tipoIsencaoFacade.verificaExistenciaNome(this.selecionado)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção!", "O Nome informado já existe, por favor informe outro."));
            retorno = false;
        }


        return retorno;
    }

    public String cancela() {
        return this.caminho;
    }
}
