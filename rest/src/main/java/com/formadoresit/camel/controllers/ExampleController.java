package com.formadoresit.camel.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

//@RestController
//@RequestMapping("examples")
public class ExampleController {

    @GetMapping
    public Map<String, Object> getUsers(){
        return Map.of("message", "ok");
    }

}
