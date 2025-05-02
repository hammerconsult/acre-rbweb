package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.validador;

import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.annotation.Campo;
import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.validador.decorator.*;


/**
 * @author Daniel Franco
 * @since 25/05/2016 11:39
 */
public class ValidadorFactory {

    private ValidadorFactory() {
    }

    public static Validador paraOCampo(Campo campo) {
        if (campo == null) {
            throw new IllegalArgumentException("Campo não pode ser Null");
        }
        switch (campo.tipo()) {
            case ANO: return new ValidadorTamanhoMaximo(4, new ValidadorTamanhoMinimo(4, new ValidadorNumeroInteiro(null)));
            case CARACTERE: return new ValidadorTamanhoMinimo(campo.qtdCaracteresObrigatorios(), new ValidadorTamanhoMaximo(campo.tamanho(), null));
            case INTEIRO: return new ValidadorNumeroInteiro(new ValidadorTamanhoMinimo(campo.qtdCaracteresObrigatorios(), new ValidadorTamanhoMaximo(campo.tamanho(), null)));
            default: return new ValidadorNull(false, null);
        }
    }
}
