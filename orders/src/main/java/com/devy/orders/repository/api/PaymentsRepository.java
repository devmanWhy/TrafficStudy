package com.devy.orders.repository.api;

import com.devy.orders.repository.api.response.SearchPaymentInfoResponseDTO;

public interface PaymentsRepository {
    public SearchPaymentInfoResponseDTO searchPaymentInfo(String orderId, String userId);
    public boolean pay(String orderId, String userId, long amount);
}
