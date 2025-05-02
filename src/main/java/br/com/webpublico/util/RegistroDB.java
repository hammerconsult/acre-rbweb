/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel
 */
public class RegistroDB implements Serializable {
    private List<CampoDB> campos = new ArrayList<>();

    public List<CampoDB> getCampos() {
        return new ArrayList<>(campos);
    }

    public CampoDB getCampo(CampoDB campo) {
        return campos.get(campos.indexOf(campo));
    }

    public void addField(CampoDB campo) {
        if (!this.campos.contains(campo)) {
            this.campos.add(campo);
        } else {
            throw new IllegalArgumentException("Campo " + campo + " já existente no registro");
        }
    }

    public int getFieldCount() {
        return campos.size();
    }

    public CampoDB getCampoByIndex(int index) {
        return campos.get(index);
    }

    public CampoDB getField(String fieldName) {
        for (CampoDB campo : campos) {
            if (campo.getNome().equalsIgnoreCase(fieldName)) {
                return campo;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (CampoDB campo : campos) {
            sb.append(campo).append(" | ");
        }
        return sb.toString();
    }

    public List<CampoDB> getFields() {
        return new ArrayList<>(campos);
    }

}
