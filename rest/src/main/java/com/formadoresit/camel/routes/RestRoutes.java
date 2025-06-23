package com.formadoresit.camel.routes;

import com.formadoresit.camel.domain.User;
import com.formadoresit.camel.domain.UserRepo;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
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
public class RestRoutes extends RouteBuilder {

    @Override
    public void configure() {
        onException(BeanValidationException.class)
                .handled(true)
                .to("direct:dataErrorHandler");

        restConfiguration()
                .port(8080)
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .contextPath("/api/camel")
                .clientRequestValidation(true)
                .apiContextPath("/api-docs");

        rest("/users")
                .get()
                    .produces("application/json")
                    .to("direct:getUsersController")
                // "GET /api/camel/users/{userId}"
                .get("/{userId}")
                    .param().name("userId").required(true).type(RestParamType.path).endParam()
                    .to("direct:getUserByIdController")
                .get("/by-age")
                    .param().name("age").required(true).type(RestParamType.query).endParam()
                    .to("direct:getUsersByAgeController")
                .post()
                    .description("Create User")
                    .type(User.class)
                    .outType(User.class)
                    .to("direct:createUserController");

        rest("/admin")
                .get("/roles").to("direct:getRoles");

        from("direct:getRoles")
                .process(exchange -> exchange.getMessage().setBody(Map.of("rol1", "admin")));

        from("direct:getUsersController")
                .to("direct:getAllUsers");

        from("direct:getUsersByAgeController")
                .to("direct:getUsersByAge");

        from("direct:getUserByIdController")
                .to("direct:getUserById")
                .filter((body().isNull()))
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .end();

        from("direct:createUserController")
                .to("bean-validator:validateUser")
                .to("direct:createUserJpa")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201));

        from("direct:dataErrorHandler")
                .log("Manejando")
                .process(exchange -> {
                    var exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                    exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
                    exchange.getMessage().setBody(Map.of("error", exception.getMessage()));
                });
    }

}
