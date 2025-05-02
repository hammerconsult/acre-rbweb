package br.com.webpublico.util;

import br.com.webpublico.enums.TipoPermissaoTaxi;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "enumConverter")
public class EnumConverter extends javax.faces.convert.EnumConverter {

    public EnumConverter() {
        super(TipoPermissaoTaxi.class);
    }
}
