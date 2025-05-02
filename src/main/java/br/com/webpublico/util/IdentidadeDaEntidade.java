/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

/**
 * @author lucas
 */
public class IdentidadeDaEntidade {

    /*
     * Criado como método estático para evitar impor uma superclasse a todas as entidades...
     */
    public static boolean calcularEquals(Object obj, Object outro) {
        if (outro == null) {
            return false;
        }
        Object idObj = Persistencia.getId(obj);
        Object idOutro = Persistencia.getId(outro);

        if (obj.getClass() != outro.getClass()) {
            return false;
        }
        if (idObj == null) {
            Long criadoEmObj = (Long) Persistencia.getAttributeValue(obj, "criadoEm");
            Long criadoEmOutro = (Long) Persistencia.getAttributeValue(outro, "criadoEm");

            if (!criadoEmObj.equals(criadoEmOutro)) {
                return false;
            }
        } else {
            if (!idObj.equals(idOutro)) {
                return false;
            }
        }
        return true;
    }

    /*
     * Criado como método estático para evitar impor uma superclasse a todas as entidades...
     */
    public static int calcularHashCode(Object obj) {
        Object id = Persistencia.getId(obj);
        if (id == null) {
            Long criadoEm = (Long) Persistencia.getAttributeValue(obj, "criadoEm");
            int hash = 3;
            hash = 97 * hash + (criadoEm != null ? criadoEm.hashCode() : 0);
            return hash;
        } else {
            int hash = 7;
            hash = 71 * hash + (id != null ? id.hashCode() : 0);
            return hash;
        }
    }

}
