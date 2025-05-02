/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.AtoLegalFacade;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.IsencaoFacede;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Heinz
 */
@ManagedBean
@SessionScoped
public class IsencaoControlador implements Serializable {

    private Isencao isencao;
    @EJB
    private IsencaoFacede isencaoFacede;
    private ConverterAutoComplete converterIsencao;
    @EJB
    private DividaFacade dividaFacede;
    private ConverterGenerico converterDivida;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoLegal;
    private List<Tributo> listaTributos;

    private String caminho;

    public IsencaoControlador() {
        listaTributos = new ArrayList<Tributo>();
        isencao = new Isencao();
    }

    public void novo() {
        listaTributos = new ArrayList<Tributo>();
        isencao = new Isencao();
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }


    public Isencao getIsencao() {
        return isencao;
    }

    public void setIsencao(Isencao isencao) {
        this.isencao = isencao;
    }

    public Converter getConverterIsencao() {
        if (converterIsencao == null) {
            converterIsencao = new ConverterAutoComplete(Isencao.class, isencaoFacede);
        }
        return converterIsencao;
    }

    public Converter getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterGenerico(Divida.class, dividaFacede);
        }
        return converterDivida;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        List<AtoLegal> lista = atoLegalFacade.listaFiltrando(parte, "nome");
        AtoLegal erro = new AtoLegal();
        if (lista.isEmpty() && parte.trim().equals("")) {
            erro.setNome("Não existem dívidas");
        } else if (lista.isEmpty()) {
            erro.setNome("Não foi encontrada a dívida desejada");
        } else {
            return lista;
        }
        lista.add(erro);
        return lista;
    }

    public List<SelectItem> getListaDivida() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (Divida object : dividaFacede.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void listaTributosDivida() {
        isencao.setItens(new ArrayList<ItemIsencao>());
        listaTributos = dividaFacede.listarTributosDivida(isencao.getDivida());
        for (Tributo t : listaTributos) {
            ItemIsencao it = new ItemIsencao();
            it.setIsencao(isencao);
            it.setTributo(t);
            isencao.getItens().add(it);
        }
    }

    public void salvar() {
        if (validaCampos()) {
            isencaoFacede.salvarNovo(isencao);
            isencao = new Isencao();
        }
    }

    private boolean validaCampos() {
        boolean retorno = Boolean.TRUE;

        if (isencao.getCodigo() == null || isencao.getDescricao() == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:categoriaCodigo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar. ", "Digite a categoria"));
            retorno = false;
        } else if (isencao.getCodigo() != null) {
            if (isencaoFacede.existeCodigoCategoria(isencao.getCodigo())) {
                FacesContext.getCurrentInstance().addMessage("Formulario:categoriaCodigo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar. ", "Este codigo já existe!"));
                retorno = false;
            }
        }

        if (isencao.getAtoLegal() == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:atLei", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar. ", "Selecione um Ato Legal."));
            retorno = false;
        }

        if (isencao.getDivida() == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:comboDivida", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar. ", "Selecione uma dívida."));
            retorno = false;
        }

        return retorno;
    }

    public String cancelar() {
        return "lista";
    }

    public List<Isencao> getLista() {
        return isencaoFacede.lista();
    }
}
