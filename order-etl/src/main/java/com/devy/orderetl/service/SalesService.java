package com.devy.orderetl.service;

import com.devy.common.event.order.OrderEvent;
import com.devy.orderetl.domain.Sales;

public interface SalesService {
    public void updateSales(OrderEvent orderEvent);
    public Sales getSalesByDate(String date);
}
