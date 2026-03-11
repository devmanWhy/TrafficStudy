package com.devy.payments.repository.message;

import com.devy.common.event.payment.PaymentEvent;

public interface PaymentEventMessageRepository {
    public void publish(PaymentEvent event);
}
