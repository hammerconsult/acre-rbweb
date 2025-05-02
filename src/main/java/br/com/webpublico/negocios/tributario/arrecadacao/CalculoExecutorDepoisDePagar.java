package br.com.webpublico.negocios.tributario.arrecadacao;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.negocios.AbstractFacade;

public abstract class CalculoExecutorDepoisDePagar<T> extends AbstractFacade<T> {

    public CalculoExecutorDepoisDePagar(Class<T> classe) {
        super(classe);
    }

    public abstract void depoisDePagar(Calculo calculo);
}
