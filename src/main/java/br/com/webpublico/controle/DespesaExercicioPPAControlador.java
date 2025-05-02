/*
 * Codigo gerado automaticamente em Tue Apr 26 17:48:46 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ManagedBean
@SessionScoped
public class DespesaExercicioPPAControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private DespesaExercicioPPAFacade despesaExercicioPPAFacade;
    @EJB
    private ContaFacade contaFacade;
    private ConverterGenerico converterContaDeDespesa;
    public Conta conta;
    @EJB
    private ExercicioFacade exercicioFacade;
    private ConverterGenerico converterExercicio;
    @EJB
    private PPAFacade ppaFacade;
    private PPA ppaSelecionado;
    private ConverterAutoComplete converterPpa;
    private List<DespesaExercicioPPA> listaDe;
    private Exercicio exercicioSelecionado;
    private List<DespesaExercicioPPA> listaD;
    private List<Conta> listaC;
    private MoneyConverter moneyConverter;
    private String exercicio;

    @Override
    public void novo() {
        super.novo();
        exercicio = null;
    }

    @Override
    public String salvar() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        String pathReal = servletContext.getContextPath();
        if (validaPpaSelecionado()) {


            try {
                for (DespesaExercicioPPA deppa : listaDe) {
                    if (deppa.getValorEstimado() != null) {
                        DespesaExercicioPPA despRecuperado = despesaExercicioPPAFacade.recuperar(deppa);
                        if (despRecuperado != null) {
                            despesaExercicioPPAFacade.salvar(deppa);
                        } else {
                            selecionado = deppa;
                            super.salvar();
                        }
                    }
                }
                listaDe = new ArrayList<DespesaExercicioPPA>();
                FacesContext.getCurrentInstance().getExternalContext().redirect(pathReal + "/faces/sucesso.xhtml");
                return null;
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "Problema ao Salvar"));
                return null;
            }
        }
        return null;
    }

    public boolean validaPpaSelecionado() {
        if (ppaSelecionado == null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Ops", "O PPA não foi selecionado");
            FacesContext.getCurrentInstance().addMessage("Formulario", msg);
            return false;
        }
        if (ppaSelecionado.getAprovacao() != null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Não e possivel alterar as despesas", "o PPA selecionada jo foi aprovado");
            FacesContext.getCurrentInstance().addMessage("Formulario", msg);
            return false;
        }
        return true;
    }

    @Override
    public void selecionar(ActionEvent evento) {
        operacao = Operacoes.EDITAR;
        DespesaExercicioPPA d = (DespesaExercicioPPA) evento.getComponent().getAttributes().get("objeto");
        selecionado = despesaExercicioPPAFacade.recuperar(d.getId());
        exercicioSelecionado = d.getExercicio();
    }

    public DespesaExercicioPPAControlador() {
        metadata = new EntidadeMetaData(DespesaExercicioPPA.class);
    }

    public DespesaExercicioPPAFacade getFacade() {
        return despesaExercicioPPAFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return despesaExercicioPPAFacade;
    }

    public void recebeConta(ActionEvent event) {
        conta = (Conta) event.getComponent().getAttributes().get("obj");

    }

    public List<SelectItem> getContaDeDespesa() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Conta object : contaFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Exercicio object : exercicioFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterPpa() {
        if (converterPpa == null) {
            converterPpa = new ConverterAutoComplete(PPA.class, ppaFacade);
        }
        return converterPpa;
    }

    public List<PPA> completaPPA(String parte) {
        if (((DespesaExercicioPPA) selecionado).getExercicio() != null) {
            return ppaFacade.listaTodosPpaExercicio(((DespesaExercicioPPA) selecionado).getExercicio(), parte);
        } else {
            return new ArrayList<PPA>();
        }
    }

    public ConverterGenerico getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
    }

    public ConverterGenerico getConverterContaDeDespesa() {
        if (converterContaDeDespesa == null) {
            converterContaDeDespesa = new ConverterGenerico(Conta.class, contaFacade);
        }
        return converterContaDeDespesa;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void editaTabela(SelectEvent i) {
        ppaSelecionado = (PPA) i.getObject();
        if ((exercicioSelecionado != null) && (ppaSelecionado != null)) {
            listaDe = getMontaTabela();
        } else {
            listaDe = new ArrayList<DespesaExercicioPPA>();
        }
    }

    public List<DespesaExercicioPPA> getMontaTabela() {
        listaExercicioDespesa();
        return listaDe;
    }

    public void listaExercicioDespesa() {
        listaDe = new ArrayList<DespesaExercicioPPA>();
        listaC = despesaExercicioPPAFacade.listaContaDespesa(exercicioSelecionado);
        listaD = despesaExercicioPPAFacade.listaIdPPA(exercicioSelecionado.getId(), ppaSelecionado.getId());
        for (DespesaExercicioPPA despesa : listaD) {
            if (listaC.contains(despesa.getContaDeDespesa())) {
                listaC.remove(despesa.getContaDeDespesa());
                listaDe.add(despesa);
            }
        }
        for (Conta c : listaC) {
            DespesaExercicioPPA de = new DespesaExercicioPPA();
            Conta contaRec = contaFacade.recuperar(c);
            if (contaRec.getCategoria().equals(CategoriaConta.ANALITICA)) {
                de.setValorEstimado(new BigDecimal(BigInteger.ZERO));
            } else {
                de.setValorEstimado(null);
            }
            de.setContaDeDespesa(c);
            de.setExercicio(exercicioSelecionado);
            de.setPpa(ppaSelecionado);
            listaDe.add(de);
            Collections.sort(listaDe);
        }
    }

    public List<DespesaExercicioPPA> despesaExercicioPPAs() {
        return null;
    }

    public List<Conta> getContas() {
        return contaFacade.lista();
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public PPA getPpaSelecionado() {
        return ppaSelecionado;
    }

    public void setPpaSelecionado(PPA ppaSelecionado) {
        this.ppaSelecionado = ppaSelecionado;
    }

    public Exercicio getExercicioSelecionado() {
        return exercicioSelecionado;
    }

    public void setExercicioSelecionado(Exercicio exercicioSelecionado) {
        this.exercicioSelecionado = exercicioSelecionado;
    }

    public List<Conta> getListaC() {
        return listaC;
    }

    public void setListaC(List<Conta> listaC) {
        this.listaC = listaC;
    }

    public List<DespesaExercicioPPA> getListaD() {
        return listaD;
    }

    public void setListaD(List<DespesaExercicioPPA> listaD) {
        this.listaD = listaD;
    }

    public List<DespesaExercicioPPA> getListaDe() {
        return listaDe;
    }

    public void setListaDe(List<DespesaExercicioPPA> listaDe) {
        this.listaDe = listaDe;
    }

    public String getExercicio() {
        return exercicio;
    }

    public void setExercicio(String exercicio) {
        this.exercicio = exercicio;
    }

    public void validaExercicio(FacesContext context, UIComponent component, Object value) {
        Integer ano = Integer.parseInt(value.toString());
        FacesMessage message = new FacesMessage();
        List<Exercicio> exer = exercicioFacade.lista();
        Exercicio ex = null;
        for (Exercicio e : exer) {
            if (ano.compareTo(e.getAno()) == 0) {
                ex = e;
                ((DespesaExercicioPPA) selecionado).setExercicio(ex);
            }
        }
        if (ex == null) {
            ((DespesaExercicioPPA) selecionado).setExercicio(null);
            message.setDetail("Exercício Inválido!");
            message.setSummary("Exercício Inválido!");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }
}
