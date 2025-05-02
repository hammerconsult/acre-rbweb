package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceIssForaMunicipio;
import org.springframework.beans.factory.annotation.Autowired;

public class IssForaMunicipioJob extends WPJob {
    @Autowired
    protected ServiceIssForaMunicipio serviceIssForaMunicipio;

    @Override
    public void execute() {
        try {
            serviceIssForaMunicipio.cancelarDebitosVencidos();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Cancelar Débitos de ISS de Fora do Município";
    }
}
