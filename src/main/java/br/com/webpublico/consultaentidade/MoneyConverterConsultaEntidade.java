package br.com.webpublico.consultaentidade;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.regex.Pattern;

@FacesConverter(value = "moneyConverterConsultaEntidade")
public class MoneyConverterConsultaEntidade implements Serializable, Converter {
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if (s == null || s.trim().equals("")) {
            return null;
        } else {
            s = s.replaceAll(Pattern.quote("R$ "), "");
            s = s.replaceAll(Pattern.quote("."), "");
            try {
                s = s.replaceAll(Pattern.quote(","), ".");
                return new BigDecimal(s);
            } catch (Exception e) {
                return new BigDecimal("0.0000");
            }
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        if (o == null || o.toString().trim().equals("")) {
            return null;
        } else {
            NumberFormat nf = NumberFormat.getCurrencyInstance();
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(4);
            return nf.format(new BigDecimal(o.toString()));
        }
    }
}
