package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.validador.decorator;

import java.io.Serializable;

/**
 * @author Daniel Franco
 * @since 25/05/2016 11:09
 */
public abstract class Validador implements Serializable {

    protected Validador validadorAninhado;

    public Validador(Validador validadorAninhado) {
        this.validadorAninhado = validadorAninhado;
    }

    public void validar(Object valor) {
        if (validadorAninhado != null) {
            validadorAninhado.validar(valor);
        }
    }
}
