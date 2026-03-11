package com.devy.common.event.payment;

import com.devy.common.event.BaseEvent;

public class PaymentEvent extends BaseEvent {

    public PaymentEvent(String eventId, String eventType) {
        super(eventId, eventType);
    }

    public PaymentEvent() {
        super();
    }

    @Override
    public String toString() {
        return "ProductEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
