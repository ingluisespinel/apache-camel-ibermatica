package com.formadoresit.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RestConsumerRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:restCOnsumer?period=5s")
                .to("direct:restConsumer");

        from("direct:restConsumer")
                .to("https://pokeapi.co/api/v2/ability")
                // JSON -> Object
                .log("Body response ${body}")
                .unmarshal().json()
                .log("unmarshalled body response ${body.class}");

    }
}
