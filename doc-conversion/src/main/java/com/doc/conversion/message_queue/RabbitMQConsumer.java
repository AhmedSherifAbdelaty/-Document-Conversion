package com.doc.conversion.message_queue;

import com.doc.conversion.dto.DocumentConversionRequest;
import com.doc.conversion.service.DocumentConversionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class RabbitMQConsumer {

    private final DocumentConversionService documentConversionService;

    @RabbitListener(queues = {"${rabbitmq.queue.json.name}"})
    public void consume(DocumentConversionRequest documentConversionRequest) {
        try {
            documentConversionService.convertDocument(documentConversionRequest.getDocumentId(), documentConversionRequest.getTargetFormat());
        } catch (Exception e) {
            log.error("Error while converting document", e);
        }
    }
}
