/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.enums.TipoPermissaoMotoTaxi;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author William
 */
@FacesConverter(value = "enumConverterMotoTaxi")
public class EnumConverterMotoTaxi extends javax.faces.convert.EnumConverter {

    public EnumConverterMotoTaxi() {
        super(TipoPermissaoMotoTaxi.class);
    }
}
