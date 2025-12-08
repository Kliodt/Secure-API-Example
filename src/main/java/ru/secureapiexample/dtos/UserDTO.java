package ru.secureapiexample.dtos;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.secureapiexample.User;
import ru.secureapiexample.security.EscapeJsonSerializer;

@Data
@NoArgsConstructor
public class UserDTO {

    @NotNull
    private Long id;

    @NotNull
    @Size(min = 4, max = 32)
    @JsonSerialize(using = EscapeJsonSerializer.class)
    private String username;

    @JsonSerialize(using = EscapeJsonSerializer.class)
    private String favoriteQuote;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.favoriteQuote = user.getFavoriteQuote();
    }
}
