package br.com.webpublico.script.rh;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.interfaces.EntidadePagavelRH;
import br.com.webpublico.interfaces.ExecutaScript;
import br.com.webpublico.interfaces.ExecutaScriptEventoFP;
import br.com.webpublico.script.Resultado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Executa a regra e a formula de um EventoFP
 *
 * @author Gécen Dacome De Marchi
 */
public class ExecutaScriptEventoFPImpl implements ExecutaScriptEventoFP, Serializable {

    private ExecutaScript executaScript;
    protected static final Logger logger = LoggerFactory.getLogger(ExecutaScriptEventoFPImpl.class);

    public ExecutaScriptEventoFPImpl(ExecutaScript executaScript) {
        this.executaScript = executaScript;

    }

    @Override
    public Resultado<Boolean> executaRegra(EventoFP eventoFP, EntidadePagavelRH entidadePagavelRH) {
        return executaScript.invocar(eventoFP.nomeFuncaoRegra(), Boolean.class, entidadePagavelRH, eventoFP);
    }

    @Override
    public Resultado<Double> executaFormula(EventoFP eventoFP, EntidadePagavelRH entidadePagavelRH) {
        return executaScript.invocar(eventoFP.nomeFuncaoFormula(), Double.class, entidadePagavelRH, eventoFP);
    }

    @Override
    public Resultado<Double> executaReferencia(EventoFP eventoFP, EntidadePagavelRH entidadePagavelRH) {
        return executaScript.invocar(eventoFP.nomeReferencia(), Double.class, entidadePagavelRH, eventoFP);
    }

    @Override
    public Resultado<Double> executaValorIntegral(EventoFP eventoFP, EntidadePagavelRH entidadePagavelRH) {
        return executaScript.invocar(eventoFP.nomeValorIntegral(), Double.class, entidadePagavelRH, eventoFP);
    }

    @Override
    public Resultado<Double> executaValorBaseDeCalculo(EventoFP eventoFP, EntidadePagavelRH entidadePagavelRH) {
        return executaScript.invocar(eventoFP.nomeValorBaseDeCalculo(), Double.class, entidadePagavelRH, eventoFP);
    }
}
