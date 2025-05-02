package br.com.webpublico.message.consumer;

import br.com.webpublico.negocios.message.RabbitMQFacade;
import br.com.webpublico.negocios.rh.esocial.RegistroESocialFacade;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
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
public class EventoEsocialConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EventoEsocialConsumer.class);

    private static final String QUEUE_NAME = "eventoesocial.queue";
    private Connection connection;
    private Channel channel;
    @EJB
    private RabbitMQFacade rabbitMQFacade;
    @EJB
    private RegistroESocialFacade registroESocialFacade;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @PostConstruct
    public void init() {
        try {
            connection = rabbitMQFacade.createConnection();
            channel = rabbitMQFacade.createChanel(connection);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                logger.debug("Mensagem recebida: {}", message);
                try {
                    JsonNode jsonNode = objectMapper.readTree(message);
                    registroESocialFacade.criarEventoESocial(jsonNode);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (Exception e) {
                    logger.error("Erro ao processar mensagem: {}", e.getMessage());
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                }
            };
            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });
        } catch (Exception e) {
            logger.error("Erro ao criar o consumidor da fila eventoesocial. Erro: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (channel != null) channel.close();
            if (connection != null) connection.close();
        } catch (IOException | TimeoutException e) {
            logger.error("Erro ao fechar connection e channel da fila eventoesocial. Erro: {}", e.getMessage());
            logger.debug("Detalhes do erro ao fechar connection e channel da fila eventoesocial.", e);
        }
    }
}
