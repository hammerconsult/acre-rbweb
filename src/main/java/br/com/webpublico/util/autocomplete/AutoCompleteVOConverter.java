/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.autocomplete;

import org.apache.commons.codec.binary.Base64;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.io.*;

/**
 * @author Arthur
 */
@FacesConverter(forClass = br.com.webpublico.util.autocomplete.AutoCompleteVO.class)
public class AutoCompleteVOConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value.trim().equals("")) {
            return null;
        }
        try {
            byte[] data = Base64.decodeBase64(value);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            Object o = ois.readObject();
            ois.close();
            return o;
        } catch (EOFException e) {
            //ignorando...
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value == null || value.equals("")) {
            return "";
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            oos.close();

            return Base64.encodeBase64String(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
