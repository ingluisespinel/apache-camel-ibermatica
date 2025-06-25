package com.formadoresit.camel.routes;

import com.formadoresit.camel.domain.User;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.apache.camel.saga.InMemorySagaService;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class ConsumerRest extends RouteBuilder {

    public ConsumerRest(CamelContext camelContext) throws Exception {
        camelContext.addService(new InMemorySagaService());
    }

    @Override
    public void configure() throws Exception {
        rest("/processors")
                .get().to("direct:getExternalData")
                .post().to("direct:proxyCircuitBreaker")
                .post("/saga").to("direct:sagaRoute");

        from("direct:sagaRoute")
                .saga()
                .propagation(SagaPropagation.REQUIRED)
                .completion("direct:markAsProcessed")
                .compensation("direct:cancelProcess")
                    .to("direct:processA")
                    .to("direct:processB");

        from("direct:processA")
                .log("procesando A");

        from("direct:processB")
                .log("procesando B")
                .throwException(new RuntimeException("Error"));

        from("direct:cancelProcess")
                .log("cancelando");

        from("direct:markAsProcessed")
                .log("procesado ok");

        // Ejemplo básico del uso de JSON Path
        from("direct:jsonExample")
                .routeId("example-json-path")
                .setBody(constant("{\"id\": 100,\"name\": \"Otro user\",\"age\": 40, \"roles\": [1,2,3]}"))
                // Ejemplo choice con predicados generados con jsonpath
                .choice()
                    .when(jsonpath("$[?(@.age < 40)]"))
                        .log("edad menor a 40")
                    .otherwise()
                        .log("Edad mayor o igual a 40")
                .end()
                // Ejemplo Split con JSON Path
                .split().jsonpath("$.roles[*]")
                        .log("Split with jsonpath, body: ${body} & class ${body.class}")
                .end()
                .to("direct:xmlExample");

        // Ejemplo básico de XPATH para acceso directo a contenido XML
        from("direct:xmlExample")
                .routeId("example-xpath")
                .setBody(constant("<persona><id>100</id><nombre>Otro user</nombre><edad>30</edad></persona>"))
                .choice()
                    .when(xpath("/persona/edad < 40"))
                        .log("edad menor 40")
                    .otherwise()
                        .log("Edad mayor o igual a 40")
                .end();

        from("direct:proxyCircuitBreaker")
                .circuitBreaker()
                    .resilience4jConfiguration()
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(30)
                        .slidingWindowSize(3)
                        .timeoutEnabled(true).timeoutDuration(1000)
                    .end()
                    .to("direct:proxy")
                .onFallback()
                    .to("direct:onFallbackProxy")
                .end();

        from("direct:onFallbackProxy")
                .log("On Fallback")
                .setBody(constant(Map.of("message", "FALLBACK")));

        from("direct:proxy")
                .log("procesando proxy con body ${body}")
                .wireTap("direct:jsonExample")
                .setHeader("UrlCopy", simple("${body['url']}"))
                .setHeader(Exchange.HTTP_METHOD, simple("${body['method']}"))
                .setBody(simple("${body['payload']}"))
                .marshal().json()
                .toD("${header.UrlCopy}?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .unmarshal().json()
                .to("direct:saveDataMongo")
                .delay(simple("${random(300, 1200)}"));

        from("direct:getExternalData")
                .log("Procesando external data")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .to("https://api.restful-api.dev/objects?bridgeEndpoint=true&throwExceptionOnFailure=false")
                .log("body response ${body.class}") // InputStreamCache -> InputStreamCache NO PUEDE MAPEARSE
                .unmarshal().json(); // LinkedHashMap -> InputStreamCache

        from("direct:saveDataMongo")
                .to("mongodb:mongodb?database=test&collection=data_proxy&operation=insert")
                .log("header after save in mongo ${headers}");
    }
}
