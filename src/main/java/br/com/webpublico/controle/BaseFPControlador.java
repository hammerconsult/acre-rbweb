/*
 * Codigo gerado automaticamente em Fri Sep 02 14:51:15 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.BaseFP;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.ItemBaseFP;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoValor;
import br.com.webpublico.enums.rh.administracaopagamento.IdentificadorBaseFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BaseFPFacade;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.negocios.ItemBaseFPFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ManagedBean(name = "baseFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoBaseFP", pattern = "/basefp/novo/", viewId = "/faces/rh/administracaodepagamento/basefp/edita.xhtml"),
    @URLMapping(id = "editarBaseFP", pattern = "/basefp/editar/#{baseFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/basefp/edita.xhtml"),
    @URLMapping(id = "editarCodigoBaseFP", pattern = "/basefp/codigo/#{baseFPControlador.codigo}/", viewId = "/faces/rh/administracaodepagamento/basefp/edita.xhtml"),
    @URLMapping(id = "listarBaseFP", pattern = "/basefp/listar/", viewId = "/faces/rh/administracaodepagamento/basefp/lista.xhtml"),
    @URLMapping(id = "verBaseFP", pattern = "/basefp/ver/#{baseFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/basefp/visualizar.xhtml"),

    @URLMapping(id = "duplicarBaseFP", pattern = "/basefp/duplicar/#{baseFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/basefp/edita.xhtml"),

})
public class BaseFPControlador extends PrettyControlador<BaseFP> implements Serializable, CRUD {

    @EJB
    private BaseFPFacade baseFPFacade;
    private ItemBaseFP itemBaseFP;
    @EJB
    private ItemBaseFPFacade itemBaseFPFacade;
    private ConverterGenerico converterItemBaseFP;
    private ConverterGenerico converterEventoFP;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private String codigo;
    private String filtroItem;

    public BaseFPControlador() {
        super(BaseFP.class);
    }

    public BaseFPFacade getFacade() {
        return baseFPFacade;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getFiltroItem() {
        return filtroItem;
    }

    public void setFiltroItem(String filtroItem) {
        this.filtroItem = filtroItem;
    }

    @Override
    public AbstractFacade getFacede() {
        return baseFPFacade;
    }

    public void setItemBaseFP(ItemBaseFP itemBaseFP) {
        this.itemBaseFP = itemBaseFP;
    }

    public ItemBaseFP getItemBaseFP() {
        return itemBaseFP;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/basefp/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "novoBaseFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setItensBasesFPs(new ArrayList<ItemBaseFP>());
        itemBaseFP = new ItemBaseFP();
        filtroItem = "";
    }

    @URLAction(mappingId = "editarBaseFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        Collections.sort(selecionado.getItensBasesFPs());
        itemBaseFP = new ItemBaseFP();
        filtroItem = "";
    }

    @URLAction(mappingId = "editarCodigoBaseFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarCodigo() {
        BaseFP b = baseFPFacade.getBaseByCodigo(codigo);
        if (b != null) {
            setId(b.getId());
            super.editar();
            Collections.sort(selecionado.getItensBasesFPs());
            itemBaseFP = new ItemBaseFP();
        }
        filtroItem = "";
    }

    @URLAction(mappingId = "duplicarBaseFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void duplicar() {
        editar();
        BaseFP novoObjeto = new BaseFP();
        operacao = Operacoes.NOVO;
        novoObjeto.setId(null);
        novoObjeto.setDescricao(null);
        novoObjeto.setCodigo(baseFPFacade.getSingletonGeradorCodigo().getProximoCodigo(BaseFP.class, "codigo").toString());
        novoObjeto.setDescricaoReduzida(null);
        novoObjeto.setFiltroBaseFP(selecionado.getFiltroBaseFP());
        novoObjeto.setItensBasesFPs(Lists.<ItemBaseFP>newArrayList());
        criarItemBaseFP(novoObjeto);
        this.selecionado = novoObjeto;
    }

    private void criarItemBaseFP(BaseFP novoObjeto) {
        List<ItemBaseFP> itemBaseFP = Lists.newArrayList();
        for (ItemBaseFP itensBasesFP : selecionado.getItensBasesFPs()) {
            ItemBaseFP novoItem = new ItemBaseFP();
            novoItem.setBaseFP(novoObjeto);
            novoItem.setEventoFP(itensBasesFP.getEventoFP());
            novoItem.setOperacaoFormula(itensBasesFP.getOperacaoFormula());
            novoItem.setDataRegistro(itensBasesFP.getDataRegistro());
            novoItem.setTipoValor(itensBasesFP.getTipoValor());
            novoItem.setSomaValorRetroativo(itensBasesFP.getSomaValorRetroativo());
            itemBaseFP.add(novoItem);
        }
        novoObjeto.getItensBasesFPs().addAll(itemBaseFP);
    }


    @URLAction(mappingId = "verBaseFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<ItemBaseFP> getItensBasesFPsFiltrado() {
        if (!"".equals(filtroItem.trim())) {
            List<ItemBaseFP> filtrado = Lists.newArrayList();
            for (ItemBaseFP baseFP : selecionado.getItensBasesFPs()) {
                if (baseFP.getEventoFP().toString().toLowerCase().contains(filtroItem.trim().toLowerCase())) {
                    filtrado.add(baseFP);
                }
            }
            return filtrado;
        }
        return selecionado.getItensBasesFPs();
    }

    public void validarItemBaseFP() {
        ValidacaoException ve = new ValidacaoException();
        if (itemBaseFP.getEventoFP() != null) {
            for (ItemBaseFP item : selecionado.getItensBasesFPs()) {
                if ((item.getEventoFP().equals(itemBaseFP.getEventoFP()))
                    && (item.getOperacaoFormula().equals(itemBaseFP.getOperacaoFormula()))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um item semelhante ao qual está sendo cadastrado!");
                    break;
                }
            }
        } else {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Evento Folha de Pagamento!");
        }
        if (itemBaseFP.getOperacaoFormula() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o campo Operação Fórmula!");
        }
        if (itemBaseFP.getTipoValor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o campo Tipo Valor!");
        }
        ve.lancarException();
    }


    public void addItemBaseFP() {
        try {
            validarItemBaseFP();

            itemBaseFP.setBaseFP(selecionado);
            selecionado.getItensBasesFPs().add(itemBaseFP);
            Collections.sort(selecionado.getItensBasesFPs());
            itemBaseFP = new ItemBaseFP();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removeItemBaseFP(ItemBaseFP item) {
        selecionado.getItensBasesFPs().remove(item);
    }

    public List<SelectItem> getEventoFP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (EventoFP object : eventoFPFacade.listaEventosAtivosFolha()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterGenerico(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }

    public List<SelectItem> getOperacaoFormula() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (OperacaoFormula object : OperacaoFormula.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoValores() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoValor object : TipoValor.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

  public List<SelectItem> getIdentificadores() {
        return Util.getListSelectItem(IdentificadorBaseFP.values(), false);
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }



    public Boolean validaCampos() {
        boolean retorno = Util.validaCampos(selecionado);
        if (retorno) {
            if (baseFPFacade.existeCodigoDaBaseFP((BaseFP) selecionado)) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "O Código da Base Folha de Pagamento já está cadastrado em outra Base Folha de Pagamento !", "O Código da Base Folha de Pagamento já está cadastrado em outra Base Folha de Pagamento");
                FacesContext.getCurrentInstance().addMessage("msg", msg);
                retorno = false;
            }
        }
        return retorno;
    }

    public void preencheBase1071() {
        baseFPFacade.isereBase1071();
    }

    public List<SelectItem> getBases() {
        return Util.getListSelectItem(baseFPFacade.lista());
    }
}
