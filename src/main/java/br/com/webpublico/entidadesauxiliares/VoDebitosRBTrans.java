package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CalculoRBTrans;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

/**
 * Created by AndreGustavo on 20/08/2014.
 */
public class VoDebitosRBTrans {
    String descricao;
    ResultadoParcela resultadoParcela;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ResultadoParcela getResultadoParcela() {
        return resultadoParcela;
    }

    public void setResultadoParcela(ResultadoParcela resultadoParcela) {
        this.resultadoParcela = resultadoParcela;
    }
}
