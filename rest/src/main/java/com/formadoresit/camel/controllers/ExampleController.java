package com.formadoresit.camel.controllers;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("examples")
public class ExampleController {
    private final ProducerTemplate producerTemplate;

    public ExampleController(ProducerTemplate producerTemplate){
        this.producerTemplate = producerTemplate;
    }

    @GetMapping
    public Map<String, Object> getUsers(){
        return producerTemplate.requestBody("direct:getUsers2", null, Map.class);
    }

}
