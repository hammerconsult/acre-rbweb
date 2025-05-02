package br.com.webpublico.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 13/09/13
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
@FacesConverter(value = "moedaConverter")
public class MoneyConverterMoedaSemRs implements Converter, Serializable {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String str) throws ConverterException {
        if ( str == null || str.toString().trim().equals("") ) {
            return new BigDecimal("0.00");
        } else {
            str = str.replaceAll(Pattern.quote("R$ "), "");
            str = str.replaceAll(Pattern.quote("."), "");
            try {
                str = str.replaceAll(Pattern.quote(","), ".");
                BigDecimal valor = new BigDecimal(str);
                return valor;
            } catch (Exception e) {
                return new BigDecimal("0.00");
            }
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object obj) {
        if ( obj == null || obj.toString().trim().equals("") ) {
            return "0,00";
        } else {
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(4);
            return nf.format(new BigDecimal(obj.toString()));
        }
    }
}
