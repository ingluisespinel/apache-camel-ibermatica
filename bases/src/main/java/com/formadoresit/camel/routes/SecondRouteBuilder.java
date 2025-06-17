package com.formadoresit.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SecondRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:secondTimer?period=5s")
                .autoStartup(false)
                .log("Procesando second timer")
                .setBody(constant("A,B,C,D"))
                .split(body().tokenize(","))
                    .to("direct:processLetter");

        from("direct:processLetter")
                .log("Body ${body}")
                .multicast()
                    .to("direct:route1")
                    .to("direct:route2")
                .end()
                .log("Finalizado con body ${body}");

        from("direct:route1")
                .routeId("route-1")
                .log("Ejecutando ruta 1");

        from("direct:route2")
                .routeId("route-2")
                .log("Ejecutando ruta 2");
    }
}
