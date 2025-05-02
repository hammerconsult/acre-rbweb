package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.validador.decorator;



import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception.ValorInvalidoException;

import java.math.BigDecimal;

/**
 * @author Daniel Franco
 * @since 25/05/2016 11:34
 */
public class ValidadorCasasDecimais extends ValidadorNumeroDecimal {

    private int qtdCasasDecimais;

    public ValidadorCasasDecimais(int qtdCasasDecimais, Validador validador) {
        super(validador);
        if (qtdCasasDecimais <= 0) {
            throw new IllegalArgumentException("A Quantidade de Casas Decimais deve ser Maior que Zero");
        }
        this.qtdCasasDecimais = qtdCasasDecimais;
    }

    @Override
    public void validar(Object valor) {
        super.validar(valor);
        BigDecimal valorDecimal = new BigDecimal(valor.toString());
        if (valorDecimal.scale() != qtdCasasDecimais) {
            throw new ValorInvalidoException("O Valor [" + valorDecimal + "] é Inválido pois possui [" + valorDecimal.scale() + "] casas decimais, quando deveria ter exatamente [" + qtdCasasDecimais + "]");
        }
    }
}
