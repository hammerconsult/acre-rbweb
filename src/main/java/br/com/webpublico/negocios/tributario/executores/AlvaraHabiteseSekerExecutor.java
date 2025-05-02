package br.com.webpublico.negocios.tributario.executores;

import br.com.webpublico.controle.AlvaraHabiteseSekerService;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Util;

public class AlvaraHabiteseSekerExecutor extends AbstractExecutor<AssistenteBarraProgresso> {

    private AlvaraHabiteseSekerExecutor() {
    }

    public static AlvaraHabiteseSekerExecutor build() {
        return new AlvaraHabiteseSekerExecutor();
    }

    @Override
    protected AssistenteBarraProgresso executeInTransaction(AssistenteBarraProgresso assistente) {
        AlvaraHabiteseSekerService alvaraSekerService = getServiceRemessaProtesto();
        return alvaraSekerService.migrarAlvarasSeker(assistente);
    }

    private AlvaraHabiteseSekerService getServiceRemessaProtesto() {
        return Util.recuperarSpringBean(AlvaraHabiteseSekerService.class);
    }
}
