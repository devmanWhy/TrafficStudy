package com.devy.common.event;

import com.devy.common.event.order.OrderPlacedEvent;
import com.devy.common.event.product.InventoryReservedEvent;

public class EventInfo {
    public static class ORDERS {
        public static final String ORDER_EVENT_TOPIC = "order-events";
        public static final Class<OrderPlacedEvent> ORDER_PLACED_EVENT_CLASS = OrderPlacedEvent.class;

    }

    public static class PRODUCTS {
        public static final String PRODUCT_EVENT_TOPIC = "product-events";
        public static final Class<InventoryReservedEvent> INVENTORY_RESERVED_EVENT_CLASS = InventoryReservedEvent.class;
    }
}
