package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ReceitaPPAContasExercicio;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ReceitaPPAFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Fabio
 */
@ManagedBean(name = "receitaPPAControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-receitappa", pattern = "/receitappa/novo/", viewId = "/faces/financeiro/ppa/receitappa/edita.xhtml"),
        @URLMapping(id = "editar-receitappa", pattern = "/receitappa/editar/#{receitaPPAControlador.id}/", viewId = "/faces/financeiro/ppa/receitappa/edita.xhtml"),
        @URLMapping(id = "ver-receitappa", pattern = "/receitappa/ver/#{receitaPPAControlador.id}/", viewId = "/faces/financeiro/ppa/receitappa/visualizar.xhtml"),
        @URLMapping(id = "listar-receitappa", pattern = "/receitappa/listar/", viewId = "/faces/financeiro/ppa/receitappa/lista.xhtml"),})
public class ReceitaPPAControlador extends PrettyControlador<ReceitaPPA> implements Serializable, CRUD {

    @EJB
    private ReceitaPPAFacade receitaPPAFacade;
    private ConverterAutoComplete converterPPA;
    private ConverterAutoComplete converterContaReceita;
    private ConverterAutoComplete converterExercicio;
    private ReceitaPPAContas receitaPPAConta;
    private MoneyConverter moneyConverter;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private List<ReceitaPPAContasExercicio> listaReceitaPPAContasExercicios;

    public ReceitaPPAControlador() {
        super(ReceitaPPA.class);
    }

    @URLAction(mappingId = "novo-receitappa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        receitaPPAConta = new ReceitaPPAContas();
        recuperarExercicio();
        listaReceitaPPAContasExercicios = new ArrayList<>();

    }

    private void recuperarExercicio() {
        Exercicio ultimoExercicioAdicionado = null;

        try {
            ultimoExercicioAdicionado = listaReceitaPPAContasExercicios.get(listaReceitaPPAContasExercicios.size() - 1).getExercicio();
        } catch (Exception e) {

        }


        if (ultimoExercicioAdicionado == null) {
            Exercicio exercicio = sistemaControlador.getExercicioCorrente();
            if (selecionado.getPpa() != null) {
                List<Exercicio> exercicios = receitaPPAFacade.getExercicioFacade().listaPorPpaFiltrando(selecionado.getPpa(), "");
                if (exercicios.contains(exercicio)) {
                    receitaPPAConta.setExercicio(exercicio);
                }
            }
        } else {
            receitaPPAConta.setExercicio(ultimoExercicioAdicionado);
        }
    }

