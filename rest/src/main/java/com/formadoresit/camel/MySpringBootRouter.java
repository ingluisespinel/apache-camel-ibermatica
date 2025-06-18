package com.formadoresit.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple Camel route that triggers from a timer and calls a bean and prints to system out.
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
@Component
public class MySpringBootRouter extends RouteBuilder {

    @Override
    public void configure() {
        /*
        restConfiguration()
                .port(8080)
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .contextPath("/api/camel");

        restConfiguration()
                .apiProperty("api.title", "Camel REST")
                .apiProperty("api.description", "API REST Users By Camel");

         */

        rest("/users")
                .get()
                    .produces("application/json")
                    .to("direct:getUsers")
                // "GET /api/camel/users/{userId}"
                .get("/{userId}")
                    .param()
                        .name("userId")
                        .type(RestParamType.path).description("User Id to find")
                    .endParam()
                    .to("direct:getUserById");

        from("direct:getUsers")
                .log("procesando get users")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .process(exchange -> {
                    Map<String, Object> body = new HashMap<>();
                    body.put("message", "ok");
                    exchange.getMessage().setBody(body);
                });

        from("direct:getUserById")
                .log("procesando get user by id");
    }

}
