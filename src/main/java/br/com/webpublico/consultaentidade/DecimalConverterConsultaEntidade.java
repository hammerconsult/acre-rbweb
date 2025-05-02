package br.com.webpublico.consultaentidade;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

@FacesConverter(value = "decimalConverterConsultaEntidade")
public class DecimalConverterConsultaEntidade implements Serializable, Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if (s == null || "".equals(s)) {
            return null;
        } else {
            try {
                String valor = s.replace(".", "").replace(",", ".");
                return new BigDecimal(valor);
            } catch (Exception e) {
                return new BigDecimal("0.00");
            }
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        if (o instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal) o;
            return new DecimalFormat("#,##0.0000").format(bd);
        }
        return null;
    }
}
