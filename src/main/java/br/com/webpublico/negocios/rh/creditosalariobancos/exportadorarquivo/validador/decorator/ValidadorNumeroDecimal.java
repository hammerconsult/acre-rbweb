package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.validador.decorator;




import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception.ValorInvalidoException;

import java.math.BigDecimal;

/**
 * @author Daniel Franco
 * @since 25/05/2016 11:32
 */
public class ValidadorNumeroDecimal extends Validador {

    public ValidadorNumeroDecimal(Validador validador) {
        super(validador);
    }

    @Override
    public void validar(Object valor) {
        super.validar(valor);
        try {
            new BigDecimal(valor.toString());
        } catch (Exception ex) {
            throw new ValorInvalidoException("O Valor [" + valor + "] não é um Valor Decimal (java.math.BigDecimal) válido", ex);
        }
    }
}
