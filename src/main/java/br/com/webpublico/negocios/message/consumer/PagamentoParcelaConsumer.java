package br.com.webpublico.negocios.message.consumer;

import br.com.webpublico.negocios.ProcessoCobrancaSPCFacade;
import br.com.webpublico.negocios.message.RabbitMQFacade;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Singleton
@Startup
public class PagamentoParcelaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PagamentoParcelaConsumer.class);
    private static final String QUEUE_NAME = "pagamentoParcela";
    private Connection connection;
    private Channel channel;

    @EJB
    private RabbitMQFacade rabbitMQFacade;
    @EJB
    private ProcessoCobrancaSPCFacade processoCobrancaSPCFacade;

    @PostConstruct
    public void init() {
        try {
            connection = rabbitMQFacade.createConnection();
            channel = rabbitMQFacade.createChanel(connection);

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.basicConsume(QUEUE_NAME, true, (consumerTag, delivery) -> {
                Long idParcela = new Long(new String(delivery.getBody(), StandardCharsets.UTF_8));
                processoCobrancaSPCFacade.estornarParcelaPaga(idParcela);
            }, consumerTag -> {});
        } catch (Exception e) {
            logger.error("Erro ao criar o consumidor da fila pagamentoParcela. Erro: {}", e.getMessage());
            logger.debug("Detalhes do erro ao criar o consumidor da fila pagamentoParcela.", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (channel != null) channel.close();
            if (connection != null) connection.close();
        } catch (IOException | TimeoutException e) {
            logger.error("Erro ao fechar connection e channel da fila pagamentoParcela. Erro: {}", e.getMessage());
            logger.debug("Detalhes do erro ao fechar connection e channel da fila pagamentoParcela.", e);
        }
    }
}
