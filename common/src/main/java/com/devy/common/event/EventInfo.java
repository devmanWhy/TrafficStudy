package com.devy.common.event;

import com.devy.common.event.order.OrderPlacedEvent;

public class EventInfo {
    public static class ORDERS {
        public static final String ORDER_EVENT_TOPIC = "order-events";
        public static final Class<OrderPlacedEvent> ORDER_PLACED_EVENT_CLASS = OrderPlacedEvent.class;
    }
}
