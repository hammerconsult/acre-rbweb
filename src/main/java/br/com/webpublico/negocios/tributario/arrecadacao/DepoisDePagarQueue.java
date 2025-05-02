package br.com.webpublico.negocios.tributario.arrecadacao;

import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.negocios.message.RabbitMQFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import java.util.LinkedList;
import java.util.Queue;


@Singleton
public class DepoisDePagarQueue {
    private final Queue<DepoisDePagarResquest> taskQueue = new LinkedList<>();
    private static final Logger logger = LoggerFactory.getLogger(DepoisDePagarQueue.class);

    @EJB
    private DepoisDePagarQueueManager depoisDePagarQueueManager;
    @EJB
    private RabbitMQFacade rabbitMQFacade;

    @Lock(LockType.WRITE)
    public void enqueueProcess(DepoisDePagarResquest loteBaixa) {
        comunicarRabbitMQ(loteBaixa);
        taskQueue.add(loteBaixa);
        depoisDePagarQueueManager.processNextTask();
    }

    private void comunicarRabbitMQ(DepoisDePagarResquest depoisDePagarResquest) {
        try {
            for (ParcelaValorDivida parcela : depoisDePagarResquest.getParcelas()) {
                rabbitMQFacade.basicPublish("pagamentoParcela", parcela.getId().toString().getBytes());
            }
        } catch (Exception e) {
            logger.error("Erro ao comunicar ao RabbitMQ novo pagamento de parcela. Erro: {}", e.getMessage());
            logger.debug("Detalhes do erro ao comunicar ao RabbitMQ novo pagamento de parcela.", e);
        }
    }

    @Lock(LockType.WRITE)
    public DepoisDePagarResquest getNext() {
        return taskQueue.poll();
    }
}
