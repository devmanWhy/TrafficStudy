package com.devy.common.event.product;

import com.devy.common.event.BaseEvent;

public class ProductEvent extends BaseEvent {

    public ProductEvent(String eventId, String eventType) {
        super(eventId, eventType);
    }

    public ProductEvent() {
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
