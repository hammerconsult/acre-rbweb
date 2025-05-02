/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * @author venon
 */
@FacesConverter(value = "dotConverter")
public class DotConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String str) {
        if (str == null || str.toString().trim().equals("")) {
            return new BigDecimal("0.0000");
        } else {
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
    public String getAsString(FacesContext fc, UIComponent uic, Object obj) {
        if (obj == null || obj.toString().trim().equals("")) {
            return "0,0000";
        } else {
            return obj.toString().replaceAll(Pattern.quote("."), ",");
        }
    }
}
