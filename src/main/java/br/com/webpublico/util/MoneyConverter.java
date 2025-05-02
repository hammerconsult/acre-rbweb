/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.regex.Pattern;

/**
 * @author fernando
 */
@FacesConverter(value = "moneyConverter")
public class MoneyConverter implements Converter, Serializable {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String str) throws ConverterException {
        if (str == null || str.toString().trim().equals("")) {
            return new BigDecimal("0.0000");
        } else {
            str = str.replaceAll(Pattern.quote("R$ "), "");
            str = str.replaceAll(Pattern.quote("."), "");
            try {
                str = str.replaceAll(Pattern.quote(","), ".");
                BigDecimal valor = new BigDecimal(str);
                return valor;
            } catch (Exception e) {
                return new BigDecimal("0.0000");
            }
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object obj) throws ConverterException {
        if (obj == null || obj.toString().trim().equals("")) {
            return "0,0000";
        } else {
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(4);
            return nf.format(new BigDecimal(obj.toString()));
        }
    }
}
