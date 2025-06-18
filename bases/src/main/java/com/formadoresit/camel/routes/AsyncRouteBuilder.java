package com.formadoresit.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class AsyncRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        // Valores por default ??

        from("timer:asyncExample?period={{app.disparador.period}}")
                .log("iniciando timer")
                .setBody(constant("1,2,3,4,5,6,7,8,9,10,11,12,13"))
                .split(body().tokenize(","))
                    .parallelProcessing()
                    .to("direct:processSync")
                .end()
                .log("finalizado timer ${body}");

        from("direct:processSync")
                .log("Procesando body ${body}")
                .process(exchange -> {
                    Thread.sleep(3000);
                })
                .log("finalizado ${body}");

        from("seda:processItem?concurrentConsumers=20")
                // OJO Solo a fines demostrativos del load balance en camel
                .loadBalance().roundRobin()
                    .to("seda:processOne")
                    .to("seda:processTwo")
                .end();

        from("seda:processOne?concurrentConsumers=20")
                .log("Procesando body ${body}")
                .process(exchange -> {
                    Thread.sleep(3000);
                })
                .log("finalizado ${body}");

        from("seda:processTwo?concurrentConsumers=20")
                .log("Procesando body ${body}")
                .process(exchange -> {
                    Thread.sleep(3000);
                })
                .log("finalizado ${body}");

    }
}
