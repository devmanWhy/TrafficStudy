package com.devy.products.domain;

import com.devy.common.event.BaseCommand;
import com.devy.common.event.BaseEvent;
import com.devy.common.event.product.InventoryCreatedEvent;
import com.devy.common.event.product.InventoryReleasedEvent;
import com.devy.common.event.product.InventoryReservedEvent;
import com.devy.common.event.product.ProductEvent;
import com.devy.common.event.product.command.CreateInventoryCommand;
import com.devy.common.event.product.command.ReleaseInventoryCommand;
import com.devy.common.event.product.command.ReserveInventoryCommand;
import com.devy.common.event.sourcing.AbstractEventSource;

import java.util.List;

public class Inventory extends AbstractEventSource {
    private String productsId;
    private int quantity;

    public Inventory() {
    }

    public Inventory(String productsId, int quantity) {
        this.productsId = productsId;
        this.quantity = quantity;
    }

    public void applyEvents(List<ProductEvent> events) {
        events.forEach(this::applyEvent);
    }


    @Override
    public BaseEvent handleCommand(BaseCommand command) {
        BaseEvent event = null;
        if (command instanceof CreateInventoryCommand createInventoryCommand) {
            event = handleCreate(createInventoryCommand);
        }

        if (command instanceof ReserveInventoryCommand reserveInventoryCommand) {
            event = handleReserve(reserveInventoryCommand);
        }

        if (command instanceof ReleaseInventoryCommand releaseInventoryCommand) {
            event = handleRelease(releaseInventoryCommand);
        }
        return event;
    }

    @Override
    public boolean handleEvent(BaseEvent event) {
        if (event instanceof InventoryCreatedEvent inventoryCreatedEvent) {
            this.productsId = inventoryCreatedEvent.getProductId();
            this.quantity = inventoryCreatedEvent.getQuantity();
        }
        if (event instanceof InventoryReservedEvent inventoryReservedEvent) {
            this.quantity -= inventoryReservedEvent.getQuantity();
        } else if (event instanceof InventoryReleasedEvent inventoryReleasedEvent) {
            this.quantity += inventoryReleasedEvent.getQuantity();
        }
        return true;
    }


    public String getProductsId() {
        return productsId;
    }

    public int getQuantity() {
        return quantity;
    }

    private ProductEvent handleRelease(ReleaseInventoryCommand releaseInventoryCommand) {
        InventoryReleasedEvent event = new InventoryReleasedEvent(
                releaseInventoryCommand.getOrderId(),
                releaseInventoryCommand.getProductId(),
                releaseInventoryCommand.getQuantity());
        return event;
    }

    private ProductEvent handleReserve(ReserveInventoryCommand reserveInventoryCommand) {
        if (canHold(reserveInventoryCommand.getQuantity())) {
            InventoryReservedEvent event = new InventoryReservedEvent(
                    reserveInventoryCommand.getOrderId(),
                    reserveInventoryCommand.getProductId(),
                    reserveInventoryCommand.getQuantity()
            );
            return event;
        }
        return null;
    }

    private ProductEvent handleCreate(CreateInventoryCommand createInventoryCommand) {
        InventoryCreatedEvent event = new InventoryCreatedEvent(
                createInventoryCommand.eventId,
                createInventoryCommand.getProductId(),
                createInventoryCommand.getQuantity()
        );
        return event;
    }

    private boolean canHold(int quantity) {
        return this.quantity >= quantity;
    }


    @Override
    public String toString() {
        return "Inventory{" +
                "productsId='" + productsId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
