/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.singletons.CacheTributario;
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
public class ConverterGenericoCacheTributario implements Converter, Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ConverterGenericoCacheTributario.class);

    private CacheTributario cacheTributario;
    private Class classe;

    public ConverterGenericoCacheTributario(Class classe, CacheTributario cacheTributario) {
        this.classe = classe;
        this.cacheTributario = cacheTributario;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        try {
            Object chave = Persistencia.getFieldId(classe).getType().getConstructor(String.class).newInstance(value);
            return cacheTributario.getDividasPorId(Long.valueOf(chave.toString()));
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Erro ao converter: " + value));
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value != null) {
            return ((Divida) value).getId().toString();
        } else {
            return null;
        }
    }
}
