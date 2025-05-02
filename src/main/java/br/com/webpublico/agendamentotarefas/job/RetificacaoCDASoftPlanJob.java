package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceIntegracaoSofPlan;
import org.springframework.beans.factory.annotation.Autowired;

public class RetificacaoCDASoftPlanJob extends WPJob {
    @Autowired
    protected ServiceIntegracaoSofPlan serviceIntegracaoSofPlan;


    @Override
    public void execute() {
        try {
            serviceIntegracaoSofPlan.retificaCDA();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Integração com o sistema PGM.net - Retificacao de Pessoas";
    }
}
