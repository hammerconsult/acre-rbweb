/*
 * Codigo gerado automaticamente em Tue Jan 17 14:07:32 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.EntidadeConsignataria;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.ItemEntidadeConsignataria;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.RowEditEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "entidadeConsignatariaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoEntidadeConsignataria", pattern = "/entidadeconsignataria/novo/", viewId = "/faces/rh/administracaodepagamento/entidadeconsignataria/edita.xhtml"),
        @URLMapping(id = "editarEntidadeConsignataria", pattern = "/entidadeconsignataria/editar/#{entidadeConsignatariaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/entidadeconsignataria/edita.xhtml"),
        @URLMapping(id = "listarEntidadeConsignataria", pattern = "/entidadeconsignataria/listar/", viewId = "/faces/rh/administracaodepagamento/entidadeconsignataria/lista.xhtml"),
        @URLMapping(id = "verEntidadeConsignataria", pattern = "/entidadeconsignataria/ver/#{entidadeConsignatariaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/entidadeconsignataria/visualizar.xhtml")
})
public class EntidadeConsignatariaControlador extends PrettyControlador<EntidadeConsignataria> implements Serializable, CRUD {

    @EJB
    private EntidadeConsignatariaFacade entidadeConsignatariaFacade;
    @EJB
    private PessoaJuridicaFacade consignatarioFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private ItemEntidadeConsignatariaFacade itemEntidadeConsignatariaFacade;
    private ConverterAutoComplete converterConsignatario;
    private ConverterAutoComplete converterEventoFP;
    private ItemEntidadeConsignataria itemSelecionado;
    private ItemEntidadeConsignataria itemValidacao;

    public EntidadeConsignatariaControlador() {
        super(EntidadeConsignataria.class);
        itemValidacao = null;
    }

    @Override
    public AbstractFacade getFacede() {
        return entidadeConsignatariaFacade;
    }

    public List<PessoaJuridica> completaConsignatario(String parte) {
        return consignatarioFacade.listaFiltrandoRazaoSocialCNPJ(parte.trim());
    }

    public Converter getConverterConsignatario() {
        if (converterConsignatario == null) {
            converterConsignatario = new ConverterAutoComplete(PessoaJuridica.class, consignatarioFacade);
        }
        return converterConsignatario;
    }

    public ItemEntidadeConsignataria getItemSelecionado() {
        return itemSelecionado;
    }

    public ItemEntidadeConsignataria getItemValidacao() {
        return itemValidacao;
    }

    public void setItemValidacao(ItemEntidadeConsignataria itemValidacao) {
        this.itemValidacao = itemValidacao;
    }

    public void setItemSelecionado(ItemEntidadeConsignataria itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

    public List<EventoFP> completaEventoFP(String parte) {
        return eventoFPFacade.listaEventoFPNaoVigentesNoConsignatario(parte.trim());
    }

    public List<EventoFP> completaEventoFPAgrupador(String parte) {
        return eventoFPFacade.listaEventoFPDesconto(parte.trim());
    }

    public Converter getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterAutoComplete(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }

    public void addItemEntidadeConsignataria() {
        if (validaItem(itemSelecionado)) {
            EntidadeConsignataria entidade = (EntidadeConsignataria) selecionado;
            itemSelecionado.setEntidadeConsignataria(entidade);
            ((EntidadeConsignataria) selecionado).getItemEntidadeConsignatarias().add(itemSelecionado);
            itemSelecionado = new ItemEntidadeConsignataria();
        }
    }

    public void removeItemEntidadeConsignataria(ActionEvent e) {
        itemSelecionado = (ItemEntidadeConsignataria) e.getComponent().getAttributes().get("objeto");

        if (itemSelecionado.getId() != null) {
            if (!validaVigenciaItem(itemSelecionado.getInicioVigencia(), itemSelecionado.getFinalVigencia())) {
                return;
            }
        }
        //itemSelecionado.setEntidadeConsignataria(null);
        ((EntidadeConsignataria) selecionado).getItemEntidadeConsignatarias().remove(itemSelecionado);
        itemSelecionado = new ItemEntidadeConsignataria();
    }

    @URLAction(mappingId = "verEntidadeConsignataria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarEntidadeConsignataria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        itemValidacao = null;
        itemSelecionado = new ItemEntidadeConsignataria();

    }

    @URLAction(mappingId = "novoEntidadeConsignataria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        itemValidacao = null;
        itemSelecionado = new ItemEntidadeConsignataria();
        ((EntidadeConsignataria) selecionado).setItemEntidadeConsignatarias(new ArrayList<ItemEntidadeConsignataria>());
    }

    public boolean validaItem(ItemEntidadeConsignataria itemEntidadeConsignataria) {
        boolean validou = true;

        if (itemEntidadeConsignataria.getEventoFP() == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:panelItemEntidadeConsignataria:eventoFP", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção !", "O Evento é obrigatório !"));
            validou = false;
        }

        if (itemEntidadeConsignataria.getInicioVigencia() == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:panelItemEntidadeConsignataria:inicioVigencia", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção !", "O inicío de vigência do item é obrigatório !"));
            validou = false;
        } else if (itemEntidadeConsignataria.getFinalVigencia() != null) {
            if (itemEntidadeConsignataria.getInicioVigencia().after(itemEntidadeConsignataria.getFinalVigencia())) {
                FacesContext.getCurrentInstance().addMessage("Formulario:panelItemEntidadeConsignataria:inicioVigencia", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção !", "O inicío de vigência do item não pode ser superior ao final da vigência !"));
                validou = false;
            }
        }


        for (ItemEntidadeConsignataria item : ((EntidadeConsignataria) selecionado).getItemEntidadeConsignatarias()) {
            //verifica se o eventoFP selecionado já consta como vigente na lista de itens
            if ((item.getEventoFP().equals(itemEntidadeConsignataria.getEventoFP())) && (!item.equals(itemEntidadeConsignataria))) {
                if (item.getFinalVigencia() != null) {
                    if (itemEntidadeConsignataria.getInicioVigencia().before(item.getFinalVigencia())
                            || itemEntidadeConsignataria.getInicioVigencia().before(item.getInicioVigencia())) {
                        FacesContext.getCurrentInstance().addMessage("Formulario:panelItemEntidadeConsignataria:inicioVigencia", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", " A Data de Início da Vigência é inválida para este Evento!"));
                        validou = false;
                    }

                    if (itemEntidadeConsignataria.getFinalVigencia().before(item.getFinalVigencia())) {
                        FacesContext.getCurrentInstance().addMessage("Formulario:panelItemEntidadeConsignataria:finalVigencia", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", " A Data Final de Vigência é inválida para este Evento !"));
                        validou = false;
                    }

                    validou = validaVigenciaItem(itemEntidadeConsignataria.getInicioVigencia(), itemEntidadeConsignataria.getFinalVigencia());
                }
            }



        }

        return validou;
    }

    private boolean validaVigenciaItem(Date inicioVigencia, Date finalVigencia) {
        Date d = Util.getDataHoraMinutoSegundoZerado(new Date());
        if (finalVigencia != null) {
            if ((d.after(inicioVigencia) || d.equals(inicioVigencia))
                    && ((d.before(finalVigencia)) || (d.equals(finalVigencia)))) {
                FacesContext.getCurrentInstance().addMessage("Formulario:panelItemEntidadeConsignataria:eventoFP", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "O Evento selecionado é vigente na lista de itens!"));
                return false;
            } else {
                if ((d.after(inicioVigencia) || d.equals(inicioVigencia))
                        && ((d.before(d)) || (d.equals(d)))) {
                    FacesContext.getCurrentInstance().addMessage("Formulario:panelItemEntidadeConsignataria:eventoFP", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "O Evento selecionado é vigente na lista de itens !"));
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public Boolean validaCampos() {
        boolean valida = true;
        if (itemValidacao != null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:panelItemEntidadeConsignataria", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "O Período de vigência do item " + itemValidacao + " é inválido !"));
            valida = false;
        }
        if (entidadeConsignatariaFacade.existeCodigo((EntidadeConsignataria) selecionado)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção !", "O Código informado já está cadastrado em outra Entidade Consignatária");
            FacesContext.getCurrentInstance().addMessage("msg", msg);
            return false;
        }
//        if (entidadeConsignatariaFacade.existeConsignatario((EntidadeConsignataria) selecionado)) {
//            FacesContext.getCurrentInstance().addMessage("Formulario:consignatario", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "O Consignatário selecionado já está selecionado em uma outra Entidade Consignatária !"));
//            valida = false;
//        }

        for (ItemEntidadeConsignataria item : ((EntidadeConsignataria) selecionado).getItemEntidadeConsignatarias()) {
            if (!validaItem(item)) {
                valida = false;
            }

            for (ItemEntidadeConsignataria itemRecuperado : itemEntidadeConsignatariaFacade.listaItemComEventoFPComMesmaVigencia(item)) {
                if (!item.equals(itemRecuperado)) {
                    FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção",
                            "A Vigência do Item " + itemRecuperado.toString() + " do Consignatário "
                                    + itemRecuperado.getEntidadeConsignataria().getPessoaJuridica().toString() + " conflita com a vigência do item "
                                    + item.toString() + " deste cadastro !"));
                    valida = false;
                    break;
                }
            }
        }
        return valida;
    }

    public void onEditRow(RowEditEvent event) {
        ItemEntidadeConsignataria item = (ItemEntidadeConsignataria) event.getObject();
        if (validaItem(item)) {
            itemValidacao = null;
        } else {
            itemValidacao = item;
        }
    }

    @Override
    public void cancelar() {
        itemValidacao = null;
        itemSelecionado = new ItemEntidadeConsignataria();
        super.cancelar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/entidadeconsignataria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

}
