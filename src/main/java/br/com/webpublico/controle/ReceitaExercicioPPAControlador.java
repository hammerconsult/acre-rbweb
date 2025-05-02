/*
 * Codigo gerado automaticamente em Fri Apr 29 14:53:23 BRT 2011
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ManagedBean
@SessionScoped
public class ReceitaExercicioPPAControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private ReceitaExercicioPPAFacade receitaExercicioPPAFacade;
    @EJB
    private ContaFacade contaDeReceitaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    private ConverterGenerico converterExercicio;
    @EJB
    private PPAFacade ppaFacade;
    private ConverterAutoComplete converterPpa;
    private Exercicio exercicioSelecionado;
    private PPA ppaSelecionado;
    private List<ReceitaExercicioPPA> listaRe;
    private List<ReceitaExercicioPPA> listaR;
    private List<Conta> listaC;
    private MoneyConverter moneyConverter;
    @EJB
    private ContaFacade contaFacade;

    @Override
    public String salvar() {
        if (validaPpaSelecionado()) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            ServletContext servletContext = (ServletContext) externalContext.getContext();
            String pathReal = servletContext.getContextPath();
            List<ReceitaExercicioPPA> receitasTemp = new ArrayList<ReceitaExercicioPPA>();
            try {
                for (ReceitaExercicioPPA receita : listaRe) {
                    if (receita.getValorEstimado() != null) {
                        if (receita != null) {
                            receitasTemp.add(receita);
                            // receitaExercicioPPAFacade.salvarReceita(receita);
                        } else {
                            selecionado = receita;
                            receitasTemp.add(receita);
                        }
                    }
                }
                listaRe = new ArrayList<ReceitaExercicioPPA>();
                ppaSelecionado.setReceitasExerciciosPPAs(receitasTemp);
                ppaFacade.salvar(ppaSelecionado);
                FacesContext.getCurrentInstance().getExternalContext().redirect(pathReal + "/faces/sucesso.xhtml");
                return null;
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_FATAL, "", "Problema ao Salvar"));
                return null;
            }

        }
        return null;
    }

    @Override
    public void novo() {
        super.novo();
        exercicioSelecionado = new Exercicio();
        ppaSelecionado = new PPA();
        listaRe = new ArrayList<ReceitaExercicioPPA>();
        listaC = new ArrayList<Conta>();
        listaR = new ArrayList<ReceitaExercicioPPA>();
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

    public List<ReceitaExercicioPPA> getListaRe() {
        return listaRe;
    }

    public void setListaRe(List<ReceitaExercicioPPA> listaRe) {
        this.listaRe = listaRe;
    }

    public void teste(ActionEvent evento) {
        ReceitaExercicioPPA rec = (ReceitaExercicioPPA) evento.getComponent().getAttributes();
        rec = receitaExercicioPPAFacade.recuperar(rec);
        listaRe.add(rec);
    }

    @Override
    public void selecionar(ActionEvent evento) {
        operacao = Operacoes.EDITAR;
        ReceitaExercicioPPA r = (ReceitaExercicioPPA) evento.getComponent().getAttributes().get("objeto");
        selecionado = receitaExercicioPPAFacade.recuperar(r.getId());
        exercicioSelecionado = r.getExercicio();
    }

    public ReceitaExercicioPPAControlador() {
        metadata = new EntidadeMetaData(ReceitaExercicioPPA.class);
    }

    public ReceitaExercicioPPAFacade getFacade() {
        return receitaExercicioPPAFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return receitaExercicioPPAFacade;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Exercicio object : exercicioFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
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
            listaRe = getMontaTabela();
        } else {
            listaRe = new ArrayList<ReceitaExercicioPPA>();
        }
    }

    public List<ReceitaExercicioPPA> getMontaTabela() {
        listaExercicioDespesa();
        return listaRe;
    }

    public void listaExercicioDespesa() {
        listaRe = new ArrayList<ReceitaExercicioPPA>();
        listaC = receitaExercicioPPAFacade.listaContaReceita(exercicioSelecionado);
        listaR = receitaExercicioPPAFacade.listaIdPPA(exercicioSelecionado.getId(), ppaSelecionado.getId());
        for (ReceitaExercicioPPA despesa : listaR) {
            if (listaC.contains(despesa.getContaDeReceita())) {
                listaC.remove(despesa.getContaDeReceita());
                listaRe.add(despesa);
            }
        }
        for (Conta c : listaC) {
            ReceitaExercicioPPA de = new ReceitaExercicioPPA();
            Conta contaRec = contaFacade.recuperar(c);
            if (contaRec.getCategoria().equals(CategoriaConta.ANALITICA)) {
                de.setValorEstimado(new BigDecimal(BigInteger.ZERO));
            } else {
                de.setValorEstimado(null);
            }
            de.setContaDeReceita(contaRec);
            de.setExercicio(exercicioSelecionado);
            de.setPpa(ppaSelecionado);
            listaRe.add(de);
        }
        Collections.sort(listaRe);
    }

    public List<Conta> getContas() {
        return contaDeReceitaFacade.lista();
    }

    public Converter getConverterPpa() {
        if (converterPpa == null) {
            converterPpa = new ConverterAutoComplete(PPA.class, ppaFacade);
        }
        return converterPpa;
    }

    public List<PPA> completaPPA(String parte) {
        return ppaFacade.listaTodosPpaExercicio(exercicioSelecionado, parte);
    }

    public List<Conta> getListaC() {
        return listaC;
    }

    public void setListaC(List<Conta> listaC) {
        this.listaC = listaC;
    }

    public List<ReceitaExercicioPPA> getListaR() {
        return listaR;
    }

    public void setListaR(List<ReceitaExercicioPPA> listaR) {
        this.listaR = listaR;
    }

    public Exercicio getExercicioSelecionado() {
        return exercicioSelecionado;
    }

    public void setExercicioSelecionado(Exercicio exercicioSelecionado) {
        this.exercicioSelecionado = exercicioSelecionado;
    }

    public PPA getPpaSelecionado() {
        return ppaSelecionado;

    }

    public void setPpaSelecionado(PPA ppaSelecionado) {
        this.ppaSelecionado = ppaSelecionado;
    }

    public List<PPA> getListaPpas() {
        return ppaFacade.lista();
    }
}
