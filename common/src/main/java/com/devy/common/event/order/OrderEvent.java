package com.devy.common.event.order;

import com.devy.common.event.BaseEvent;

public class OrderEvent extends BaseEvent {

    public OrderEvent(String eventId, String eventType) {
        super(eventId, eventType);
    }

    public OrderEvent() {
        super();
    }

    @Override
    public String toString() {
        return "OrderEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
