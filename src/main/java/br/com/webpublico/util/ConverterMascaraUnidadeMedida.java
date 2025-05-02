package br.com.webpublico.util;

import br.com.webpublico.enums.TipoMascaraUnidadeMedida;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class ConverterMascaraUnidadeMedida implements Converter, Serializable {

    private String mascara;

    public ConverterMascaraUnidadeMedida(String mascara) {
        this.mascara = mascara;
    }

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string == null || "".equals(string)) {
            return null;
        }
        String valor = string.replace(".", "").replace(",", ".");
        return new BigDecimal(valor);
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal) o;
            if (mascara != null) {
                return new DecimalFormat(mascara).format(bd);
            }
            return new DecimalFormat(TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL.getMascara()).format(bd);
        }
        return null;
    }
}
