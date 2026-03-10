package com.devy.products.repository.message;

import com.devy.common.event.product.ProductEvent;

public interface ProductEventMessageRepository {

    public void publish(ProductEvent event);
}
