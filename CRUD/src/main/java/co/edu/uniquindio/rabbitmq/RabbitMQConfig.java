package co.edu.uniquindio.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "user_queue";

    @Bean
    public Queue userQueue() {
        return new Queue(QUEUE_NAME, true);
    }
}
