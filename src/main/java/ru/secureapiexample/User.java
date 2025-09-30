package ru.secureapiexample;


import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String username;

    private String encodedPassword;

    private String favoriteQuote;
}
