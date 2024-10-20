package co.edu.uniquindio.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // Enviar un mensaje a RabbitMQ
    public void sendMessage(String action, String email) {
        String message = "{\"action\":\"" + action + "\", \"email\":\"" + email + "\"}";
        rabbitTemplate.convertAndSend("user_queue", message);
        System.out.println("Sent message: " + message);
    }
}