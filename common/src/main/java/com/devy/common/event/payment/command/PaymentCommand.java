package com.devy.common.event.payment.command;

import com.devy.common.event.BaseCommand;

public class PaymentCommand extends BaseCommand {

    public PaymentCommand(String eventId, String eventType) {
        super(eventId, eventType);
    }

    public PaymentCommand() {
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
