package br.com.webpublico.negocios.tributario.executores;

import br.com.webpublico.negocios.RemessaProtestoFacade;

public class RetornoRemessaProtestoExecutor extends AbstractExecutor<Object> {

    private RemessaProtestoFacade remessaFacade;

    private RetornoRemessaProtestoExecutor() {
    }

    public static RetornoRemessaProtestoExecutor build(RemessaProtestoFacade remessaFacade) {
        RetornoRemessaProtestoExecutor retornoRemessaProtestoExecutor = new RetornoRemessaProtestoExecutor();
        retornoRemessaProtestoExecutor.setRemessaFacade(remessaFacade);
        return retornoRemessaProtestoExecutor;
    }

    private void setRemessaFacade(RemessaProtestoFacade remessaFacade) {
        this.remessaFacade = remessaFacade;
    }

    @Override
    protected void executeInTransactionWithoutResult(Object assistente) {
        remessaFacade.consultarRetornoRemessa();
    }
}
