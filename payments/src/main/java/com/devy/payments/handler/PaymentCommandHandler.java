package com.devy.payments.handler;

import com.devy.common.event.EventInfo;
import com.devy.common.event.payment.PaymentFailedEvent;
import com.devy.common.event.payment.PaymentSucceedEvent;
import com.devy.common.event.payment.command.DoPaymentCommand;
import com.devy.payments.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static com.devy.common.event.EventInfo.PAYMENTS.DO_PAYMENT_COMMAND_CLASS;

@Component
public class PaymentCommandHandler {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public PaymentCommandHandler(ObjectMapper objectMapper, PaymentService paymentService, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = {EventInfo.PAYMENTS.PAYMENT_COMMAND_TOPIC})
    public void handleProductCommand(String message, Acknowledgment acknowledgment) {
        log.info("Received message: {}", message);
        JsonNode jsonNode = objectMapper.readTree(message);
        JsonNode eventId = jsonNode.get("eventId");
        JsonNode eventType = jsonNode.get("eventType");
        if (DO_PAYMENT_COMMAND_CLASS.getName().equals(eventType.asString())) {
            DoPaymentCommand doPaymentCommand = objectMapper.treeToValue(jsonNode, DO_PAYMENT_COMMAND_CLASS);
            try {
                paymentService.pay(
                        doPaymentCommand.getOrderId(),
                        doPaymentCommand.getUserId(),
                        doPaymentCommand.getTotalAmount()
                );
                log.info("ReserveInventoryCommand: {}", doPaymentCommand);
                PaymentSucceedEvent paymentSucceedEvent = new PaymentSucceedEvent(
                        doPaymentCommand.getOrderId(),
                        doPaymentCommand.getUserId()
                );
                kafkaTemplate.send(EventInfo.PAYMENTS.PAYMENT_EVENT_TOPIC,
                        objectMapper.writeValueAsString(paymentSucceedEvent));
            } catch (Exception e) {
                log.error("Failed to pay inventoryReservedEvent: {}", message, e);
                PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent(
                        doPaymentCommand.getOrderId(),
                        doPaymentCommand.getUserId()
                );
                kafkaTemplate.send(EventInfo.PAYMENTS.PAYMENT_EVENT_TOPIC,
                        objectMapper.writeValueAsString(paymentFailedEvent));
            }
        }
        acknowledgment.acknowledge();
    }
}
