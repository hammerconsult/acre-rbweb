package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.validador.decorator;


import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception.TamanhoMenorQueOPermitidoException;

/**
 * @author Daniel Franco
 * @since 25/05/2016 11:19
 */
public class ValidadorTamanhoMinimo extends Validador {

    private int tamanhoMinimo;

    public ValidadorTamanhoMinimo(int tamanhoMinimo, Validador validador) {
        super(validador);
        if (tamanhoMinimo <= 0) {
            throw new IllegalArgumentException("O Tamanho Mínimo deve ser Maior que Zero");
        }
        this.tamanhoMinimo = tamanhoMinimo;
    }

    @Override
    public void validar(Object valor) {
        super.validar(valor);
        if (valor != null && valor.toString().trim().length() < tamanhoMinimo) {
            throw new TamanhoMenorQueOPermitidoException("O Valor [" + valor + "] é inválido pois tem [" + (tamanhoMinimo - valor.toString().trim().length()) + "] caracteres a menos que a quantidade mínima de [" + tamanhoMinimo + "] caracteres");
        }
    }
}
