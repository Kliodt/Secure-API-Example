package ru.secureapiexample;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.secureapiexample.dtos.LoginDTO;
import ru.secureapiexample.dtos.UserDTO;
import ru.secureapiexample.security.JWTService;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @PostMapping("auth/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody @Valid LoginDTO loginDto) {
        var user = userRepository.findUserByUsername(loginDto.getUsername());

        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getEncodedPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var accessToken = jwtService.createAccessToken(user.getId());
        return ResponseEntity.ok(Map.of(
                "token", accessToken,
                "user", new UserDTO(user)
        ));
    }


    @PostMapping("auth/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody @Valid LoginDTO loginDto) {

        var user = new User();
        user.setUsername(loginDto.getUsername());
        user.setEncodedPassword(passwordEncoder.encode(loginDto.getPassword()));

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) { // duplicated name
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        var accessToken = jwtService.createAccessToken(user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "token", accessToken,
                "user", new UserDTO(user)
        ));
    }


    @GetMapping("api/data")
    public List<UserDTO> getAllUsers() {
        return ((List<User>)userRepository.findAll()).stream().map(UserDTO::new).toList();
    }


    @PostMapping("api/update_user")
    public ResponseEntity<UserDTO> updateUser(
            @RequestBody @Valid UserDTO userDto,
            @AuthenticationPrincipal Long authUserId
    ) {
        if (authUserId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!authUserId.equals(userDto.getId())) { // only allow editing user's own data
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var user = userRepository.findById(userDto.getId()).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        // update
        user.setUsername(userDto.getUsername());
        user.setFavoriteQuote(userDto.getFavoriteQuote());

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) { // duplicated name
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.ok(new UserDTO(user));
    }
}
