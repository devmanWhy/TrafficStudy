package com.devy.payments.service;

import com.devy.common.event.payment.PaymentFailedEvent;
import com.devy.common.event.payment.PaymentSucceedEvent;
import com.devy.payments.controller.request.SearchPaymentInfoRequestDTO;
import com.devy.payments.controller.response.SearchPaymentInfoResponseDTO;
import com.devy.payments.domain.Outbox;
import com.devy.payments.domain.Payment;
import com.devy.payments.repository.jpa.OutboxRepository;
import com.devy.payments.repository.jpa.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final PaymentRepository paymentRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public PaymentServiceImpl(PaymentRepository paymentRepository, OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean pay(String orderId, String userId, long amount) {
        try {
            log.info("Paying order: {} for amount: {}", orderId, amount);
            String paymentId = java.util.UUID.randomUUID().toString();
            PaymentSucceedEvent paymentSucceedEvent = new PaymentSucceedEvent(orderId, userId);
            paymentRepository.save(new Payment(paymentId, orderId, userId, amount));
            outboxRepository.save(new Outbox(
                    orderId,
                    paymentSucceedEvent.getClass().getName(),
                    objectMapper.writeValueAsString(paymentSucceedEvent)
            ));
            return true;
        } catch (Exception e) {
            PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent(orderId, userId);
            outboxRepository.save(new Outbox(
                    orderId,
                    paymentFailedEvent.getClass().getName(),
                    objectMapper.writeValueAsString(paymentFailedEvent)
            ));
            log.error("Failed to pay order: {} for amount: {}", orderId, amount, e);
        }

        return false;
    }

    @Override
    public SearchPaymentInfoResponseDTO searchPaymentsInfo(SearchPaymentInfoRequestDTO request) {
        log.info("Searching payments info for order: {}", request);
        Optional<Payment> existsPaymentOptional = paymentRepository.findByOrderIdAndUserId(request.orderId(), request.userId());
        if (existsPaymentOptional.isPresent()) {
            Payment existsPayment = existsPaymentOptional.get();
            return new SearchPaymentInfoResponseDTO(
                    existsPayment.getPaymentId(),
                    existsPayment.getAmount(),
                    existsPayment.getPaidAt());
        }
        return null;
    }
}
