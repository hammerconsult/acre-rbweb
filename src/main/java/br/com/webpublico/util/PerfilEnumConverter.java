package br.com.webpublico.util;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;

@FacesConverter(value = "perfilEnumConverter")
public class PerfilEnumConverter implements Converter,Serializable {

    private static final String ATTRIBUTE_ENUM_TYPE = "GenericEnumConverter.enumType";

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof Enum) {
            component.getAttributes().put(ATTRIBUTE_ENUM_TYPE, value.getClass());
            return ((Enum<?>) value).name();
        } else {
            throw new ConverterException(new FacesMessage("Value is not an enum: " + value.getClass()));
        }
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Class<Enum> enumType = (Class<Enum>) component.getAttributes().get(ATTRIBUTE_ENUM_TYPE);
        try {
            return Enum.valueOf(enumType, value);
        } catch (IllegalArgumentException e) {
            throw new ConverterException(new FacesMessage("Value is not an enum of type: " + enumType));
        }
    }
}
