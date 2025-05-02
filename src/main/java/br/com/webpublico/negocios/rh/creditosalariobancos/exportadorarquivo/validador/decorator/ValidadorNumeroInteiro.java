package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.validador.decorator;


import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception.ValorInvalidoException;

/**
 * @author Daniel Franco
 * @since 25/05/2016 11:28
 */
public class ValidadorNumeroInteiro extends Validador {

    public ValidadorNumeroInteiro(Validador validador) {
        super(validador);
    }

    @Override
    public void validar(Object valor) {
        super.validar(valor);
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(valor.toString());
        } catch (Exception ex) {
            throw new ValorInvalidoException("O Valor [" + valor + "] não é um número inteiro válido", ex);
        }
    }
}
