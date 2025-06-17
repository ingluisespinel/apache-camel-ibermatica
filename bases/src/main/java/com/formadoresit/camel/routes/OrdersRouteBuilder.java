package com.formadoresit.camel.routes;

import com.formadoresit.camel.aggregators.TaxAggregator;
import com.formadoresit.camel.components.OrderComponent;
import com.formadoresit.camel.services.OrderService;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class OrdersRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:ordersTimer?period=5s")
                .routeId("orders-timer")
                .log("==================================\nIniciando procesamiento")
                .bean(OrderComponent.class, "generateOrderList")
                .log("New Body: ${body}")
                .split(body()).aggregationStrategy(new TaxAggregator())
                    .to("direct:processOrder")
                .end()
                .log("After split Body: ${body}");

        from("direct:processOrder")
                .log("Procesando orden ${body.id}")
                .setHeader("TaxValue", simple("${properties:app.tax:1}"))
                //.bean(OrderService.class, "calculateTax(${body} , {{app.tax}})")
                .bean(OrderService.class, "calculateTax")
                .log("Body result: ${body}");
    }
}
