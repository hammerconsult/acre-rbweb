package br.com.webpublico.util;

import br.com.webpublico.negocios.BemFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;

public class ConverterBem implements Converter, Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ConverterBem.class);
    private BemFacade facade;
    private Class classe;

    public ConverterBem(Class classe, BemFacade f) {
        this.facade = f;
        this.classe = classe;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            Object chave = Persistencia.getFieldId(classe).getType().getConstructor(String.class).newInstance(value);
            return facade.recuperarSemDependencias(chave);
        } catch (Exception ex) {
            logger.trace("Erro: ", ex);
        }
        return null;
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
                    return Persistencia.getAttributeValue(value, Persistencia.getFieldId(classe).getName()).toString();
                } catch (Exception e) {
                    return String.valueOf(value);
                }
            }
        }
    }
}