    @URLAction(mappingId = "ver-receitappa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-receitappa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }

    public void recuperarEditarVer() {
        this.selecionado = this.receitaPPAFacade.recuperar(super.getId());
        receitaPPAConta = new ReceitaPPAContas();
        listaReceitaPPAContasExercicios = new ArrayList<>();
        for (ReceitaPPAContas rec : selecionado.getReceitaPPAContas()) {
            ReceitaPPAContasExercicio receitaPPAContasExercicio = getContaDoExercicio(rec.getExercicio(), listaReceitaPPAContasExercicios);
            if (receitaPPAContasExercicio == null) {
                receitaPPAContasExercicio = new ReceitaPPAContasExercicio();
                receitaPPAContasExercicio.setExercicio(rec.getExercicio());
                receitaPPAContasExercicio.setTotalValor(BigDecimal.ZERO);
                receitaPPAContasExercicio.setReceitasPPAContas(new ArrayList<ReceitaPPAContas>());
                listaReceitaPPAContasExercicios.add(receitaPPAContasExercicio);
            }
            receitaPPAContasExercicio.getReceitasPPAContas().add(rec);
            receitaPPAContasExercicio.setTotalValor(receitaPPAContasExercicio.getTotalValor().add(rec.getValor()));
            ordernarExercicios();
        }
    }

    private ReceitaPPAContasExercicio getContaDoExercicio(Exercicio exercicio, List<ReceitaPPAContasExercicio> receitaPPAContas) {
        for (ReceitaPPAContasExercicio ppaConta : receitaPPAContas) {
            if (ppaConta.getExercicio().equals(exercicio)) {
                return ppaConta;
            }
        }
        return null;
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, receitaPPAFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    @Override
    public AbstractFacade getFacede() {
        return receitaPPAFacade;
    }

    public ReceitaPPAFacade getReceitaPPAFacade() {
        return receitaPPAFacade;
    }

    public void setReceitaPPAFacade(ReceitaPPAFacade receitaPPAFacade) {
        this.receitaPPAFacade = receitaPPAFacade;
    }

    public List<PPA> completaPPA(String parte) {
        return receitaPPAFacade.getPpaFacade().listaFiltrandoPPA(parte.trim());
    }

    public List<Exercicio> completaExercicio(String parte) {
        return receitaPPAFacade.getExercicioFacade().listaPorPpaFiltrando(selecionado.getPpa(), parte);
    }

    public List<Conta> completaContaReceita(String parte) {
        ReceitaPPA rppa = (ReceitaPPA) selecionado;
        if (rppa.getPpa() != null) {
            List<Exercicio> exs = receitaPPAFacade.getPpaFacade().listaPpaEx(rppa.getPpa());
            for (Exercicio e : exs) {
                if (e.equals(sistemaControlador.getExercicioCorrente())) {
                    return receitaPPAFacade.getContaFacade().listaFiltrandoReceita(parte.trim(), e);
                }
            }
        }
        return new ArrayList<Conta>();
    }

    public Converter getConverterPPA() {
        if (converterPPA == null) {
            converterPPA = new ConverterAutoComplete(PPA.class, receitaPPAFacade.getPpaFacade());
        }
        return converterPPA;
    }

    public Converter getConverterContaReceita() {
        if (converterContaReceita == null) {
            converterContaReceita = new ConverterAutoComplete(Conta.class, receitaPPAFacade.getContaFacade());
        }
        return converterContaReceita;
    }

    public ReceitaPPAContas getReceitaPPAConta() {
        return receitaPPAConta;
    }

    public void setReceitaPPAConta(ReceitaPPAContas receitaPPAConta) {
        this.receitaPPAConta = receitaPPAConta;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public void setaPPA(SelectEvent evt) {
//        selecionado.setPpa((PPA) evt.getObject());
        recuperarExercicio();
        receitaPPAConta.setContaReceita(null);
    }

    public void setaExercicio(SelectEvent evt) {
        receitaPPAConta.setExercicio((Exercicio) evt.getObject());
        receitaPPAConta.setContaReceita(null);
    }

    public void adicionaConta() {
        if (receitaPPAConta != null) {
            if (receitaPPAConta.getExercicio() == null) {
                FacesUtil.addCampoObrigatorio("O campo Exercício deve ser informado.");
                return;
            }
            if (receitaPPAConta.getContaReceita() == null) {
                FacesUtil.addCampoObrigatorio("O campo Conta de Receita deve ser informado.");
                return;
            }
            if (receitaPPAConta.getValor().compareTo(BigDecimal.ZERO) < 0) {
                FacesUtil.addOperacaoNaoPermitida("O campo Valor deve ser maior que zero(0).");
                return;
            }
            if (receitaPPAConta.getContaReceita() != null && receitaPPAConta.getValor() != null) {
                receitaPPAConta.setReceitaPPA((ReceitaPPA) selecionado);
                if (listaReceitaPPAContasExercicios.isEmpty()) {
                    ReceitaPPAContasExercicio reccexe = new ReceitaPPAContasExercicio();
                    List<ReceitaPPAContas> list = new ArrayList<>();
                    list.add(receitaPPAConta);
                    reccexe.setExercicio(receitaPPAConta.getExercicio());
                    reccexe.setReceitasPPAContas(list);
                    reccexe.setTotalValor(receitaPPAConta.getValor());
                    listaReceitaPPAContasExercicios.add(reccexe);
                } else {
                    for (ReceitaPPAContasExercicio recce : listaReceitaPPAContasExercicios) {
                        if (receitaPPAConta.getExercicio().equals(recce.getExercicio())) {
                            for (ReceitaPPAContas recppa : recce.getReceitasPPAContas()) {
                                if (recppa.getContaReceita().equals(receitaPPAConta.getContaReceita())) {
                                    FacesUtil.addOperacaoNaoPermitida("A conta " + receitaPPAConta.getContaReceita().getCodigo() + " já foi adicionada para o exercício " + receitaPPAConta.getExercicio().getAno() + ".");
                                    return;
                                }
                            }
                            recce.getReceitasPPAContas().add(receitaPPAConta);
                            recce.setTotalValor(recce.getTotalValor().add(receitaPPAConta.getValor()));
                            selecionado.getReceitaPPAContas().add(receitaPPAConta);
                            receitaPPAConta = new ReceitaPPAContas();
                            recuperarExercicio();
                            return;
                        }
                    }
                    ReceitaPPAContasExercicio reccexe = new ReceitaPPAContasExercicio();
                    List<ReceitaPPAContas> list = new ArrayList<>();
                    list.add(receitaPPAConta);
                    reccexe.setExercicio(receitaPPAConta.getExercicio());
                    reccexe.setReceitasPPAContas(list);
                    reccexe.setTotalValor(receitaPPAConta.getValor());
                    listaReceitaPPAContasExercicios.add(reccexe);
                }
                selecionado.getReceitaPPAContas().add(receitaPPAConta);
                receitaPPAConta = new ReceitaPPAContas();
                recuperarExercicio();
                ordernarExercicios();
            }
        }
    }

    public void ordernarExercicios() {
        Collections.sort(listaReceitaPPAContasExercicios, new Comparator<ReceitaPPAContasExercicio>() {
            @Override
            public int compare(ReceitaPPAContasExercicio o1, ReceitaPPAContasExercicio o2) {
                String i1 = o1.getExercicio().getAno().toString();
                String i2 = o2.getExercicio().getAno().toString();
                return i1.compareTo(i2);
            }
        });
    }

    public BigDecimal getValorTotalReceita() {
        BigDecimal total = BigDecimal.ZERO;
        for (ReceitaPPAContasExercicio r : listaReceitaPPAContasExercicios) {
            total = total.add(r.getTotalValor());
        }
        return total;
    }

    public void removerConta(ReceitaPPAContas rppac) {
        if (rppac != null) {
            ReceitaPPAContasExercicio receitaPPAContasExercicio = null;
            for (ReceitaPPAContasExercicio recce : listaReceitaPPAContasExercicios) {
                if (recce.getExercicio().equals(rppac.getExercicio()) && recce.getReceitasPPAContas().contains(rppac)) {
                    recce.getReceitasPPAContas().remove(rppac);
                    recce.setTotalValor(recce.getTotalValor().subtract(rppac.getValor()));
                    receitaPPAContasExercicio = recce;
                    break;
                }
            }

            if (receitaPPAContasExercicio.getReceitasPPAContas().isEmpty()) {
                listaReceitaPPAContasExercicios.remove(receitaPPAContasExercicio);
            }
            selecionado.getReceitaPPAContas().remove(rppac);
        }
    }

    @Override
    public void salvar() {
        if (Util.validaCampos(selecionado)) {
            try {
                ReceitaPPA rppa = (ReceitaPPA) selecionado;
                if (rppa.getId() != null) {
                    this.receitaPPAFacade.salvar(rppa);
                } else {
                    this.receitaPPAFacade.salvarNovo(rppa);
                }
                FacesUtil.addOperacaoRealizada("Registro salvo com Sucesso.");
                redireciona();
            } catch (Exception e) {
                FacesUtil.addOperacaoNaoRealizada("Erro ao salvar: " + e.getMessage());
            }
        }
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/receitappa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<ReceitaPPAContasExercicio> getListaReceitaPPAContasExercicios() {
        return listaReceitaPPAContasExercicios;
    }

    public void setListaReceitaPPAContasExercicios(List<ReceitaPPAContasExercicio> listaReceitaPPAContasExercicios) {
        this.listaReceitaPPAContasExercicios = listaReceitaPPAContasExercicios;
    }
}
