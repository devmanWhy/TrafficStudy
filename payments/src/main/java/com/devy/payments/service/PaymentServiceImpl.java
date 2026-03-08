package com.devy.payments.service;

import com.devy.payments.controller.request.SearchPaymentInfoRequestDTO;
import com.devy.payments.controller.response.SearchPaymentInfoResponseDTO;
import com.devy.payments.domain.Payment;
import com.devy.payments.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public boolean pay(String orderId, String userId, long amount) {
        log.info("Paying order: {} for amount: {}", orderId, amount);
        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String paymentId = java.util.UUID.randomUUID().toString();
        paymentRepository.save(new Payment(paymentId, orderId, userId, amount));
        return true;
    }

    @Override
    public SearchPaymentInfoResponseDTO searchPaymentsInfo(SearchPaymentInfoRequestDTO request) {
        log.info("Searching payments info for order: {}", request);
        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
