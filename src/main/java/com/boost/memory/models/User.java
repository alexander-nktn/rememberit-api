package com.boost.memory.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;

    public String firstName;

    public String lastName;

    public String email;

    public User(
            String id,
            String firstName,
            String lastName,
            String email
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public User() {}
}
