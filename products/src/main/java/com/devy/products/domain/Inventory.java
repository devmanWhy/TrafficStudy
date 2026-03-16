package com.devy.products.domain;

import com.devy.common.event.EventInfo;
import com.devy.common.event.product.InventoryCreatedEvent;
import com.devy.common.event.product.InventoryReleasedEvent;
import com.devy.common.event.product.InventoryReservedEvent;
import com.devy.common.event.product.ProductEvent;
import com.devy.common.event.product.command.CreateInventoryCommand;
import com.devy.common.event.product.command.ProductCommand;
import com.devy.common.event.product.command.ReleaseInventoryCommand;
import com.devy.common.event.product.command.ReserveInventoryCommand;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private String productsId;
    private int quantity;
    private List<ProductEvent> unCommitedEvents = new ArrayList<>();
    private int currentSequence = 0;

    public Inventory() {
    }

    public Inventory(String productsId, int quantity) {
        this.productsId = productsId;
        this.quantity = quantity;
        this.unCommitedEvents = new ArrayList<>();
    }

    public void applyCommand(ProductCommand command) {
        if (command.getEventType().equals(EventInfo.PRODUCTS.CREATE_INVENTORY_COMMAND_CLASS.getName())) {
            CreateInventoryCommand createInventoryCommand = (CreateInventoryCommand) command;
            handleCreate(createInventoryCommand);
        }

        if (command.getEventType().equals(EventInfo.PRODUCTS.RESERVE_INVENTORY_COMMAND_CLASS.getName())) {
            ReserveInventoryCommand reserveInventoryCommand = (ReserveInventoryCommand) command;
            handleReserve(reserveInventoryCommand);
        }

        if (command.getEventType().equals(EventInfo.PRODUCTS.RELEASE_INVENTORY_COMMAND_CLASS.getName())) {
            ReleaseInventoryCommand releaseInventoryCommand = (ReleaseInventoryCommand) command;
            handleRelease(releaseInventoryCommand);
        }
    }

    public void applyEvents(List<ProductEvent> events) {
        events.forEach(this::applyEvent);
    }

    public void applyEvent(ProductEvent event) {
        if (event.getEventType().equals(EventInfo.PRODUCTS.INVENTORY_CREATED_EVENT_CLASS.getName())) {
            InventoryCreatedEvent inventoryCreatedEvent = (InventoryCreatedEvent) event;
            this.productsId = inventoryCreatedEvent.getProductId();
            this.quantity = inventoryCreatedEvent.getQuantity();
        }
        if (event.getEventType().equals(EventInfo.PRODUCTS.INVENTORY_RESERVED_EVENT_CLASS.getName())) {
            InventoryReservedEvent inventoryReservedEvent = (InventoryReservedEvent) event;
            this.quantity -= inventoryReservedEvent.getQuantity();
        } else if (event.getEventType().equals(EventInfo.PRODUCTS.INVENTORY_RELEASED_EVENT_CLASS.getName())) {
            InventoryReleasedEvent inventoryReleasedEvent = (InventoryReleasedEvent) event;
            this.quantity += inventoryReleasedEvent.getQuantity();
        }
        this.currentSequence++;
    }

    private void handleRelease(ReleaseInventoryCommand releaseInventoryCommand) {
        InventoryReleasedEvent event = new InventoryReleasedEvent(
                releaseInventoryCommand.getOrderId(),
                releaseInventoryCommand.getProductId(),
                releaseInventoryCommand.getQuantity());
        applyEvent(event);
        this.unCommitedEvents.add(event);
    }

    private void handleReserve(ReserveInventoryCommand reserveInventoryCommand) {
        if (canHold(reserveInventoryCommand.getQuantity())) {
            InventoryReservedEvent event = new InventoryReservedEvent(
                    reserveInventoryCommand.getOrderId(),
                    reserveInventoryCommand.getProductId(),
                    reserveInventoryCommand.getQuantity()
            );
            applyEvent(event);
            this.unCommitedEvents.add(event);
        }
    }

    private boolean canHold(int quantity) {
        return this.quantity >= quantity;
    }

    private void handleCreate(CreateInventoryCommand createInventoryCommand) {
        InventoryCreatedEvent event = new InventoryCreatedEvent(
                createInventoryCommand.eventId,
                createInventoryCommand.getProductId(),
                createInventoryCommand.getQuantity()
        );
        applyEvent(event);
        this.unCommitedEvents.add(event);
    }

    public void commit() {
        this.unCommitedEvents.clear();
    }

    public String getProductsId() {
        return productsId;
    }

    public int getQuantity() {
        return quantity;
    }

    public List<ProductEvent> getUnCommitedEvents() {
        return unCommitedEvents;
    }

    public int getCurrentSequence() {
        return currentSequence;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "productsId='" + productsId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
