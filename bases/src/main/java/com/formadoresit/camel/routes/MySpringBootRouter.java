package com.formadoresit.camel.routes;

import com.formadoresit.camel.components.OrderComponent;
import com.formadoresit.camel.domain.Order;
import com.formadoresit.camel.processors.MyProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class MySpringBootRouter extends RouteBuilder {
    private final MyProcessor myProcessor;

    public MySpringBootRouter(MyProcessor myProcessor){
        this.myProcessor = myProcessor;
    }

    @Override
    public void configure() {
        from("timer:disparador?period={{app.disparador.period}}")
                .routeId("disparador")
                // Ejemplo de uso de setHeader para establecer Header
                .setHeader("MyHeader", constant("Header value"))
                .setHeader("PaymentMethod", constant("PAYPAL"))
                // Ejemplo para establecer body vía setBody
                .setBody(constant("Hola Mundo !"))
                // Ejemplo de llamado a método a través del uso de bean
                //.bean("orderComponent", "generateOrder()")
                .bean(OrderComponent.class, "generateOrder(1001, ${header.PaymentMethod})")
                .to("direct:routeA")
                //.process(myProcessor)
                .to("direct:routeB");

        from("direct:creationOrderViaProcessor")
                // Ejemplo de uso de processors para acceso al objeto Exchange //
                .process(exchange -> {
                    var order = new Order();
                    order.setId(UUID.randomUUID().toString());
                    order.setOwner("Luis");
                    order.setItems(List.of("Item1", "Item2", "Item3"));
                    order.setAmount(1001d);
                    order.setPaymentMethod("PAYPAL");
                    exchange.getMessage().setBody(order);
                });

        from("direct:routeA")
                .routeId("route-a")
                .log("Ejecutando ruta A, body class ${body.class}")
                .log("order id: ${body.id} y owner ${body.owner}, item cero ${body.items[0]}")
                .split(simple("${body.items}"))
                    .log("Procesando item ${body}")
                .end()
                .log("Finalizado el procesamiento de items,Body final ${body}");

        from("direct:routeB")
                .routeId("route-b")
                .log("Ejecutando ruta B con body ${body}, y headers ${headers}")
                .choice()
                    .when(simple("${body.amount} > 1000"))
                        .log("procesando mayor a mil ")
                        .to("direct:procesarMayorA100")
                    .when(simple("${body.amount} > 500"))
                        .log("Procesando mayor a 500")
                    .otherwise()
                        .log("procesando menor igual 500 ")
                .end();

        from("direct:procesarMayorA100")
                .log("procesando mayor a 1000")
                .filter(simple("${body.paymentMethod} == 'PAYPAL'"))
                    .log("Order filtrada por PAYPAL")
                    .stop()
                .end()
                .log("Finalizado el filtrado. body ${body}");
    }

}
