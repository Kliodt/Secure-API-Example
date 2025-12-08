package ru.secureapiexample.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {

    @NotNull
    @Size(min = 4, max = 32)
    private String username;

    @NotNull
    @Size(min = 8, max = 64)
    private String password;
}
