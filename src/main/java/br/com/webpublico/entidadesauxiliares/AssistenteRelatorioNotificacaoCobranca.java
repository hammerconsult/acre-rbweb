package br.com.webpublico.entidadesauxiliares;

import java.util.List;

/**
 * Created by mga on 30/06/2017.
 */
public class AssistenteRelatorioNotificacaoCobranca {

    private List<NotificacaoCobrancaMalaDireta> resultado;

    public AssistenteRelatorioNotificacaoCobranca(List<NotificacaoCobrancaMalaDireta> resultado) {
        this.resultado = resultado;
    }

    public List<NotificacaoCobrancaMalaDireta> getResultado() {
        return resultado;
    }

    public void setResultado(List<NotificacaoCobrancaMalaDireta> resultado) {
        this.resultado = resultado;
    }
}
