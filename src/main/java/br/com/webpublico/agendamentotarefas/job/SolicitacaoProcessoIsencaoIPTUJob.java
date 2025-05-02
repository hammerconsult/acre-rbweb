package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.services.ServiceSolicitacaoProcessoIsencaoIPTU;
import org.springframework.beans.factory.annotation.Autowired;

public class SolicitacaoProcessoIsencaoIPTUJob extends WPJob {

    @Autowired
    private ServiceSolicitacaoProcessoIsencaoIPTU serviceSolicitacaoProcessoIsencaoIPTU;

    @Override
    public void execute() {
        try {
            serviceSolicitacaoProcessoIsencaoIPTU.atualizarSituacaoProcessoIsencaoIPTU();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Atualizando Situações dos Processos de Solicitação vencidos de 'Deferido' para 'Vencido'";
    }
}
