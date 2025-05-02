/*
 * Codigo gerado automaticamente em Mon Jul 09 15:47:01 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.entidades.AlteracaoReceitaLoa;
import br.com.webpublico.entidades.ReceitaLOA;
import br.com.webpublico.enums.TipoAlteracaoReceitaLoa;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AlteracaoReceitaLoaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.enums.Operacoes;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class AlteracaoReceitaLoaControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private AlteracaoReceitaLoaFacade alteracaoReceitaLoaFacade;
    private MoneyConverter moneyConverter;
    private ConverterAutoComplete converterReceitaLoa;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private TipoAlteracaoReceitaLoa tipoAlteracaoReceitaLoa;

    public AlteracaoReceitaLoaControlador() {
        metadata = new EntidadeMetaData(AlteracaoReceitaLoa.class);
    }

    public AlteracaoReceitaLoaFacade getFacade() {
        return alteracaoReceitaLoaFacade;
    }

    @Override
    public void novo() {
        super.novo();
        tipoAlteracaoReceitaLoa = null;
    }

    @Override
    public AbstractFacade getFacede() {
        return alteracaoReceitaLoaFacade;
    }

    public ConverterAutoComplete getConverterReceitaLoa() {
        if (converterReceitaLoa == null) {
            converterReceitaLoa = new ConverterAutoComplete(ReceitaLOA.class, alteracaoReceitaLoaFacade.getReceitaLOAFacade());
        }
        return converterReceitaLoa;
    }

    @Override
    public String salvar() {
        AlteracaoReceitaLoa cd = ((AlteracaoReceitaLoa) selecionado);
        if (validaCampos()) {
            if (operacao.equals(Operacoes.NOVO)) {
                try {
                    cd.setTipoAlteracaoReceitaLoa(tipoAlteracaoReceitaLoa);
                    alteracaoReceitaLoaFacade.salvarNovo(cd);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com Sucesso!", "Salvo com Sucesso!"));
                    return "lista.xhtml";
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
                    return "edita.xhtml";
                }
            } else {
                try {
                    cd.setTipoAlteracaoReceitaLoa(tipoAlteracaoReceitaLoa);
                    alteracaoReceitaLoaFacade.salvar(cd);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com Sucesso!", "Salvo com Sucesso!"));
                    return "lista.xhtml";
                } catch (Exception ex) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
                    return "edita.xhtml";
                }
            }
        }
        return "edita.xhtml";
    }

    public List<SelectItem> getListaTipoAlteracao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAlteracaoReceitaLoa tAlteracao : TipoAlteracaoReceitaLoa.values()) {
            toReturn.add(new SelectItem(tAlteracao, tAlteracao.getDescricao()));
        }
        return toReturn;
    }

//    public List<ReceitaLOA> completaReceitaLoa(String parte) {
//        if (tipoAlteracaoReceitaLoa.equals(TipoAlteracaoReceitaLoa.PREVISAOADICIONALRECEITA) || tipoAlteracaoReceitaLoa.equals(TipoAlteracaoReceitaLoa.ANULACAOPREVISAORECEITA)) {
//            return alteracaoReceitaLoaFacade.getReceitaLOAFacade().filtraReceitaLoaPorDescricaoTipoPrevisaoCodExercDaConta(
//                    parte.trim(), sistemaControlador.getExercicioCorrente(), TipoPrevisaoReceitaLoa.PREVISAOINICIALRECEITA);
//        } else if (tipoAlteracaoReceitaLoa.equals(TipoAlteracaoReceitaLoa.DEDUCAOPREVISAOADICIONALRECEITA)) {
//            return alteracaoReceitaLoaFacade.getReceitaLOAFacade().filtraReceitaLoaPorDescricaoTipoPrevisaoCodExercDaConta(
//                    parte.trim(), sistemaControlador.getExercicioCorrente(), TipoPrevisaoReceitaLoa.DEDUCAO);
//        } else if (tipoAlteracaoReceitaLoa.equals(TipoAlteracaoReceitaLoa.DEDUCAOPREVIADICIONALRECEITAFUNDEB)) {
//            return alteracaoReceitaLoaFacade.getReceitaLOAFacade().filtraReceitaLoaPorDescricaoTipoPrevisaoCodExercDaConta(
//                    parte.trim(), sistemaControlador.getExercicioCorrente(), TipoPrevisaoReceitaLoa.DEDUCAOFUNDEB);
//        } else {
//            return null;
//        }
//    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public TipoAlteracaoReceitaLoa getTipoAlteracaoReceitaLoa() {
        return tipoAlteracaoReceitaLoa;
    }

    public void setTipoAlteracaoReceitaLoa(TipoAlteracaoReceitaLoa tipoAlteracaoReceitaLoa) {
        this.tipoAlteracaoReceitaLoa = tipoAlteracaoReceitaLoa;
    }
}
