package br.com.webpublico.negocios.message;

import com.google.common.base.Strings;
import com.rabbitmq.client.*;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;

@Stateless
public class RabbitMQFacade implements Serializable {

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

    public Connection createConnection() throws IOException, TimeoutException {
        return factory.newConnection();
    }

    public Channel createChanel(Connection connection) throws IOException, TimeoutException {
        return connection.createChannel();
    }

    public void createConsumer(String queueName, DeliverCallback deliverCallback) {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
