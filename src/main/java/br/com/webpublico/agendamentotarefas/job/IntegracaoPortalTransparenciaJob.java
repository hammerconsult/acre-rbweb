package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.contabil.services.ServiceIntegracaoPortalTransparencia;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author renat
 * @since 13/05/2016
 */
public class IntegracaoPortalTransparenciaJob extends WPJob {

    @Autowired
    protected ServiceIntegracaoPortalTransparencia serviceIntegracaoPortalTransparencia;

    @Override
    public void execute() {
        try {
            serviceIntegracaoPortalTransparencia.sincronizar();
        } catch (Exception e) {
            logger.warn("Erro executando Job", e);
        }
    }

    @Override
    public String toString() {
        return "Integração com o Portal da Transparência.";
    }

}
