package com.formadoresit.camel.routes;

import com.formadoresit.camel.dtos.OrderDto;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * A simple Camel route that triggers from a timer and calls a bean and prints to system out.
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
@Component
public class MySpringBootRouter extends RouteBuilder {

    @Override
    public void configure() {
        from("timer:hello?period={{timer.period}}")
                .routeId("hello")
                .autoStartup(false)
                .log("Iniciando procesamiento")
                .process(exchange -> {
                    var order = new OrderDto();
                    order.setAmount(1000d);
                    order.setId("1");
                    exchange.getMessage().setBody(order);
                })
                .to("direct:processBodyToJson")
                .to("direct:processBodyToObject");;

        from("direct:processBodyToJson")
                .routeId("process-body-to-json")
                .log("procesando body de tipo ${body.class}")
                .marshal().json()
                .log("post body de tipo ${body.class} con value ${body}");

        from("direct:processBodyToObject")
                .routeId("process-body-to-object")
                .log("procesando body de tipo ${body.class}")
                .unmarshal().json(OrderDto.class)
                .log("post body de tipo ${body.class} con value ${body}");

    }

}
