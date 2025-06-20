package com.formadoresit.camel.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class User {
    private int id;
    @NotBlank(message = "name requerido")
    private String name;
    @Min(value = 18, message = "age debe ser por lo menos 18")
    private int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
