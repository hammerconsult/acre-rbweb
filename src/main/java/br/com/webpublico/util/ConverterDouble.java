/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.text.DecimalFormat;

/**
 * @author daniel
 */
@FacesConverter(value = "converterDouble")
public class ConverterDouble implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || "".equals(value)) {
            return new Double(0);
        }
        String valor = value.replace(".", "").replace(",", "."); //replace troca a string que voce quer que no caso é . por vazio, mas pode ser usado pra qualquer coisa
        return Double.parseDouble(valor);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof Double) { // instanceof verifica se esse objeto é do tipo especificado
            Double d = (Double) value;
            return new DecimalFormat("#,##0.00").format(d);
        }
        return null;
    }
}
