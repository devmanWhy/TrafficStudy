package com.devy.common.event;

import com.devy.common.event.order.OrderCancelledEvent;
import com.devy.common.event.order.OrderConfirmedEvent;
import com.devy.common.event.order.OrderPlacedEvent;
import com.devy.common.event.order.command.CancelOrderCommand;
import com.devy.common.event.order.command.ConfirmOrderCommand;
import com.devy.common.event.payment.PaymentFailedEvent;
import com.devy.common.event.payment.PaymentSucceedEvent;
import com.devy.common.event.payment.command.DoPaymentCommand;
import com.devy.common.event.product.InventoryCreatedEvent;
import com.devy.common.event.product.InventoryReleasedEvent;
import com.devy.common.event.product.InventoryReservedEvent;
import com.devy.common.event.product.command.CreateInventoryCommand;
import com.devy.common.event.product.command.ReleaseInventoryCommand;
import com.devy.common.event.product.command.ReserveInventoryCommand;

public class EventInfo {
    public static final String OUTBOX_TOPIC = "cdc.traffic_study.outbox";
    public static final String PRODUCT_TOPIC = "cdc.traffic_study.product";
    public static class ORDERS {
        // Command
        public static final String ORDER_COMMAND_TOPIC = "order-commands";
        public static final Class<ConfirmOrderCommand> CONFIRM_ORDER_COMMAND_CLASS = ConfirmOrderCommand.class;
        public static final Class<CancelOrderCommand> CANCEL_ORDER_COMMAND_CLASS = CancelOrderCommand.class;

        // Result
        public static final String ORDER_EVENT_TOPIC = "order-events";
        public static final Class<OrderPlacedEvent> ORDER_PLACED_EVENT_CLASS = OrderPlacedEvent.class;
        public static final Class<OrderConfirmedEvent> ORDER_CONFIRMED_EVENT_CLASS = OrderConfirmedEvent.class;
        public static final Class<OrderCancelledEvent> ORDER_CANCELLED_EVENT_CLASS = OrderCancelledEvent.class;

    }

    public static class PRODUCTS {
        // Command
        public static final String PRODUCT_COMMAND_TOPIC = "product-commands";
        public static final Class<CreateInventoryCommand> CREATE_INVENTORY_COMMAND_CLASS = CreateInventoryCommand.class;
        public static final Class<ReserveInventoryCommand> RESERVE_INVENTORY_COMMAND_CLASS = ReserveInventoryCommand.class;
        public static final Class<ReleaseInventoryCommand> RELEASE_INVENTORY_COMMAND_CLASS = ReleaseInventoryCommand.class;


        // Result
        public static final String PRODUCT_EVENT_TOPIC = "product-events";
        public static final Class<InventoryCreatedEvent> INVENTORY_CREATED_EVENT_CLASS = InventoryCreatedEvent.class;
        public static final Class<InventoryReservedEvent> INVENTORY_RESERVED_EVENT_CLASS = InventoryReservedEvent.class;
        public static final Class<InventoryReleasedEvent> INVENTORY_RELEASED_EVENT_CLASS = InventoryReleasedEvent.class;

    }

    public static class PAYMENTS {
        // Command
        public static final String PAYMENT_COMMAND_TOPIC = "payment-commands";
        public static final Class<DoPaymentCommand> DO_PAYMENT_COMMAND_CLASS = DoPaymentCommand.class;

        // Result
        public static final String PAYMENT_EVENT_TOPIC = "payment-events";
        public static final Class<PaymentSucceedEvent> PAYMENT_SUCCEED_EVENT_CLASS = PaymentSucceedEvent.class;
        public static final Class<PaymentFailedEvent> PAYMENT_FAILED_EVENT_CLASS = PaymentFailedEvent.class;

    }
}
