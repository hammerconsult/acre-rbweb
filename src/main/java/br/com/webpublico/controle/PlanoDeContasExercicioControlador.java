/*
 * Codigo gerado automaticamente em Fri Apr 15 09:21:53 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.enums.ClasseDaConta;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PlanoDeContas;
import br.com.webpublico.entidades.PlanoDeContasExercicio;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.PlanoDeContasExercicioFacade;
import br.com.webpublico.negocios.PlanoDeContasFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "planoDeContasExercicioControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoplanodecontasexercicio", pattern = "/plano-de-contas-exercicio/novo/", viewId = "/faces/financeiro/planodecontas/planodecontasexercicio/edita.xhtml"),
        @URLMapping(id = "editarplanodecontasexercicio", pattern = "/plano-de-contas-exercicio/editar/#{planoDeContasExercicioControlador.id}/", viewId = "/faces/financeiro/planodecontas/planodecontasexercicio/edita.xhtml"),
        @URLMapping(id = "verplanodecontasexercicio", pattern = "/plano-de-contas-exercicio/ver/#{planoDeContasExercicioControlador.id}/", viewId = "/faces/financeiro/planodecontas/planodecontasexercicio/visualizar.xhtml"),
        @URLMapping(id = "listarplanodecontasexercicio", pattern = "/plano-de-contas-exercicio/listar/", viewId = "/faces/financeiro/planodecontas/planodecontasexercicio/lista.xhtml"),
})
public class PlanoDeContasExercicioControlador extends PrettyControlador<PlanoDeContasExercicio> implements Serializable, CRUD {

    @EJB
    private PlanoDeContasExercicioFacade planoDeContasExercicioFacade;
    private ConverterGenerico converterExercicio;
    private ConverterGenerico converterPlanoDeContasReceita, converterPlanoDeContasDestinacao, converterPlanoDeContasDespesa, converterPlanoDeContasContabil, converterPlanoExtraorcamentario, converterPlanoAuxiliar;
    private String exercicio;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public PlanoDeContasExercicioControlador() {
        super(PlanoDeContasExercicio.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/plano-de-contas-exercicio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public PlanoDeContasExercicioFacade getFacade() {
        return planoDeContasExercicioFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return planoDeContasExercicioFacade;
    }

    @URLAction(mappingId = "novoplanodecontasexercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        exercicio = null;
    }

    @URLAction(mappingId = "verplanodecontasexercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "editarplanodecontasexercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    @Override
    public void salvar() {
        try {
            if (validaCampos()
                    && validarParaMesmoExercicio()) {
                if (operacao.equals(Operacoes.NOVO)) {
                    planoDeContasExercicioFacade.salvarNovo(selecionado);
                } else {
                    planoDeContasExercicioFacade.salvar(selecionado);
                }
                FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
                redireciona();
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar. " + e.getMessage());
        }
    }

    private boolean validaCampos() {
        if (exercicio.isEmpty()
                || exercicio.equals("")) {
            FacesUtil.addCampoObrigatorio("O campo Exercício deve ser informado.");
            return false;
        }
        return true;
    }

    public void recuperaEditaVer() {
        operacao = Operacoes.EDITAR;
//        PlanoDeContasExercicio pce = (PlanoDeContasExercicio) evento.getComponent().getAttributes().get("objeto");
//        selecionado = pce;
        selecionado = planoDeContasExercicioFacade.recuperar(selecionado.getId());
//        exercicioSelecionado = pce.getExercicio();
        exercicio = ((PlanoDeContasExercicio) selecionado).getExercicio().toString();
    }

    public void validaExercicio(FacesContext context, UIComponent component, Object value) {
        if (exercicio != null) {
            Integer ano = Integer.parseInt(value.toString());
            FacesMessage message = new FacesMessage();
            List<Exercicio> exer = planoDeContasExercicioFacade.getExercicioFacade().lista();
            Exercicio ex = null;
            for (Exercicio e : exer) {
                if (e.getAno() != null) {
                    if (ano.compareTo(e.getAno()) == 0) {
                        ex = e;
                        ((PlanoDeContasExercicio) selecionado).setExercicio(ex);
                    }
                }
            }
            if (ex == null) {
                ((PlanoDeContasExercicio) selecionado).setExercicio(null);
                message.setDetail("Nenhum Exercício foi cadastrado para o ano de " + ano);
                message.setSummary("Operação não Permitida");
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(message);
            }
        }
    }

    public boolean validarParaMesmoExercicio() {
        if (planoDeContasExercicioFacade.validarParaMesmoExercicio(selecionado, exercicio)) {
            return true;
        }
        FacesUtil.addOperacaoNaoPermitida("Já existe um plano de contas exercício para o exercício de " + exercicio);
        return false;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Exercicio object : planoDeContasExercicioFacade.getExercicioFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
//        if (exercicioSelecionado != null) {
//            toReturn.add(new SelectItem(exercicioSelecionado, exercicioSelecionado.getAno().toString()));
//        }
        return toReturn;
    }

    public List<SelectItem> getPlanosContabeis() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
//        for (PlanoDeContas object : PlanoDeContasFacade.lista()) {
        for (PlanoDeContas object : planoDeContasExercicioFacade.getPlanoDeContasFacade().getListaPlanoDeContas(ClasseDaConta.CONTABIL)) {
            toReturn.add(new SelectItem(object, object.getDescricao() + " - " + object.getExercicio()));
        }
        return toReturn;
    }

    public List<SelectItem> getPlanosReceita() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PlanoDeContas object : planoDeContasExercicioFacade.getPlanoDeContasFacade().getListaPlanoDeContas(ClasseDaConta.RECEITA)) {
            toReturn.add(new SelectItem(object, object.getDescricao() + " - " + object.getExercicio()));
        }
        return toReturn;
    }

    public List<SelectItem> getPlanosDespesa() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PlanoDeContas object : planoDeContasExercicioFacade.getPlanoDeContasFacade().getListaPlanoDeContas(ClasseDaConta.DESPESA)) {
            toReturn.add(new SelectItem(object, object.getDescricao() + " - " + object.getExercicio()));
        }
        return toReturn;
    }

    public List<SelectItem> getPlanosDestinacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PlanoDeContas object : planoDeContasExercicioFacade.getPlanoDeContasFacade().getListaPlanoDeContas(ClasseDaConta.DESTINACAO)) {
            toReturn.add(new SelectItem(object, object.getDescricao() + " - " + object.getExercicio()));
        }
        return toReturn;
    }

    public List<SelectItem> getPlanosExtraorcamentario() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PlanoDeContas object : planoDeContasExercicioFacade.getPlanoDeContasFacade().getListaPlanoDeContas(ClasseDaConta.EXTRAORCAMENTARIA)) {
            toReturn.add(new SelectItem(object, object.getDescricao() + " - " + object.getExercicio()));
        }
        return toReturn;
    }

    public List<SelectItem> getPlanosAuxiliar() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PlanoDeContas object : planoDeContasExercicioFacade.getPlanoDeContasFacade().getListaPlanoDeContas(ClasseDaConta.AUXILIAR)) {
            toReturn.add(new SelectItem(object, object.getDescricao() + object.getExercicio()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, planoDeContasExercicioFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public ConverterGenerico getConverterPlanoContabil() {
        if (converterPlanoDeContasContabil == null) {
            converterPlanoDeContasContabil = new ConverterGenerico(PlanoDeContas.class, planoDeContasExercicioFacade.getPlanoDeContasFacade());
        }
        return converterPlanoDeContasContabil;
    }

    public ConverterGenerico getConverterPlanoReceita() {
        if (converterPlanoDeContasReceita == null) {
            converterPlanoDeContasReceita = new ConverterGenerico(PlanoDeContas.class, planoDeContasExercicioFacade.getPlanoDeContasFacade());
        }
        return converterPlanoDeContasReceita;
    }

    public ConverterGenerico getConverterPlanoDespesa() {
        if (converterPlanoDeContasDespesa == null) {
            converterPlanoDeContasDespesa = new ConverterGenerico(PlanoDeContas.class, planoDeContasExercicioFacade.getPlanoDeContasFacade());
        }
        return converterPlanoDeContasDespesa;
    }

    public ConverterGenerico getConverterPlanoDestinacao() {
        if (converterPlanoDeContasDestinacao == null) {
            converterPlanoDeContasDestinacao = new ConverterGenerico(PlanoDeContas.class, planoDeContasExercicioFacade.getPlanoDeContasFacade());
        }
        return converterPlanoDeContasDestinacao;
    }

    public ConverterGenerico getConverterPlanoExtraorcamentario() {
        if (converterPlanoExtraorcamentario == null) {
            converterPlanoExtraorcamentario = new ConverterGenerico(PlanoDeContas.class, planoDeContasExercicioFacade.getPlanoDeContasFacade());
        }
        return converterPlanoExtraorcamentario;
    }

    public ConverterGenerico getConverterPlanoAuxiliar() {
        if (converterPlanoAuxiliar == null) {
            converterPlanoAuxiliar = new ConverterGenerico(PlanoDeContas.class, planoDeContasExercicioFacade.getPlanoDeContasFacade());
        }
        return converterPlanoAuxiliar;
    }

    public String getExercicio() {
        return exercicio;
    }

    public void setExercicio(String exercicio) {
        this.exercicio = exercicio;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
