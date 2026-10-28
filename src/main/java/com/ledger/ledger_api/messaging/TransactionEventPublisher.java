package com.ledger.ledger_api.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public TransactionEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(TransactionEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRANSACTION_QUEUE,
                event
        );
    }
}