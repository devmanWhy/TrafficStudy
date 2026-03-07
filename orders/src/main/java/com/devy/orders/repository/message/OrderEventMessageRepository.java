package com.devy.orders.repository.message;

import com.devy.common.event.order.OrderEvent;

public interface OrderEventMessageRepository {

    public void publish(OrderEvent event);
}
