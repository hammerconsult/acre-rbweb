/*
 * Codigo gerado automaticamente em Tue Jan 10 14:40:47 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.enums.TipoGrupoExportacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.nfse.domain.PremioSorteioNfse;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "grupoExportacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoGrupoExportacao", pattern = "/grupo-exportacao/novo/", viewId = "/faces/rh/administracaodepagamento/grupoexportacao/edita.xhtml"),
    @URLMapping(id = "editarGrupoExportacao", pattern = "/grupo-exportacao/editar/#{grupoExportacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/grupoexportacao/edita.xhtml"),
    @URLMapping(id = "listarGrupoExportacao", pattern = "/grupo-exportacao/listar/", viewId = "/faces/rh/administracaodepagamento/grupoexportacao/lista.xhtml"),
    @URLMapping(id = "verGrupoExportacao", pattern = "/grupo-exportacao/ver/#{grupoExportacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/grupoexportacao/visualizar.xhtml")
})
public class GrupoExportacaoControlador extends PrettyControlador<GrupoExportacao> implements Serializable, CRUD {

    @EJB
    private GrupoExportacaoFacade grupoExportacaoFacade;
    @EJB
    private ModuloExportacaoFacade moduloExportacaoFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterModuloExportacao;
    private ConverterAutoComplete converterContaExtraorcamentaria;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private ConverterAutoComplete converterEventoFP;
    @EJB
    private BaseFPFacade baseFPFacade;
    @EJB
    private ContaFacade contaFacade;
    private ConverterAutoComplete converterBaseFP;
    private ItemGrupoExportacao itemGrupoExportacao;
    private ItemGrupoExportacaoContabil itemGrupoExportacaoContabil;
    private boolean isEditandoItemGrupo = false;
    private boolean isEditandoItemGrupoContabil = false;

    public GrupoExportacaoControlador() {
        super(GrupoExportacao.class);
    }

    public GrupoExportacaoFacade getFacade() {
        return grupoExportacaoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return grupoExportacaoFacade;
    }

    public List<SelectItem> getModuloExportacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (ModuloExportacao object : moduloExportacaoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoGrupoExportacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoGrupoExportacao object : TipoGrupoExportacao.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterModuloExportacao() {
        if (converterModuloExportacao == null) {
            converterModuloExportacao = new ConverterAutoComplete(ModuloExportacao.class, moduloExportacaoFacade);
        }
        return converterModuloExportacao;
    }

    public ConverterAutoComplete getConverterContaExtraorcamentaria() {
        if (converterContaExtraorcamentaria == null) {
            converterContaExtraorcamentaria = new ConverterAutoComplete(Conta.class, contaFacade);
        }
        return converterContaExtraorcamentaria;
    }

    public List<EventoFP> completaEventoFP(String parte) {
        return eventoFPFacade.listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    public List<BaseFP> completaBaseFP(String parte) {
        return baseFPFacade.listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    public List<ContaExtraorcamentaria> completarContaExtra(String parte) {
        return contaFacade.listaFiltrandoTodasContaExtraPorExercicio(parte.trim(), sistemaControlador.getExercicioCorrente());
    }

    public Converter getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterAutoComplete(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }

    public Converter getConverterBaseFP() {
        if (converterBaseFP == null) {
            converterBaseFP = new ConverterAutoComplete(BaseFP.class, baseFPFacade);
        }
        return converterBaseFP;
    }

    @URLAction(mappingId = "editarGrupoExportacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        itemGrupoExportacao = new ItemGrupoExportacao();
        itemGrupoExportacaoContabil = new ItemGrupoExportacaoContabil();
    }

    @URLAction(mappingId = "verGrupoExportacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "novoGrupoExportacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ((GrupoExportacao) selecionado).setItensGruposExportacoes(new ArrayList<ItemGrupoExportacao>());
        itemGrupoExportacao = new ItemGrupoExportacao();
        itemGrupoExportacaoContabil = new ItemGrupoExportacaoContabil();
    }

    public void addItemGrupo() {
        if (itemGrupoExportacao.getEventoFP() == null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção !", "Selecione um evento !");
            FacesContext.getCurrentInstance().addMessage("msgs", msg);
            return;
        }

        for (ItemGrupoExportacao item : ((GrupoExportacao) selecionado).getItensGruposExportacoes()) {
            if (item.getBaseFP() != null) {
                if (item.getBaseFP().equals(itemGrupoExportacao.getBaseFP())) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção !", "Já existe um item semelhante ao qual está sendo cadastrado !");
                    FacesContext.getCurrentInstance().addMessage("msgs", msg);
                    return;
                }
            }
        }

        GrupoExportacao grupo = (GrupoExportacao) selecionado;
        itemGrupoExportacao.setGrupoExportacao(grupo);
        Util.adicionarObjetoEmLista(selecionado.getItensGruposExportacoes(), itemGrupoExportacao);
        itemGrupoExportacao = new ItemGrupoExportacao();
        setEditandoItemGrupo(false);
    }

    public void editarItemGrupo(ItemGrupoExportacao itemGrupo) {
        setEditandoItemGrupo(true);
        this.itemGrupoExportacao = (ItemGrupoExportacao) Util.clonarObjeto(itemGrupo);
    }

    public boolean isEditandoItemGrupo() {
        return isEditandoItemGrupo;
    }

    public void setEditandoItemGrupo(boolean editandoItemGrupo) {
        isEditandoItemGrupo = editandoItemGrupo;
    }

    public void adicionarItemGrupoContabil() {
        try {
            validarItemGrupoContabil();
            itemGrupoExportacaoContabil.setGrupoExportacao(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getItensGrupoExportacaoContabil(), itemGrupoExportacaoContabil);
            itemGrupoExportacaoContabil = new ItemGrupoExportacaoContabil();
            setEditandoItemGrupoContabil(false);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void editarItemGrupoContabil(ItemGrupoExportacaoContabil itemGrupoContabil) {
        setEditandoItemGrupoContabil(true);
        this.itemGrupoExportacaoContabil = (ItemGrupoExportacaoContabil) Util.clonarObjeto(itemGrupoContabil);
    }

    public boolean isEditandoItemGrupoContabil() {
        return isEditandoItemGrupoContabil;
    }

    public void setEditandoItemGrupoContabil(boolean editandoItemGrupoContabil) {
        isEditandoItemGrupoContabil = editandoItemGrupoContabil;
    }

    private void validarItemGrupoContabil() {
        ValidacaoException ve = new ValidacaoException();
        if (itemGrupoExportacaoContabil.getClasseCredor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Classe de Pessoa deve ser informado.");
        }
        if (StringUtils.isBlank(itemGrupoExportacaoContabil.getCodigo())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Código de Receita Dirf PF deve ser informado.");
        }
        if (StringUtils.isBlank(itemGrupoExportacaoContabil.getCodigoPj())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Código de Receita Dirf PJ deve ser informado.");
        }
        if (itemGrupoExportacaoContabil.getPercentual() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Percentual deve ser informado.");
        }
        ve.lancarException();

        if (itemGrupoExportacaoContabil.getPercentual().compareTo(BigDecimal.ZERO) <= 0 || itemGrupoExportacaoContabil.getPercentual().compareTo(new BigDecimal(100)) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo percentual deve ser maior que 0 e menor que 100.");
        }
        for (ItemGrupoExportacaoContabil item : selecionado.getItensGrupoExportacaoContabil()) {
            if (item.getContaExtraorcamentaria() != null && itemGrupoExportacaoContabil.getContaExtraorcamentaria() != null) {
                if (itemGrupoExportacaoContabil.getContaExtraorcamentaria().equals(item.getContaExtraorcamentaria())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Conta Extraorçamentária selecionada já está adicionada.");
                }
            }
        }
        ve.lancarException();
    }

    public void removeItemGrupo(ActionEvent e) {
        itemGrupoExportacao = (ItemGrupoExportacao) e.getComponent().getAttributes().get("objeto");
        selecionado.getItensGruposExportacoes().remove(itemGrupoExportacao);
        itemGrupoExportacao = new ItemGrupoExportacao();
    }

    public void removeItemGrupoContabil(ActionEvent e) {
        itemGrupoExportacaoContabil = (ItemGrupoExportacaoContabil) e.getComponent().getAttributes().get("objeto");
        selecionado.getItensGrupoExportacaoContabil().remove(itemGrupoExportacaoContabil);
        itemGrupoExportacaoContabil = new ItemGrupoExportacaoContabil();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/grupo-exportacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ItemGrupoExportacao getItemGrupoExportacao() {
        return itemGrupoExportacao;
    }

    public void setItemGrupoExportacao(ItemGrupoExportacao itemGrupoExportacao) {
        this.itemGrupoExportacao = itemGrupoExportacao;
    }

    public List<SelectItem> getOperacaoFormula() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (OperacaoFormula object : OperacaoFormula.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<ModuloExportacao> completaModuloExportacao(String parte) {
        return moduloExportacaoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public void setNullEventoFP() {
        itemGrupoExportacao.setEventoFP(null);
    }

    public void setNullBaseFP() {
        itemGrupoExportacao.setBaseFP(null);
    }

    public void alterarTipoItem(SelectEvent ev) {
        EventoFP e = (EventoFP) ev.getObject();
        if (e.getTipoEventoFP().equals(TipoEventoFP.VANTAGEM)) {
            itemGrupoExportacao.setOperacaoFormula(OperacaoFormula.ADICAO);
        }
        if (e.getTipoEventoFP().equals(TipoEventoFP.DESCONTO)) {
            itemGrupoExportacao.setOperacaoFormula(OperacaoFormula.SUBTRACAO);
        }
    }

    public ItemGrupoExportacaoContabil getItemGrupoExportacaoContabil() {
        return itemGrupoExportacaoContabil;
    }

    public void setItemGrupoExportacaoContabil(ItemGrupoExportacaoContabil itemGrupoExportacaoContabil) {
        this.itemGrupoExportacaoContabil = itemGrupoExportacaoContabil;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
