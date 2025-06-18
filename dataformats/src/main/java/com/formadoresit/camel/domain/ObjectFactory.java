package com.formadoresit.camel.domain;

import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    public ObjectFactory(){
    }

    public User createUser(){
        return new User();
    }
}