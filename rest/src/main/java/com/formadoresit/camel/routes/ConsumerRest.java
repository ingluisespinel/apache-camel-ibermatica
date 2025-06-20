package com.formadoresit.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ConsumerRest extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        rest("/processors")
                .get().to("direct:getExternalData")
                .post().to("direct:proxy");

        from("direct:proxy")
                .log("procesando proxy con body ${body}")
                .setHeader(Exchange.HTTP_METHOD, simple("${body['method']}"))
                .setBody(simple("${body['payload']}"))
                .toD("${body['url']}?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal().json();

        from("direct:getExternalData")
                .log("Procesando external data")
                .setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
                .to("https://api.restful-api.dev/objects?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .log("body response ${body.class}") // <- InputStreamCache -> InputStreamCache NO PUEDE MAPEARSE
                .unmarshal().json(); // LinkedHashMap -> InputStreamCache
    }
}
