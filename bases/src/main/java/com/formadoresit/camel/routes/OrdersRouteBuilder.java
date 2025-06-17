package com.formadoresit.camel.routes;

import com.formadoresit.camel.aggregators.TaxAggregator;
import com.formadoresit.camel.components.OrderComponent;
import com.formadoresit.camel.exceptions.MyBusinessException;
import com.formadoresit.camel.services.OrderService;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class OrdersRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        errorHandler(deadLetterChannel("direct:errorHandlerDLC"));

        onException(MyBusinessException.class)
                .maximumRedeliveries(3)
                .redeliveryDelay(3000)
                .handled(true)
                .to("direct:errorHandler");

        from("direct:errorHandler")
                .log("Manejando exception ${exception}");

        from("direct:errorHandlerDLC")
                .log("DLC Manejando exception ${exception}");

        from("timer:ordersTimer?period=5s")
                .routeId("orders-timer")
                .autoStartup(false)
                .log("==================================\nIniciando procesamiento")
                .bean(OrderComponent.class, "generateOrderList")
                .log("New Body: ${body}")
                .split(body()).aggregationStrategy(new TaxAggregator())
                    .to("direct:processOrder")
                .end()
                .log("After split Body: ${body}");

        from("direct:processOrder")
                .log("Procesando orden ${body.id}")
                .doTry()
                    .setHeader("TaxValue", simple("${properties:app.tax:1}"))
                    //.bean(OrderService.class, "calculateTax(${body} , {{app.tax}})")
                    .bean(OrderService.class, "calculateTax")
                    .throwException(new IllegalArgumentException("Algo falló"))
                    .log("Body result: ${body}")
                .doCatch(IllegalArgumentException.class)
                    .log(LoggingLevel.ERROR, "Exception in route")
                    .process(exchange -> {
                        Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                        log.info("In process: {}", exception.getMessage());
                    })
                .doCatch(IllegalAccessError.class)
                    .log(LoggingLevel.ERROR, "Exception in route")
                .doFinally()
                    .log("ejecutando do finally");
    }
}
