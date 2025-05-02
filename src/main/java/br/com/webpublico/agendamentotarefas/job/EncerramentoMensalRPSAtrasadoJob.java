package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.nfse.facades.RPSFacade;
import br.com.webpublico.util.Util;

public class EncerramentoMensalRPSAtrasadoJob extends WPJob {

    private RPSFacade RPSFacade;

    public RPSFacade getRPSFacade() {
        if (RPSFacade == null) {
            RPSFacade = (RPSFacade) Util.getFacadeViaLookup("java:module/RPSFacade");
        }
        return RPSFacade;
    }

    @Override
    public void execute() {
        try {
            getRPSFacade().encerrarcCompetenciaAnterior();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Fechamento Mensal de RPS Atrasados";
    }
}
