package com.devy.common.event.product.command;

import com.devy.common.event.BaseCommand;

public class ProductCommand extends BaseCommand {

    public ProductCommand(String eventId, String eventType) {
        super(eventId, eventType);
    }

    public ProductCommand() {
        super();
    }

    @Override
    public String toString() {
        return "PaymentCommand{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
