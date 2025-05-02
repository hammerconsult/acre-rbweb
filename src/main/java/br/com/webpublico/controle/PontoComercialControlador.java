/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ItemPontoComercial;
import br.com.webpublico.entidades.Localizacao;
import br.com.webpublico.entidades.PontoComercial;
import br.com.webpublico.entidades.TipoPontoComercial;
import br.com.webpublico.negocios.AtividadePontoFacade;
import br.com.webpublico.negocios.LocalizacaoFacade;
import br.com.webpublico.negocios.PontoComercialFacade;
import br.com.webpublico.negocios.TipoPontoComercialFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.enums.Operacoes;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gustavo
 */
@ManagedBean
@SessionScoped
public class PontoComercialControlador implements Serializable {

    @EJB
    private PontoComercialFacade facade;
    private String caminho;
    private List<PontoComercial> lista;
    private PontoComercial selecionado;
    private Operacoes operacao;
    private ItemPontoComercial itemPontoComercial;
    private ConverterGenerico converterTipoPontoComercial;
    private ConverterAutoComplete converterlocalizacao;
    private transient Converter converterAtividadePonto;
    @EJB
    private TipoPontoComercialFacade tipoPontoComercialFacade;
    @EJB
    private AtividadePontoFacade atividadeFacade;
    @EJB
    private LocalizacaoFacade localizacaoFacade;

    public PontoComercialFacade getFacade() {
        return facade;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public List<PontoComercial> getLista() {
        if (lista == null) {
            lista = facade.lista();
        }
        return lista;
    }

    public Converter getConverterTipoPontoComercial() {
        if (converterTipoPontoComercial == null) {
            converterTipoPontoComercial = new ConverterGenerico(TipoPontoComercial.class, tipoPontoComercialFacade);
        }
        return converterTipoPontoComercial;
    }


    public Converter getConverterLocalizacao() {
        if (converterlocalizacao == null) {
            converterlocalizacao = new ConverterAutoComplete(Localizacao.class, localizacaoFacade);
        }
        return converterlocalizacao;
    }

    public ItemPontoComercial getItemPontoComercial() {
        return itemPontoComercial;
    }

    public void setItemPontoComercial(ItemPontoComercial itemPontoComercial) {
        this.itemPontoComercial = itemPontoComercial;
    }

    public PontoComercial getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(PontoComercial selecionado) {
        this.selecionado = selecionado;
    }

    public void novo() {
        selecionado = new PontoComercial();
        itemPontoComercial = new ItemPontoComercial();
    }

    public List<Localizacao> completaLocalizacao(String parte) {
        return localizacaoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public void selecionar(ActionEvent ae) {
        selecionado = facade.recuperar(((PontoComercial) ae.getComponent().getAttributes().get("objeto")).getId());
        itemPontoComercial = new ItemPontoComercial();
    }

    public String cancelar() {
        //System.out.println("Caminho : " + caminho);
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
        if (selecionado.getNumeroBox() == null) {
            resultado = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "O Número do Box deve ser informado"));
        }
        if (selecionado.getArea() == null || selecionado.getArea().compareTo(BigDecimal.ZERO) < 0) {
            resultado = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "A área deve ser maior ou igual a zero"));
        }
        if (selecionado.getLocalizacao() == null || selecionado.getLocalizacao().getId() == null) {
            resultado = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "A localização deve ser informada"));
        } else if (selecionado.getNumeroBox() != null && facade.JaExisteBoxNaLocalizacao(selecionado)) {
            resultado = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Já existe um box com esse número nessa localização"));
        }
        return resultado;
    }

    public String caminho() {
        return caminho;
    }

    public List<SelectItem> getPontoComercialPontos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoPontoComercial tpc : tipoPontoComercialFacade.lista()) {
            toReturn.add(new SelectItem(tpc, tpc.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoPontosComerciais() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();

        toReturn.add(new SelectItem(null, " "));
        for (TipoPontoComercial tpc : tipoPontoComercialFacade.retornaTipoPontoComercial()) {
            toReturn.add(new SelectItem(tpc, tpc.getDescricao()));
        }

        return toReturn;
    }
}
