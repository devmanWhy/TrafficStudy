package com.devy.common.event.order.command;

import com.devy.common.event.BaseCommand;

public class OrderCommand extends BaseCommand {

    public OrderCommand(String eventId, String eventType) {
        super(eventId, eventType);
    }

    public OrderCommand() {
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
