package com.formadoresit.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserRoutes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:getAllUsers")
                .to("sql:SELECT * FROM usuario?outputClass=com.formadoresit.camel.domain.User")
                .log("Body result class ${body[0].class}");

        from("direct:getUserById")
                .log("Buscando user id ${header.userId}")
                .to("sql:classpath:queries/findById.sql?outputClass=com.formadoresit.camel.domain.User&outputType=SelectOne");
        
        from("direct:createUserJpa")
                .to("jpa:com.formadoresit.camel.domain.User");

        from("direct:getUsersByAge")
                .log("Buscando usuario con edad ${header.age}")
                .process(exchange -> {
                    Map<String, Object> jpaParams = Map.of("age", exchange.getMessage().getHeader("age", Integer.class));
                    exchange.getMessage().setHeader("CamelJpaParameters", jpaParams);
                })
                .to("jpa:com.formadoresit.camel.domain.User?query=SELECT u FROM User u WHERE u.age = :age");
    }
}
