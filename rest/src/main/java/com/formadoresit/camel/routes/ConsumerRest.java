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

        from("direct:jsonExample")
                .setBody(constant("{\n" +
                        "    \"id\": 100,\n" +
                        "    \"name\": \"Otro user\",\n" +
                        "    \"age\": 40\n" +
                        "}"))
                .choice()
                    .when(jsonpath("age == 40"))
                        .log("edad 40")
                .otherwise()
                    .log("no es 40");

        from("direct:proxy")
                .log("procesando proxy con body ${body}")
                .wireTap("direct:jsonExample")
                .setHeader("UrlCopy", simple("${body['url']}"))
                .setHeader(Exchange.HTTP_METHOD, simple("${body['method']}"))
                .setBody(simple("${body['payload']}"))
                .marshal().json()
                .toD("${header.UrlCopy}?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal().json()
                .to("direct:saveDataMongo");

        from("direct:getExternalData")
                .log("Procesando external data")
                .setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
                .to("https://api.restful-api.dev/objects?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .log("body response ${body.class}") // InputStreamCache -> InputStreamCache NO PUEDE MAPEARSE
                .unmarshal().json(); // LinkedHashMap -> InputStreamCache

        from("direct:saveDataMongo")
                .to("mongodb:mongodb?database=test&collection=data_proxy&operation=insert")
                .log("header after save in mongo ${headers}");
    }
}
