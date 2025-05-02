/*
 * Codigo gerado automaticamente em Fri May 06 09:14:43 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ProvisaoPPADespesa;
import br.com.webpublico.entidades.ProvisaoPPAFonte;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.MoneyConverter;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class ProvisaoPPAFonteControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private ProvisaoPPAFonteFacade provisaoPPAFonteFacade;
    @EJB
    private ProvisaoPPADespesaFacade provisaoPPADespesaFacade;
    private ConverterAutoComplete converterProvisaoPPADespesa;
    @EJB
    private ContaFacade fonteDeRecursosFacade;
    private ConverterAutoComplete converterFonteDeRecursos;
    @EJB
    private ProvisaoPPAFacade provisaoPPAFacade;
    private MoneyConverter moneyConverter;
    Exercicio exerc;

    public Exercicio getExerc() {
        return exerc;
    }

    public void setExerc(Exercicio exerc) {
        this.exerc = exerc;
    }

    public ProvisaoPPAFonteControlador() {
        metadata = new EntidadeMetaData(ProvisaoPPAFonte.class);
    }

    public ProvisaoPPAFonteFacade getFacade() {
        return provisaoPPAFonteFacade;
    }

    public void setaDependenciasProvisao(ActionEvent e) {
        super.novo();
        ProvisaoPPADespesa p = (ProvisaoPPADespesa) e.getComponent().getAttributes().get("objeto");
        ((ProvisaoPPAFonte) selecionado).setProvisaoPPADespesa(p);
        ((ProvisaoPPAFonte) selecionado).setDestinacaoDeRecursos(p.getContaDeDespesa());

    }

    @Override
    public AbstractFacade getFacede() {
        return provisaoPPAFonteFacade;
    }

    @Override
    public String salvar() {
        if (((ProvisaoPPAFonte) selecionado).getSomenteLeitura() == true) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção!", "Esta Provisão PPA Fonte não pode sofrer modificações!"));
            return "edita";
        } else {
            return super.salvar();
        }
    }

    public List<SelectItem> getFonteDeRecursos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Conta object : fonteDeRecursosFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public Converter getConverterFonteDeRecursos() {
        if (converterFonteDeRecursos == null) {
            converterFonteDeRecursos = new ConverterAutoComplete(Conta.class, fonteDeRecursosFacade);
        }
        return converterFonteDeRecursos;
    }

    public List<Conta> completaFonteDeRecursos(String parte) {
        return fonteDeRecursosFacade.listaFiltrandoDestinacaoRecursos(parte.trim(), exerc);
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    @Override
    public void selecionar(ActionEvent evento) {
        super.selecionar(evento);
    }

    public Converter getConverterProvisaoPPADespesa() {
        if (converterProvisaoPPADespesa == null) {
            converterProvisaoPPADespesa = new ConverterAutoComplete(ProvisaoPPADespesa.class, provisaoPPADespesaFacade);
        }
        return converterProvisaoPPADespesa;
    }

    public List<ProvisaoPPADespesa> completaProvisaoDespesa(String parte) {
        return provisaoPPADespesaFacade.listaFiltrando(parte.trim(), "codigo");
    }

    public Exercicio testeVal(SelectEvent evt) {
        ProvisaoPPADespesa pNovo = (ProvisaoPPADespesa) evt.getObject();
        if (pNovo != null) {
            provisaoPPADespesaFacade.recuperar(pNovo);
            exerc = pNovo.getSubAcaoPPA().getExercicio();
        }
        return exerc;
    }
}
