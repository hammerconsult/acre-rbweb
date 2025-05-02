package br.com.webpublico.agendamentotarefas.job;


import br.com.webpublico.negocios.comum.GarbageCollectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GarbageCollectorJob extends WPJob {


    @Override
    public void execute() {
        GarbageCollectorService.startGarbageColletor();
    }

    @Override
    public String toString() {
        return "Serviço de execução do Garbage Collector";
    }
}
