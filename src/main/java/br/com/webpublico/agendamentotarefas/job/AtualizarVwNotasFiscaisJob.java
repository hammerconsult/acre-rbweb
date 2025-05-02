package br.com.webpublico.agendamentotarefas.job;

import br.com.webpublico.negocios.tributario.dao.JdbcNotaFiscalDao;
import org.springframework.beans.factory.annotation.Autowired;

public class AtualizarVwNotasFiscaisJob extends WPJob {

    @Autowired
    private JdbcNotaFiscalDao dao;

    @Override
    public void execute() {
        dao.atualizarVwNotasFiscais();
    }

    @Override
    public String toString() {
        return "Atualizando vwnotasfiscais";
    }
}
