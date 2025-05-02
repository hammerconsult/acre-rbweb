package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.controle.TransferenciaParametrosTributarioControlador;

public class AssistenteTransferenciaExercicio {

    private TransferenciaParametrosTributarioControlador.TransferenciaParametroTributario transferenciaParametroTributario;
    private boolean sucesso;

    public AssistenteTransferenciaExercicio() {
    }

    public AssistenteTransferenciaExercicio(TransferenciaParametrosTributarioControlador.TransferenciaParametroTributario transferenciaParametroTributario, boolean sucesso) {
        this.transferenciaParametroTributario = transferenciaParametroTributario;
        this.sucesso = sucesso;
    }

    public TransferenciaParametrosTributarioControlador.TransferenciaParametroTributario getTransferenciaParametroTributario() {
        return transferenciaParametroTributario;
    }

    public void setTransferenciaParametroTributario(TransferenciaParametrosTributarioControlador.TransferenciaParametroTributario transferenciaParametroTributario) {
        this.transferenciaParametroTributario = transferenciaParametroTributario;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }
}
