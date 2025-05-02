/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.negocios.SistemaFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;

@FacesConverter(value = "converterEntidade")
public class ConverterEntidade implements Converter, Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ConverterEntidade.class);
    private SistemaFacade facade;

    public ConverterEntidade() {
        facade = ((SistemaControlador) Util.getControladorPeloNome("sistemaControlador")).getSistemaFacade();
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value.trim().isEmpty()) {
            return null;
        }
        try {
            int chave = Integer.parseInt(value);
            return facade.findById(chave);
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Erro ao converter: " + value));
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        } else {
            if (value instanceof Long) {
                return String.valueOf(value);
            } else {
                try {
                    return Persistencia.getId(value).toString();
                } catch (Exception e) {
                    return String.valueOf(value);
                }
            }
        }
    }
}
