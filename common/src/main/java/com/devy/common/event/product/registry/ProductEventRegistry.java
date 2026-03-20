package com.devy.common.event.product.registry;

import com.devy.common.event.EventInfo;
import com.devy.common.event.product.ProductEvent;

import java.util.HashMap;
import java.util.Map;

public class ProductEventRegistry {
    private static final Map<String, Class<? extends ProductEvent>> PRODUCT_MAP = new HashMap<>();

    static {
        PRODUCT_MAP.put(EventInfo.PRODUCTS.INVENTORY_CREATED_EVENT_CLASS.getName(), EventInfo.PRODUCTS.INVENTORY_CREATED_EVENT_CLASS);
        PRODUCT_MAP.put(EventInfo.PRODUCTS.INVENTORY_RESERVED_EVENT_CLASS.getName(), EventInfo.PRODUCTS.INVENTORY_RESERVED_EVENT_CLASS);
        PRODUCT_MAP.put(EventInfo.PRODUCTS.INVENTORY_RELEASED_EVENT_CLASS.getName(), EventInfo.PRODUCTS.INVENTORY_RELEASED_EVENT_CLASS);
    }

    public static Class<? extends ProductEvent> getEventClass(String eventType) {
        return PRODUCT_MAP.get(eventType);
    }
}
