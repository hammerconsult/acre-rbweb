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
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author edi
 */
@FacesConverter(value = "converterBigDecimal2CasasSemArredondamento")
public class ConverterBigDecimal2CasasSemArredondamento implements Converter {

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
        if (o instanceof BigDecimal) { // instanceof verifica se esse objeto é do tipo especificado
            BigDecimal bd = (BigDecimal) ((BigDecimal) o).setScale(2, RoundingMode.FLOOR);
            return new DecimalFormat("#,##0.00").format(bd);
        }
        return null;
    }

}
