/*
 * Codigo gerado automaticamente em Mon Jun 25 20:29:45 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TagValor;
import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ItemEventoCLPFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@SessionScoped
public class ItemEventoCLPControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private ItemEventoCLPFacade itemEventoCLPFacade;
    private ItemEventoCLP itemEventoCLP;
    private EventoContabil eventoContabil;
    private ConverterAutoComplete converterCLP;
    private ConverterAutoComplete converterHistorico;
    private List<ItemEventoCLP> listaItemEventoCLPs;
    private List<ItemEventoCLP> listaItemEventoCLPRemovido;

    public ItemEventoCLPControlador() {
        metadata = new EntidadeMetaData(ItemEventoCLP.class);
    }

    public ItemEventoCLPFacade getFacade() {
        return itemEventoCLPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return itemEventoCLPFacade;
    }

    @Override
    public void selecionar(ActionEvent evento) {
        eventoContabil = (EventoContabil) evento.getComponent().getAttributes().get("objeto");
        listaItemEventoCLPs = itemEventoCLPFacade.getListaItens(eventoContabil);
        itemEventoCLP = new ItemEventoCLP();
        listaItemEventoCLPRemovido = new ArrayList<ItemEventoCLP>();
    }

    @Override
    public String salvar() {
        try {
            for (ItemEventoCLP ieclp : listaItemEventoCLPRemovido) {
                itemEventoCLPFacade.salvar(ieclp);
            }
            for (ItemEventoCLP ie : listaItemEventoCLPs) {
                itemEventoCLPFacade.salvar(ie, eventoContabil);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com Sucesso!", "Salvo com Sucesso!"));
            return "lista.xhtml";
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return "edita.xhtml";
        }
    }

    public void adicionarItem() {
        if (itemEventoCLP.getEventoContabil() == null) {
            itemEventoCLP.setEventoContabil(eventoContabil);
        }

        listaItemEventoCLPs.add(itemEventoCLP);
        itemEventoCLP = new ItemEventoCLP();
    }

    public void alterarVigente(ActionEvent evt) {
        ItemEventoCLP itemEventoCLPVelho = new ItemEventoCLP();
        itemEventoCLPVelho = (ItemEventoCLP) evt.getComponent().getAttributes().get("objeto");
        itemEventoCLPVelho.setDataVigencia(new Date());
        itemEventoCLP = new ItemEventoCLP();
        itemEventoCLP.setClp(itemEventoCLPVelho.getClp());
        itemEventoCLP.setEventoContabil(itemEventoCLPVelho.getEventoContabil());
        itemEventoCLP.setTagValor(itemEventoCLPVelho.getTagValor());
        itemEventoCLP.setReprocessar(Boolean.TRUE);
    }

    public void alterarNovaVigencia(ActionEvent evt) {
        ItemEventoCLP itemEventoCLPVelho = new ItemEventoCLP();
        itemEventoCLPVelho = (ItemEventoCLP) evt.getComponent().getAttributes().get("objeto");
        itemEventoCLPVelho.setDataVigencia(new Date());
        itemEventoCLP = new ItemEventoCLP();
        itemEventoCLP.setClp(itemEventoCLPVelho.getClp());
        itemEventoCLP.setEventoContabil(itemEventoCLPVelho.getEventoContabil());
        itemEventoCLP.setTagValor(itemEventoCLPVelho.getTagValor());
        itemEventoCLP.setReprocessar(Boolean.FALSE);
    }

    public List<EventoContabil> getListaEventos() {
        return itemEventoCLPFacade.getEventoContabilFacade().lista();
    }

    public void excluiItem(ActionEvent evt) {
        ItemEventoCLP item = (ItemEventoCLP) evt.getComponent().getAttributes().get("excluirItem");
        item.setDataVigencia(new Date());
        listaItemEventoCLPRemovido.add(item);
        listaItemEventoCLPs.remove(item);
    }

    public List<SelectItem> getTagValor() {
        List<SelectItem> listaDest = new ArrayList<SelectItem>();
        listaDest.add(new SelectItem(null, ""));
        for (TagValor dest : TagValor.values()) {
            listaDest.add(new SelectItem(dest, dest.getDescricao()));
        }
        return listaDest;
    }

    public List<SelectItem> getTipoBalancete() {
        List<SelectItem> listaDest = new ArrayList<SelectItem>();
        listaDest.add(new SelectItem(null, ""));
        for (TipoBalancete dest : TipoBalancete.values()) {
            listaDest.add(new SelectItem(dest, dest.getDescricao()));
        }
        return listaDest;
    }

    public List<CLP> completaCLP(String parte) {
        return itemEventoCLPFacade.getClpFacade().listaFiltrando(parte.trim(), "codigo", "nome");
    }

    public List<ClpHistoricoContabil> completaHistoricoContabil(String parte) {
        return itemEventoCLPFacade.getClpHistoricoContabilFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterHistorico() {
        if (converterHistorico == null) {
            converterHistorico = new ConverterAutoComplete(ClpHistoricoContabil.class, itemEventoCLPFacade.getClpHistoricoContabilFacade());
        }
        return converterHistorico;
    }

    public ConverterAutoComplete getConverterCLP() {
        if (converterCLP == null) {
            converterCLP = new ConverterAutoComplete(CLP.class, itemEventoCLPFacade.getClpFacade());
        }
        return converterCLP;
    }

    public List<ItemEventoCLP> getListaItemEventoCLPs() {
        return listaItemEventoCLPs;
    }

    public void setListaItemEventoCLPs(List<ItemEventoCLP> listaItemEventoCLPs) {
        this.listaItemEventoCLPs = listaItemEventoCLPs;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public ItemEventoCLP getItemEventoCLP() {
        return itemEventoCLP;
    }

    public void setItemEventoCLP(ItemEventoCLP itemEventoCLP) {
        this.itemEventoCLP = itemEventoCLP;
    }
}
