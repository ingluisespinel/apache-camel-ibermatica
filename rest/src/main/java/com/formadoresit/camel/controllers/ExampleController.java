package com.formadoresit.camel.controllers;

import com.formadoresit.camel.domain.User;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/spring-boot/examples")
public class ExampleController {
    @Autowired
    private ProducerTemplate producerTemplate;

    @GetMapping
    public List<User> getUsers(){
        return producerTemplate.requestBody("direct:getUsers", null, List.class);
    }

}
