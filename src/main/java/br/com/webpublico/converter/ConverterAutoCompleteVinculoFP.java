/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.converter;

import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.negocios.rh.pccr.VinculoFPLeitorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;

/**
 * @author Felipe Marinzeck
 */
@FacesConverter(value = "converterVinculoFP")
public class ConverterAutoCompleteVinculoFP implements Converter, Serializable {

    @Autowired
    private VinculoFPLeitorFacade vinculoFPLeitorFacade;


    public ConverterAutoCompleteVinculoFP() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        VinculoFP v = new VinculoFP();
        v.setId(Long.parseLong(value));
        return v;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return value+"";
    }

    public VinculoFPLeitorFacade getVinculoFPLeitorFacade() {
        return vinculoFPLeitorFacade;
    }

    public void setVinculoFPLeitorFacade(VinculoFPLeitorFacade vinculoFPLeitorFacade) {
        this.vinculoFPLeitorFacade = vinculoFPLeitorFacade;
    }
}
