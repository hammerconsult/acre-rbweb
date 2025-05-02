package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.executores.SituacaoParcelaProtestoExecutor;

public class SituacaoParcelaProtestoJob extends WPJob {
    @Override
    public void execute() {
        SituacaoParcelaProtestoExecutor.build().executeFutureWithoutResult();
    }
}
