package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceAtualizacaoViewMaterializadaBI;
import org.springframework.beans.factory.annotation.Autowired;

public class AtualizacaoViewMaterializadaBIJob extends WPJob {

    @Autowired
    private ServiceAtualizacaoViewMaterializadaBI serviceAtualizacaoViewMaterializadaBI;

    @Override
    public void execute() {
        try {
            serviceAtualizacaoViewMaterializadaBI.atualizarViewMaterializadaBI();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Atualização da View Materializada da Exportação do BI";
    }
}
