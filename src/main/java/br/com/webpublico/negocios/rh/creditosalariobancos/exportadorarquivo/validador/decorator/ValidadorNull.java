package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.validador.decorator;


import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception.ValorInvalidoException;

/**
 * @author Daniel Franco
 * @since 25/05/2016 11:09
 */
public class ValidadorNull extends Validador {

    private boolean aceitaNull;

    public ValidadorNull(boolean aceitaNull, Validador validador) {
        super(validador);
        this.aceitaNull = aceitaNull;
    }

    @Override
    public void validar(Object valor) {
        super.validar(valor);
        if (valor == null && !aceitaNull) {
            throw new ValorInvalidoException("O valor não pode ser null");
        }
    }
}
