package com.formadoresit.camel.components;

import com.formadoresit.camel.domain.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderComponent {

    public Order generateOrder(){
        var order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setOwner("Luis");
        order.setItems(List.of("Item1", "Item2", "Item3"));
        order.setAmount(1001d);
        order.setPaymentMethod("PAYPAL");
        return order;
    }

    public Order generateOrder(Double amount, String paymentMethod){
        var order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setOwner("Luis");
        order.setItems(List.of("Item1", "Item2", "Item3"));
        order.setAmount(amount);
        order.setPaymentMethod(paymentMethod);
        return order;
    }
}
