package br.com.webpublico.message;

import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;

@Service
public class RabbitMQService implements Serializable {
    final ConnectionFactory factory = new ConnectionFactory();

    @PostConstruct
    public void init() {
        factory.setHost(getEnvOrElse("RABBITMQ_HOST", "192.168.1.24"));
        factory.setPort(Integer.parseInt(getEnvOrElse("RABBITMQ_PORT", "5672")));
        factory.setUsername(getEnvOrElse("RABBITMQ_USER", "user"));
        factory.setPassword(getEnvOrElse("RABBITMQ_PASSWORD", "senha10"));
    }

    private String getEnvOrElse(String env, String orElse) {
        return Strings.isNullOrEmpty(System.getenv(env)) ? orElse : System.getenv(env);
    }

    public static RabbitMQService getService() {
        return (RabbitMQService) Util.getSpringBeanPeloNome("rabbitMQService");
    }

    public void basicPublish(String queue, byte[] msg) throws IOException {
        Connection connection = null;
        try {
            connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(queue, true, false, false, null);
            channel.basicPublish("", queue, null, msg);
            channel.close();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public void basicPublishExchange(String exchange, byte[] msg) throws IOException {
        Connection connection = null;
        try {
            connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT, true);
            channel.basicPublish(exchange, "", null, msg);
            channel.close();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
