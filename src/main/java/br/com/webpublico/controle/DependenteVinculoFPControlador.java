/*
 * Codigo gerado automaticamente em Thu Aug 04 15:51:37 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Dependente;
import br.com.webpublico.entidades.DependenteVinculoFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DependenteFacade;
import br.com.webpublico.negocios.DependenteVinculoFPFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class DependenteVinculoFPControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private DependenteVinculoFPFacade dependenteVinculoFPFacade;
    @EJB
    private DependenteFacade dependenteFacade;
    private ConverterGenerico converterDependente;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    private ConverterGenerico converterVinculoFP;

    public DependenteVinculoFPControlador() {
        metadata = new EntidadeMetaData(DependenteVinculoFP.class);
    }

    public DependenteVinculoFPFacade getFacade() {
        return dependenteVinculoFPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return dependenteVinculoFPFacade;
    }

    public List<SelectItem> getDependente() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Dependente object : dependenteFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDependente() + ""));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterDependente() {
        if (converterDependente == null) {
            converterDependente = new ConverterGenerico(Dependente.class, dependenteFacade);
        }
        return converterDependente;
    }

    public List<SelectItem> getVinculoFP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (VinculoFP object : vinculoFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getMatriculaFP() + " - " + object.getUnidadeOrganizacional()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterVinculoFP() {
        if (converterVinculoFP == null) {
            converterVinculoFP = new ConverterGenerico(VinculoFP.class, vinculoFPFacade);
        }
        return converterVinculoFP;
    }

    @Override
    public String salvar() {//Valida se a data de inicio da vigencia é superior a data final da vigencia
        if (((DependenteVinculoFP) selecionado).getInicioVigencia().after(((DependenteVinculoFP) selecionado).getFinalVigencia())) {
            FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção!", "Fim da Vigência não pode ser menor que o Início da Vigência!"));
        } else {
            return super.salvar();
        }
        return "";
    }
}
