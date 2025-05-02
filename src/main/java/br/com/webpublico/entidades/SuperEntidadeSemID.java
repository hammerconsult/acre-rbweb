package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 29/08/13
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class SuperEntidadeSemID implements Serializable {

    private static final String PADRAO_INDEFINIDO = "o/a";

    @Transient
    public Long criadoEm = System.nanoTime();

    public SuperEntidadeSemID() {
        this.criadoEm = System.nanoTime();
    }

    public void realizarValidacoes() {
        validarCamposObrigatorios();
    }

    public void validarCamposObrigatorios() {
        Util.validarCampos(this);
    }

    public String getArtigoDefinidoDaEntidade() {
        Etiqueta e = this.getClass().getAnnotation(Etiqueta.class);
        if (e == null) {
            return PADRAO_INDEFINIDO;
        }

        if (e.genero().equalsIgnoreCase("M")) {
            return "o";
        }

        if (e.genero().equalsIgnoreCase("F")) {
            return "a";
        }

        return PADRAO_INDEFINIDO;
    }

    public String getPromonomeDemonstrativoDaEntidade() {
        Etiqueta e = this.getClass().getAnnotation(Etiqueta.class);
        final String PADRAO_INDEFINIDO = "este/esta";

        if (e == null) {
            return PADRAO_INDEFINIDO;
        }

        if (e.genero().equalsIgnoreCase("M")) {
            return "este";
        }

        if (e.genero().equalsIgnoreCase("F")) {
            return "esta";
        }

        return PADRAO_INDEFINIDO;
    }

    public String getNomeDaEntidade() {
        Etiqueta e = this.getClass().getAnnotation(Etiqueta.class);
        if (e == null) {
            return this.getClass().getSimpleName();
        }

        return e.value();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }


}
