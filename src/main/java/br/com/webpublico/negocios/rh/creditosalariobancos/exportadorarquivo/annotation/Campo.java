package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.annotation;



import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration.TipoCampo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Daniel Franco
 * @since 24/05/2016 10:18
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Campo {

    int posicao();
    String identificador() default "";
    boolean obrigatorio() default true;
    int tamanho() default 1;
    int qtdCaracteresObrigatorios() default 1;
    int qtdCasasDecimais() default 0;
    TipoCampo tipo();
    String formato() default "";

}
