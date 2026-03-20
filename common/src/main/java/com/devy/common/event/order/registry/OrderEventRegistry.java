package com.devy.common.event.order.registry;

import com.devy.common.event.EventInfo;
import com.devy.common.event.order.OrderEvent;

import java.util.HashMap;
import java.util.Map;

public class OrderEventRegistry {
    private static final Map<String, Class<? extends OrderEvent>> ORDER_MAP = new HashMap<>();

    static {
        ORDER_MAP.put(EventInfo.ORDERS.ORDER_PLACED_EVENT_CLASS.getName(), EventInfo.ORDERS.ORDER_PLACED_EVENT_CLASS);
        ORDER_MAP.put(EventInfo.ORDERS.ORDER_CONFIRMED_EVENT_CLASS.getName(), EventInfo.ORDERS.ORDER_CONFIRMED_EVENT_CLASS);
        ORDER_MAP.put(EventInfo.ORDERS.ORDER_CANCELLED_EVENT_CLASS.getName(), EventInfo.ORDERS.ORDER_CANCELLED_EVENT_CLASS);
    }

    public static Class<? extends OrderEvent> getEventClass(String eventType) {
        return ORDER_MAP.get(eventType);
    }
}
