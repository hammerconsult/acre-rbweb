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
import java.text.DecimalFormat;

/**
 * @author edi
 */
@FacesConverter(value = "converterBigDecimalCasasDecimais")
public class ConverterBigDecimalCasasDecimais implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string == null || "".equals(string)) {
            return BigDecimal.ZERO;
        }
        String valor = string.replace(".", "").replace(",", "."); //replace troca a string que voce quer que no caso é . por vazio, mas pode ser usado pra qualquer coisa
        return new BigDecimal(valor);
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o instanceof BigDecimal) {
            BigDecimal valor = (BigDecimal) o;
            String valorStr = valor.toString();
            String valorDecimal = valorStr.substring(valorStr.indexOf(".") + 1);
            String casasDecimais = "";
            for (int x = 0; x < valorDecimal.length(); x++) {
                casasDecimais += "0";
            }
            DecimalFormat mascara = new DecimalFormat( "#,##0." + casasDecimais);
            return mascara.format(valor);
        }
        return "0,00";
    }
}
