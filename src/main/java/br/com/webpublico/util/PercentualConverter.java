/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.regex.Pattern;

/**
 * @author venon
 */
@FacesConverter("percentualConverter")
public class PercentualConverter implements Converter, Serializable {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String str) {
        if (str == null || str.toString().trim().equals("")) {
            return new BigDecimal("0.0000");
        } else {
            str = str.replaceAll(Pattern.quote("%"), "");
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
    public String getAsString(FacesContext context, UIComponent component, Object obj) {
        if (obj == null || obj.toString().trim().equals("")) {
            return "0,0000";
        } else {
            NumberFormat nf = NumberFormat.getPercentInstance();
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(4);
            BigDecimal num = (new BigDecimal(obj.toString()).divide(new BigDecimal(100)));
            return nf.format(num);
        }
    }
}
