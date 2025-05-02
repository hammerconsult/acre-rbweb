package br.com.webpublico.seguranca.controle.converter;

import com.google.common.base.Strings;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;

@FacesConverter(forClass = LocalTime.class)
public class LocalTimeConverter implements Converter, Serializable {

    private static final DateTimeFormatter FORMATTTER = DateTimeFormat.forPattern("HH:mm");

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ////System.out.println("xxx ==> " + value);
        if (!Strings.isNullOrEmpty(value)) {
            try {
                return FORMATTTER.parseLocalTime(value);
            } catch (IllegalArgumentException ex) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data/Hora inválida", null));
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value != null) {
            LocalTime localTime = (LocalTime) value;
            return localTime.toString(FORMATTTER);
        }
        return null;
    }
}
