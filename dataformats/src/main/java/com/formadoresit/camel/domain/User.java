package com.formadoresit.camel.domain;

import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {
    @XmlElement(name = "id")
    private int id;
    @XmlElement(name = "name")
    private String name;
    @XmlElementWrapper(name = "roles") // Indica que es una lista dentro de un nodo
    @XmlElement(name = "rol")  // Nombre de cada elemento dentro de la lista
    private List<Rol> roles;

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

    public List<Rol> getRoles() {
        return roles;
    }

    public void setRoles(List<Rol> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', roles=" + roles + "}";

    }

}