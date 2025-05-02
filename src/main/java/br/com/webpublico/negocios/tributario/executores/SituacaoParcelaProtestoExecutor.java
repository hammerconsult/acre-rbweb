package br.com.webpublico.negocios.tributario.executores;

import br.com.webpublico.negocios.tributario.services.ServiceRemessaProtesto;
import br.com.webpublico.util.Util;

public class SituacaoParcelaProtestoExecutor extends AbstractExecutor<Object> {

    private SituacaoParcelaProtestoExecutor() {}

    public static SituacaoParcelaProtestoExecutor build() {
        return new SituacaoParcelaProtestoExecutor();
    }

    @Override
    protected void executeInTransactionWithoutResult(Object assistente) {
        ServiceRemessaProtesto serviceRemessaProtesto = getServiceRemessaProtesto();
        serviceRemessaProtesto.atualizarSituacaoDeProtestoDasParcelas();
    }

    public ServiceRemessaProtesto getServiceRemessaProtesto() {
        return Util.recuperarSpringBean(ServiceRemessaProtesto.class);
    }
}
