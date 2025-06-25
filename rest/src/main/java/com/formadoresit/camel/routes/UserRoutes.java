package com.formadoresit.camel.routes;

import com.formadoresit.camel.domain.User;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserRoutes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        interceptFrom("activemq:queue:newUsers")
                .log("Nuevo usuario generado")
                .to("micrometer:counter:newUsers");

        from("direct:getAllUsers")
                .to("sql:SELECT * FROM usuario?outputClass=com.formadoresit.camel.domain.User")
                .log("Body result class ${body[0].class}");

        from("direct:createUser")
                .routeId("create-user")
                .transacted()
                .to("direct:createUserJpa")
                .to("direct:notifyNewUser")
                .end();

        from("direct:createUserJpa")
                .routeId("insert-user-jpa")
                .to("jpa:com.formadoresit.camel.domain.User");

        from("direct:notifyNewUser")
                .routeId("notify-new-user")
                .log("Notificando nuevo usuario ${body.id}")
                .process(exchange -> {
                    var body = exchange.getMessage().getBody(User.class);
                    if(body.getId() % 2 == 0){
                        throw new RuntimeException("Id par no soportado");
                    }
                })
                .to("activemq:queue:newUsers?disableReplyTo=true");
        from("direct:getUserById")
                .log("Buscando user id ${header.userId}")
                .to("sql:classpath:queries/findById.sql?outputClass=com.formadoresit.camel.domain.User&outputType=SelectOne");



        from("activemq:queue:newUsers")
                .log("Consumiendo evento ${body.id} con tipo ${body.class}");


        from("direct:getUsersByAge")
                .log("Buscando usuario con edad ${header.age}")
                .process(exchange -> {
                    Map<String, Object> jpaParams = Map.of("age", exchange.getMessage().getHeader("age", Integer.class));
                    exchange.getMessage().setHeader("CamelJpaParameters", jpaParams);
                })
                .to("jpa:com.formadoresit.camel.domain.User?query=SELECT u FROM User u WHERE u.age = :age");
    }
}
