package br.com.webpublico.util;

import br.com.webpublico.entidades.EntidadeWebPublico;

/**
 * @author Daniel Franco
 * @since 18/11/2015 13:39
 */
public class IdentidadeDaEntidadeSemReflexao {

    public static boolean calcularEquals(EntidadeWebPublico obj, Object outroObj) {
        if (outroObj == null) {
            return false;
        }

        if (obj.getClass() != outroObj.getClass()) {
            return false;
        }

        EntidadeWebPublico outro = (EntidadeWebPublico) outroObj;

        final Long idObj = obj.getId();
        final Long idOutro = outro.getId();

        if (idObj == null) {
            final Long criadoEmObj = obj.getCriadoEm();
            final Long criadoEmOutro = outro.getCriadoEm();

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
    public static int calcularHashCode(EntidadeWebPublico obj) {
        Long id = obj.getId();
        if (id == null) {
            Long criadoEm = obj.getCriadoEm();
            int hash = 3;
            hash = 97 * hash + (criadoEm != null ? criadoEm.hashCode() : 0);
            return hash;
        } else {
            int hash = 7;
            hash = 71 * hash + id.hashCode();
            return hash;
        }
    }

}
