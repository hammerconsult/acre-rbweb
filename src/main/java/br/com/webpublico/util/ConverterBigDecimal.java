/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author Rafael
 */
@FacesConverter(value = "converterBigDecimal")
public class ConverterBigDecimal implements Converter, Serializable {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        try {
            if (string != null && !string.isEmpty()) {
                String valor = string.replace(".", "").replace(",", "."); //replace troca a string que voce quer que no caso é . por vazio, mas pode ser usado pra qualquer coisa
                return new BigDecimal(valor);
            }
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "'" + string + "'" + " não é um padrão numérico.", "'" + string + "'" + " não é um padrão numérico. Exemplo #,##0#"));
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o instanceof BigDecimal) { // instanceof verifica se esse objeto é do tipo especificado
            BigDecimal bd = (BigDecimal) o;
            return new DecimalFormat("#,##0.00").format(bd);
        }
        return null;
    }
}
