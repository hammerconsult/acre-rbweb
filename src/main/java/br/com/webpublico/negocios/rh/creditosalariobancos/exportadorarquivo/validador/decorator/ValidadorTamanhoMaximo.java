package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.validador.decorator;


import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.exception.TamanhoMaiorQueOPermitidoException;

/**
 * @author Daniel Franco
 * @since 25/05/2016 11:15
 */
public class ValidadorTamanhoMaximo extends Validador {

    private int tamanhoMaximo;

    public ValidadorTamanhoMaximo(int tamanhoMaximo, Validador validador) {
        super(validador);
        if (tamanhoMaximo <= 0) {
            throw new IllegalArgumentException("O Tamanho Máximo deve ser Maior que zero");
        }
        this.tamanhoMaximo = tamanhoMaximo;
    }

    @Override
    public void validar(Object valor) {
        super.validar(valor);
        if (valor != null && valor.toString().trim().length() > tamanhoMaximo) {
            throw new TamanhoMaiorQueOPermitidoException("O Valor [" + valor + "] é inválido pois tem [" + (valor.toString().trim().length() - tamanhoMaximo) + "] caracteres além do máximo permitido de [" + tamanhoMaximo + "] Caracteres");
        }
    }
}
