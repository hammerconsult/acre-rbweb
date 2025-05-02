/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author terminal3
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-planejamento-estrategico", pattern = "/planejamento-estrategico/novo/", viewId = "/faces/financeiro/ppa/planejamentoestrategico/edita.xhtml"),
    @URLMapping(id = "editar-planejamento-estrategico", pattern = "/planejamento-estrategico/editar/#{planejamentoEstrategicoControlador.id}/", viewId = "/faces/financeiro/ppa/planejamentoestrategico/edita.xhtml"),
    @URLMapping(id = "ver-planejamento-estrategico", pattern = "/planejamento-estrategico/ver/#{planejamentoEstrategicoControlador.id}/", viewId = "/faces/financeiro/ppa/planejamentoestrategico/visualizar.xhtml"),
    @URLMapping(id = "listar-planejamento-estrategico", pattern = "/planejamento-estrategico/listar/", viewId = "/faces/financeiro/ppa/planejamentoestrategico/lista.xhtml")
})
public class PlanejamentoEstrategicoControlador extends PrettyControlador<PlanejamentoEstrategico> implements Serializable, CRUD {

    @EJB
    private PlanejamentoEstrategicoFacade planejamentoEstrategicoFacade;
    @EJB
    private ItemPlanejamentoEstrategicoFacade itemPlanejamentoEstrategicoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    protected ConverterGenerico converterExercicio;
    private ConverterGenerico converterEixo;
    private transient Converter converterItemSuperior;
    private ExercicioPlanoEstrategico exercicioPlanoEstrategico;
    private ItemPlanejamentoEstrategico itemPlan;
    private ItemPlanejamentoEstrategico itemPlanSuperior;
    private ItemPlanejamentoEstrategico itemPlanAux;
    private MacroObjetivoEstrategico macroObjEst;
    private Exercicio exercicio;

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "novo-planejamento-estrategico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ((PlanejamentoEstrategico) selecionado).setItens(new ArrayList<ItemPlanejamentoEstrategico>());
        ((PlanejamentoEstrategico) selecionado).setMacros(new ArrayList<MacroObjetivoEstrategico>());
        ((PlanejamentoEstrategico) selecionado).setExerciciosPlanoEstrategico(new ArrayList<ExercicioPlanoEstrategico>());
        exercicioPlanoEstrategico = new ExercicioPlanoEstrategico();
        itemPlan = new ItemPlanejamentoEstrategico();
        itemPlanAux = new ItemPlanejamentoEstrategico();
        itemPlanSuperior = new ItemPlanejamentoEstrategico();
        macroObjEst = new MacroObjetivoEstrategico();
        exercicio = new Exercicio();
    }

    @URLAction(mappingId = "editar-planejamento-estrategico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionar();
    }

    @URLAction(mappingId = "ver-planejamento-estrategico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionar();
    }

    public void selecionar() {
        itemPlan = new ItemPlanejamentoEstrategico();
        operacao = Operacoes.EDITAR;
        selecionado = planejamentoEstrategicoFacade.recuperar(selecionado.getId());
        exercicioPlanoEstrategico = new ExercicioPlanoEstrategico();
        itemPlanAux = new ItemPlanejamentoEstrategico();
        itemPlanSuperior = new ItemPlanejamentoEstrategico();
        macroObjEst = new MacroObjetivoEstrategico();
        exercicio = new Exercicio();
        if (selecionado.getItens() != null && !selecionado.getItens().isEmpty()) {
            ordernarItens();
        }
    }

    public List<ItemPlanejamentoEstrategico> getItens() {
        return ((PlanejamentoEstrategico) selecionado).getItens();
    }

    public List<SelectItem> getItemSuperiores() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ItemPlanejamentoEstrategico object : itemPlanejamentoEstrategicoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Exercicio object : exercicioFacade.listaNaoUtilizados()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getItensPlanSuperior() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ItemPlanejamentoEstrategico object : itemPlanejamentoEstrategicoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public List<MacroObjetivoEstrategico> getMacrosTabela() {
        return ((PlanejamentoEstrategico) selecionado).getMacros();
    }

    private Boolean validaExercicio() {
        if (exercicio.getAno() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Ano deve ser informado.");
            return false;
        } else {
            try {
                if (exercicioFacade.getExercicioPorAno(exercicio.getAno()) == null) {
                    FacesUtil.addOperacaoNaoPermitida("Exercício não cadastrado.");
                    return false;
                }
            } catch (ExcecaoNegocioGenerica e) {
                FacesUtil.addOperacaoNaoPermitida(e.getMessage());
                return false;
            }

            if (!existeExercicioIgualEmOutroPlanejamento(exercicio.getAno())) {
                FacesUtil.addOperacaoNaoPermitida("O exercício selecionado já foi adicionado em outro Planejamento Estratégico");
                return false;
            } else {
                for (ExercicioPlanoEstrategico epe : selecionado.getExerciciosPlanoEstrategico()) {
                    if (epe.getExercicio().getAno().equals(exercicio.getAno())) {
                        FacesUtil.addOperacaoNaoPermitida("O exercício selecionado já foi adicionado.");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void novoExercicioPlanoEstrategico() {
        if (validaExercicio()) {
            exercicio = exercicioFacade.getExercicioPorAno(exercicio.getAno());
            exercicioPlanoEstrategico.setPlanejamentoEstrategico((PlanejamentoEstrategico) selecionado);
            exercicioPlanoEstrategico.setExercicio(exercicio);
            ((PlanejamentoEstrategico) selecionado).getExerciciosPlanoEstrategico().add(exercicioPlanoEstrategico);
            exercicioPlanoEstrategico = new ExercicioPlanoEstrategico();
            exercicio = new Exercicio();
        }
    }

    public void adicionarItemPlan() {
        try {
            validarItemPlan();
            itemPlan.setDataRegistro(getSistemaControlador().getDataOperacao());
            itemPlan.setPlanejamentoEstrategico((PlanejamentoEstrategico) selecionado);
            if(Strings.isNullOrEmpty(itemPlan.getCodigo())) {
                itemPlan.setCodigo(pegaProximoCodigoDoSuperior());
            }
            Util.adicionarObjetoEmLista(selecionado.getItens(), itemPlan);
            itemPlan = new ItemPlanejamentoEstrategico();
            ordernarItens();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarItemPlan() {
        ValidacaoException ve = new ValidacaoException();

        if (itemPlan.getMacroObjetivoEstrategico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Eixo Estratégico deve ser informado.");
        }
        if(Strings.isNullOrEmpty(itemPlan.getDescricao())){
            ve.adicionarMensagemDeCampoObrigatorio("O campo Objetivo do Eixo deve ser informado");
        }
        if (getMacroOE() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Eixo Estratégico deve ser informado.");
        }
        ve.lancarException();
    }

    public void limparItemPlanEstrategico() {
        itemPlan = new ItemPlanejamentoEstrategico();
    }

    public String pegaProximoCodigoDoSuperior() {
        Integer maiorCodigo = 0;
        for (ItemPlanejamentoEstrategico o : selecionado.getItens()) {
            if (!o.getCodigo().contains(".")) {
                Integer codigoDaVez = Integer.parseInt(o.getCodigo());
                if (maiorCodigo == null) {
                    maiorCodigo = codigoDaVez;
                } else {
                    if (codigoDaVez > maiorCodigo) {
                        maiorCodigo = codigoDaVez;
                    }
                }
            }
        }
        return String.valueOf(maiorCodigo + 1);
    }

    public String pegaProximoCodigoDoSuperior(ItemPlanejamentoEstrategico superior) {
        Integer maiorCodigo = 0;
        for (ItemPlanejamentoEstrategico o : selecionado.getItens()) {
            if (o.getCodigo().startsWith(superior.getCodigo()) && !o.getCodigo().equals(superior.getCodigo())) {
                String[] codigos = o.getCodigo().split("\\.");
                Integer codigoDaVez = Integer.parseInt(codigos[codigos.length - 1]);
                if (maiorCodigo == null) {
                    maiorCodigo = codigoDaVez;
                } else {
                    if (codigoDaVez > maiorCodigo) {
                        maiorCodigo = codigoDaVez;
                    }
                }
            }
        }
        return superior.getCodigo() + "." + String.valueOf(maiorCodigo + 1);
    }

    public void ordernarItens() {
        Collections.sort(selecionado.getItens(), new Comparator<ItemPlanejamentoEstrategico>() {
            @Override
            public int compare(ItemPlanejamentoEstrategico o1, ItemPlanejamentoEstrategico o2) {
                Integer i1 = Integer.parseInt(o1.getCodigo());
                Integer i2 = Integer.parseInt(o2.getCodigo());
                return i1.compareTo(i2);
            }
        });
    }

    public void novoItem() {
        itemPlan = new ItemPlanejamentoEstrategico();
    }

    public void adicionarMacroEstrategico() {
        if (macroObjEst == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Descrição deve ser informado.");
        } else if (macroObjEst.getDescricao().trim().equals("")) {
            FacesUtil.addOperacaoNaoPermitida("O campo Descrição deve ser informado.");
        } else {
            macroObjEst.setPlanejamentoEstrategico((PlanejamentoEstrategico) selecionado);
            Util.adicionarObjetoEmLista(selecionado.getMacros(), macroObjEst);
            macroObjEst = new MacroObjetivoEstrategico();
        }
    }

    public void limparMacroEstrategico() {
        macroObjEst = new MacroObjetivoEstrategico();
    }

    public void removeExercicioPlanoEstrategico(ActionEvent evento) {
        ((PlanejamentoEstrategico) selecionado).getExerciciosPlanoEstrategico().remove((ExercicioPlanoEstrategico) evento.getComponent().getAttributes().get("removeExercicio"));
    }

    public void removeItem(ActionEvent evento) {
        PlanejamentoEstrategico pe = (PlanejamentoEstrategico) selecionado;
        ItemPlanejamentoEstrategico item = (ItemPlanejamentoEstrategico) evento.getComponent().getAttributes().get("removeItem");

        if (podeRemoverItem(pe, item)) {
            ((PlanejamentoEstrategico) selecionado).getItens().remove(item);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Existem itens filhos deste item."));
        }
    }

    private boolean podeRemoverItem(PlanejamentoEstrategico pe, ItemPlanejamentoEstrategico item) {
        boolean podeRemoverOItem = true;
        for (ItemPlanejamentoEstrategico it : pe.getItens()) {
            if (it.getItemSuperior() != null) {
                if (it.getItemSuperior().equals(item)) {
                    podeRemoverOItem = false;
                }
            }
        }
        return podeRemoverOItem;
    }

    public void adicionarDesdobramentoItem(ActionEvent evento) {
        itemPlan.setItemSuperior((ItemPlanejamentoEstrategico) evento.getComponent().getAttributes().get("item"));
        itemPlanAux = ((ItemPlanejamentoEstrategico) evento.getComponent().getAttributes().get("item"));

    }

    public void removeMacro(ActionEvent evento) {
        PlanejamentoEstrategico pe = ((PlanejamentoEstrategico) selecionado);
        pe.getMacros().remove((MacroObjetivoEstrategico) evento.getComponent().getAttributes().get("removeMacro"));
    }

    public List<ExercicioPlanoEstrategico> getExerciciosPlanoEstrategicos() {
        List<ExercicioPlanoEstrategico> toReturn = ((PlanejamentoEstrategico) selecionado).getExerciciosPlanoEstrategico();
        Collections.sort(toReturn, new Comparator<ExercicioPlanoEstrategico>() {
            @Override
            public int compare(ExercicioPlanoEstrategico o1, ExercicioPlanoEstrategico o2) {
                return o1.getExercicio().getAno().compareTo(o2.getExercicio().getAno());
            }
        });
        return toReturn;
    }

    public void editarItemPlanejamento(ItemPlanejamentoEstrategico itemPlanejamentoEstrategico) {
        itemPlan = (ItemPlanejamentoEstrategico) Util.clonarObjeto(itemPlanejamentoEstrategico);
    }

    public void editarMacroEstrategico(MacroObjetivoEstrategico macroObjetivoEstrategico) {
        macroObjEst = (MacroObjetivoEstrategico) Util.clonarObjeto(macroObjetivoEstrategico);
    }

    public PlanejamentoEstrategicoControlador() {
        super(PlanejamentoEstrategico.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return planejamentoEstrategicoFacade;
    }

    public PlanejamentoEstrategicoFacade getPlanejamentoEstrategicoFacade() {
        return planejamentoEstrategicoFacade;
    }

    public ItemPlanejamentoEstrategico getItemPlan() {
        return itemPlan;
    }

    public void setItemPlan(ItemPlanejamentoEstrategico itemPlan) {
        this.itemPlan = (ItemPlanejamentoEstrategico) Util.clonarObjeto(itemPlan);
    }

    public ItemPlanejamentoEstrategico getItemPlanAux() {
        return itemPlanAux;
    }

    public void setItemPlanAux(ItemPlanejamentoEstrategico itemPlanAux) {
        this.itemPlanAux = itemPlanAux;
    }

    public MacroObjetivoEstrategico getMacroObjEst() {
        return macroObjEst;
    }

    public void setMacroObjEst(MacroObjetivoEstrategico macroObjEst) {
        this.macroObjEst = macroObjEst;
    }

    public ItemPlanejamentoEstrategico getItemPlanSuperior() {
        return itemPlanSuperior;
    }

    public void setItemPlanSuperior(ItemPlanejamentoEstrategico itemPlanSuperior) {
        this.itemPlanSuperior = itemPlanSuperior;
    }

    public ItemPlanejamentoEstrategicoFacade getItemPlanejamentoEstrategicoFacade() {
        return itemPlanejamentoEstrategicoFacade;
    }

    public void setItemPlanejamentoEstrategicoFacade(ItemPlanejamentoEstrategicoFacade itemPlanejamentoEstrategicoFacade) {
        this.itemPlanejamentoEstrategicoFacade = itemPlanejamentoEstrategicoFacade;
    }

    public ExercicioPlanoEstrategico getExercicioPlanoEstrategico() {
        return exercicioPlanoEstrategico;
    }

    public void setExercicioPlanoEstrategico(ExercicioPlanoEstrategico exercicioPlanoEstrategico) {
        this.exercicioPlanoEstrategico = exercicioPlanoEstrategico;
    }

    public ConverterGenerico getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
    }

    public Converter getConverterEixo() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
                for (MacroObjetivoEstrategico macroObjetivoEstrategico : selecionado.getMacros()) {
                    if (macroObjetivoEstrategico.getCriadoEm().equals(Long.valueOf(string))) {
                        return macroObjetivoEstrategico;
                    }
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext fc, UIComponent uic, Object o) {
                return o != null ? ((MacroObjetivoEstrategico) o).getCriadoEm().toString() : "";
            }
        };
    }

    public Converter getConverterItemSuperior() {
        if (converterItemSuperior == null) {
            converterItemSuperior = new Converter() {
                @Override
                public Object getAsObject(FacesContext context, UIComponent component, String value) {
                    int hashCode = Integer.parseInt(value);
                    PlanejamentoEstrategico pe = (PlanejamentoEstrategico) selecionado;
                    for (ItemPlanejamentoEstrategico ipe : pe.getItens()) {
                        if (ipe.getNome().hashCode() == hashCode) {
                            return ipe;
                        }
                    }
                    return null;
                }

                @Override
                public String getAsString(FacesContext context, UIComponent component, Object value) {
                    ItemPlanejamentoEstrategico ipe = (ItemPlanejamentoEstrategico) value;
                    return "" + ipe.getNome().hashCode();
                }
            };
        }
        return converterItemSuperior;
    }

    @Override
    public void salvar() {
        PlanejamentoEstrategico pe = ((PlanejamentoEstrategico) selecionado);
        if (validaCampos()) {
            if (operacao.equals(Operacoes.NOVO)) {
                try {
                    planejamentoEstrategicoFacade.salvarNovo(selecionado);
                    FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
                    redireciona();
                } catch (Exception ex) {
                    FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
                }
            } else {
                try {
                    planejamentoEstrategicoFacade.salvar(selecionado);
                    FacesUtil.addOperacaoRealizada("Registro alterado com sucesso.");
                    redireciona();
                } catch (Exception ex) {
                    FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
                }
            }
        }
    }

    public Boolean validaCampos() {
        Boolean retorno = Util.validaCampos(selecionado);
        if (((PlanejamentoEstrategico) selecionado).getExerciciosPlanoEstrategico().size() < 4) {
            FacesUtil.addOperacaoNaoPermitida("Deve ser informado um número mínimo de quatro exercícios por planejamento estratégico.");
            retorno = false;
        }
        return retorno;
    }

    public Boolean existeExercicioIgualEmOutroPlanejamento(Integer ano) {
        if (ano != null) {
            List<Exercicio> listaNaoUtilizado = exercicioFacade.listaNaoUtilizados();
            if (!listaNaoUtilizado.isEmpty()) {
                for (Exercicio e : listaNaoUtilizado) {
                    if (e.getAno().compareTo(ano) == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<SelectItem> getMacroOE() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (MacroObjetivoEstrategico object : selecionado.getMacros()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }

        return toReturn;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/planejamento-estrategico/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
