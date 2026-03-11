package com.devy.common.event;

import com.devy.common.event.order.OrderPlacedEvent;
import com.devy.common.event.payment.PaymentFailedEvent;
import com.devy.common.event.product.InventoryReleasedEvent;
import com.devy.common.event.product.InventoryReservedEvent;

public class EventInfo {
    public static class ORDERS {
        public static final String ORDER_EVENT_TOPIC = "order-events";
        public static final Class<OrderPlacedEvent> ORDER_PLACED_EVENT_CLASS = OrderPlacedEvent.class;

    }

    public static class PRODUCTS {
        public static final String PRODUCT_EVENT_TOPIC = "product-events";
        public static final Class<InventoryReservedEvent> INVENTORY_RESERVED_EVENT_CLASS = InventoryReservedEvent.class;
        public static final Class<InventoryReleasedEvent> INVENTORY_RELEASED_EVENT_CLASS = InventoryReleasedEvent.class;
    }

    public static class PAYMENTS {
        public static final String PAYMENT_EVENT_TOPIC = "payment-events";
        public static final Class<PaymentFailedEvent> PAYMENT_FAILED_EVENT_CLASS = PaymentFailedEvent.class;
    }
}
