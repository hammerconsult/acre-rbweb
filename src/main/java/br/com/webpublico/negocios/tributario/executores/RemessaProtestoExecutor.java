package br.com.webpublico.negocios.tributario.executores;

import br.com.webpublico.negocios.tributario.services.ServiceRemessaProtesto;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Util;

public class RemessaProtestoExecutor extends AbstractExecutor<AssistenteBarraProgresso> {

    private RemessaProtestoExecutor() {
    }

    public static RemessaProtestoExecutor build() {
        return new RemessaProtestoExecutor();
    }

    @Override
    protected void executeInTransactionWithoutResult(AssistenteBarraProgresso assistente) {
        ServiceRemessaProtesto serviceRemessaProtesto = getServiceRemessaProtesto();
        serviceRemessaProtesto.enviarRemessaPorAgendamento();
    }

    @Override
    protected AssistenteBarraProgresso executeInTransaction(AssistenteBarraProgresso assistente) {
        ServiceRemessaProtesto serviceRemessaProtesto = getServiceRemessaProtesto();
        return serviceRemessaProtesto.enviarRemessa(assistente);
    }

    private ServiceRemessaProtesto getServiceRemessaProtesto() {
        return Util.recuperarSpringBean(ServiceRemessaProtesto.class);
    }
}
