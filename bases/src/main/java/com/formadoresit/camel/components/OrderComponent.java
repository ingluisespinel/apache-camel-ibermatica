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

    public List<Order> generateOrderList(){
        var order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setOwner("Luis");
        order.setItems(List.of("Item1", "Item2", "Item3"));
        order.setAmount(1001d);
        order.setPaymentMethod("PAYPAL");

        var order2 = new Order();
        order2.setId(UUID.randomUUID().toString());
        order2.setOwner("Carlos");
        order2.setItems(List.of("Item1"));
        order2.setAmount(2000d);
        order2.setPaymentMethod("PAYU");
        return List.of(order, order2);
    }
}
