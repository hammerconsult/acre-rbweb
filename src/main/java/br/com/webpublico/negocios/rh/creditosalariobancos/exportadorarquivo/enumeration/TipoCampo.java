package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Daniel Franco
 * @since 16/05/2016 15:05
 */
public enum TipoCampo {
    CARACTERE(String.class),
    DATA(Date.class),
    ANO(Integer.class),
    INTEIRO(Integer.class),
    DECIMAL(Integer.class),
    ;

    Class[] classesAceitas;

    TipoCampo(Class... classesAceitas) {
        this.classesAceitas = classesAceitas;
    }

    public List<Class> getClassesAceitas() {
        return Arrays.asList(classesAceitas);
    }

    public boolean aceitaClasse(Class classe) {
        for (Class classeAceita : this.classesAceitas) {
            if (classeAceita.isAssignableFrom(classe)) {
                return true;
            }
        }
        return false;
    }

    public TipoCampo tipoCampoParaClasse(Class classe) {
        for (TipoCampo tipoCampo : TipoCampo.values()) {
            if (tipoCampo.aceitaClasse(classe)) {
                return tipoCampo;
            }
        }
        return null;
    }
}
