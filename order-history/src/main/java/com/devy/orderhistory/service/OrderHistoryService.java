package com.devy.orderhistory.service;

import com.devy.common.event.BaseEvent;

import java.util.List;

public interface OrderHistoryService {

    public void saveOrderHistory(String orderId, String eventType, String message);

    public List<Object> getOrderHistory(String orderId);
}
