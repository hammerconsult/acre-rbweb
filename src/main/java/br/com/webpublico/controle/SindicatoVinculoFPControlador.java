/*
 * Codigo gerado automaticamente em Thu Aug 04 09:29:19 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Sindicato;
import br.com.webpublico.entidades.SindicatoVinculoFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SindicatoFacade;
import br.com.webpublico.negocios.SindicatoVinculoFPFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class SindicatoVinculoFPControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private SindicatoVinculoFPFacade sindicatoVinculoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    private ConverterAutoComplete converterVinculoFP;
    @EJB
    private SindicatoFacade sindicatoFacade;
    private ConverterGenerico converterSindicato;

    public SindicatoVinculoFPControlador() {
        metadata = new EntidadeMetaData(SindicatoVinculoFP.class);
    }

    public SindicatoVinculoFPFacade getFacade() {
        return sindicatoVinculoFPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return sindicatoVinculoFPFacade;
    }

    public List<SelectItem> getVinculoFP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (VinculoFP object : vinculoFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getMatriculaFP().toString()));
        }
        return toReturn;
    }

    public Converter getConverterVinculoFP() {
        if (converterVinculoFP == null) {
            converterVinculoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterVinculoFP;
    }

    public List<SelectItem> getSindicato() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Sindicato object : sindicatoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterSindicato() {
        if (converterSindicato == null) {
            converterSindicato = new ConverterGenerico(Sindicato.class, sindicatoFacade);
        }
        return converterSindicato;
    }

    public List<VinculoFP> completaVinculos(String parte) {
        return vinculoFPFacade.listaTodosVinculos(parte.trim());
    }

    @Override
    public String salvar() {
        SindicatoVinculoFP svfp = ((SindicatoVinculoFP) selecionado);
        if (svfp.getFinalVigencia() != null) {
            if (svfp.getInicioVigencia().after(svfp.getFinalVigencia())) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "O Inicio da vigência não pode ser maior que o final da Vigência", "O Inicio da vigência não pode ser maior que o final da Vigência");
                FacesContext.getCurrentInstance().addMessage("msg", msg);
                return "";
            }
        }
        return super.salvar();
    }


}
