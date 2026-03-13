package com.devy.orchestrator.order;

import com.devy.common.event.BaseCommand;
import com.devy.common.event.BaseEvent;
import com.devy.common.event.EventInfo;
import com.devy.common.event.order.OrderPlacedEvent;
import com.devy.common.event.order.command.CancelOrderCommand;
import com.devy.common.event.payment.command.DoPaymentCommand;
import com.devy.common.event.product.command.ReleaseInventoryCommand;
import com.devy.common.event.product.command.ReserveInventoryCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private final List<BaseEvent> processedEvents;
    private final String orderId;

    public CommandManager(String orderId) {
        this.orderId = orderId;
        this.processedEvents = new ArrayList<>();
    }

    public void apply(BaseEvent event) {
        if (event.eventId.equals(orderId)) {
            processedEvents.add(event);
        }
        throw new RuntimeException("Event not for this order : eventId : " + event.eventId + " orderId : " + orderId + "");
    }

    public BaseCommand getCommand() {
        BaseCommand command = null;
        OrderPlacedEvent orderPlacedEvent = getOrderPlacedEvent();
        if (isOrderPlacedEvent()) {
            command = new ReserveInventoryCommand(
                    orderPlacedEvent.eventId,
                    orderPlacedEvent.getProductId(),
                    orderPlacedEvent.getQuantity()
            );
        }
        if (isInventoryReservedEvent()) {
            command = new DoPaymentCommand(
                    orderPlacedEvent.eventId,
                    orderPlacedEvent.getUserId(),
                    orderPlacedEvent.getTotalAmount()
            );
        }

        if (isInventoryReleasedEvent()) {
            command = new CancelOrderCommand(
                    orderPlacedEvent.eventId,
                    orderPlacedEvent.getUserId()
            );
        }

        if (isPaymentFailedEvent()) {
            command = new ReleaseInventoryCommand(
                    orderPlacedEvent.eventId,
                    orderPlacedEvent.getProductId(),
                    orderPlacedEvent.getQuantity()
            );
        }

        return command;
    }

    private boolean isOrderPlacedEvent() {
        return processedEvents.getLast().eventType.equals(EventInfo.ORDERS.ORDER_PLACED_EVENT_CLASS.getName());
    }

    private boolean isInventoryReservedEvent() {
        return processedEvents.getLast().eventType.equals(EventInfo.PRODUCTS.INVENTORY_RESERVED_EVENT_CLASS.getName());
    }

    private boolean isInventoryReleasedEvent() {
        return processedEvents.getLast().eventType.equals(EventInfo.PRODUCTS.INVENTORY_RELEASED_EVENT_CLASS.getName());
    }

    private boolean isPaymentFailedEvent() {
        return processedEvents.getLast().eventType.equals(EventInfo.PAYMENTS.PAYMENT_FAILED_EVENT_CLASS.getName());
    }

    private OrderPlacedEvent getOrderPlacedEvent() {
        return (OrderPlacedEvent) processedEvents.getFirst();
    }
}
