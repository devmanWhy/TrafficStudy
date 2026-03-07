package com.devy.payments.service;

import com.devy.payments.controller.request.SearchPaymentInfoRequestDTO;
import com.devy.payments.controller.response.SearchPaymentInfoResponseDTO;

public interface PaymentService {
    public boolean pay(String orderId, String userId, long amount);

    public SearchPaymentInfoResponseDTO searchPaymentsInfo(SearchPaymentInfoRequestDTO request);
}
