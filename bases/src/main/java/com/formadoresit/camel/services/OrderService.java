package com.formadoresit.camel.services;

import com.formadoresit.camel.domain.Order;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    public Double calculateTax(@Body Order order,@Header("TaxValue") float tax){
        return order.getAmount() * tax;
    }
}
