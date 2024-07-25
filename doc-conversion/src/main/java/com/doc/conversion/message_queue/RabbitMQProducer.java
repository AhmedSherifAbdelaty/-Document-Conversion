package com.doc.conversion.message_queue;

import com.doc.conversion.dto.DocumentConversionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMQProducer {


    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.json.key}")
    private String routingJsonKey;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(DocumentConversionRequest request) {
        log.info("Sending document conversion request {} " , request);
        rabbitTemplate.convertAndSend(exchange, routingJsonKey, request);
    }
}
