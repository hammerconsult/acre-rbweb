/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.negocios.AbstractFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * @author Munif
 */
public class ConverterPessoa implements Converter {

    protected static final Logger logger = LoggerFactory.getLogger(ConverterPessoa.class);
    private AbstractFacade facade;

    public ConverterPessoa(AbstractFacade f) {
        this.facade = f;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        logger.debug("getAsObject " + value.getClass() + " " + value + " facade " + facade);
        return facade.recuperar(Pessoa.class, new Long(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        logger.debug("getAsString " + value.getClass() + " " + value + " facade " + facade);
        if (value != null) {
            Pessoa obj = (Pessoa) value;
            return String.valueOf(obj.getId());
        } else {
            return null;
        }
    }
}
