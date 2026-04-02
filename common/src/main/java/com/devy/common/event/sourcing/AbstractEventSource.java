package com.devy.common.event.sourcing;

import com.devy.common.event.BaseCommand;
import com.devy.common.event.BaseEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEventSource {
    private int currentSequence;
    private List<BaseEvent> unCommittedEvents = new ArrayList<>();

    public void applyCommand(BaseCommand command) {
        // Handle Command
        BaseEvent baseEvent = handleCommand(command);
        // Apply Event
        applyEvent(baseEvent);
        // Add unCommited Event
        unCommittedEvents.add(baseEvent);
    }

    public void applyEvents(List<BaseEvent> events) {
        events.forEach(this::applyEvent);
    }

    public void applyEvent(BaseEvent event) {
        if (handleEvent(event)) {
            currentSequence++;
        }
    }

    public abstract BaseEvent handleCommand(BaseCommand command);

    public abstract boolean handleEvent(BaseEvent event);

    public void commit() {
        unCommittedEvents.clear();
    }

    public int getCurrentSequence() {
        return currentSequence;
    }

    public List<BaseEvent> getUnCommittedEvents() {
        return unCommittedEvents;
    }
}
