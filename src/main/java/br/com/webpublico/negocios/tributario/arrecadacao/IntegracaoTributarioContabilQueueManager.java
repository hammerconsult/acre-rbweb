package br.com.webpublico.negocios.tributario.arrecadacao;

import br.com.webpublico.ws.procuradoria.IntegraSoftplanFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


@Singleton
@Startup
public class IntegracaoTributarioContabilQueueManager {
    private final Logger logger = LoggerFactory.getLogger(IntegracaoTributarioContabilQueueManager.class);
    private BlockingQueue<Runnable> taskQueue;
    private Thread workerThread;

    @PostConstruct
    public void init() {
        taskQueue = new LinkedBlockingQueue<>();
        workerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Consome as tarefas da fila e executa
                    Runnable task = taskQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        workerThread.start();
    }

    @Lock(LockType.WRITE)
    public void enqueueProcess(Runnable task) {
        taskQueue.add(task);
    }

    @PreDestroy
    public void cleanUp() {
        if (workerThread != null && workerThread.isAlive()) {
            workerThread.interrupt();
        }
    }

    // Exemplo de um cron para verificar se há tarefas enfileiradas
    @Schedule(hour = "*", minute = "*/5", persistent = false)
    public void checkQueue() {
        logger.info("Fila tem " + taskQueue.size() + " tarefas.");
    }
}
