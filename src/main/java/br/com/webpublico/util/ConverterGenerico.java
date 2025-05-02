/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.negocios.AbstractFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;

/**
 * @author Munif
 */
public class ConverterGenerico implements Converter, Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ConverterGenerico.class);
    private AbstractFacade facade;
    private Class classe;

    public ConverterGenerico(Class classe, AbstractFacade f) {
        this.facade = f;
        this.classe = classe;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        try {
            Object chave = Persistencia.getFieldId(classe).getType().getConstructor(String.class).newInstance(value);
            return facade.recuperar(classe, chave);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Erro ao converter: " + value));
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value != null) {
            return String.valueOf(Persistencia.getId(value));
        } else {
            return null;
        }
    }
}
