package org.juego.siete.islas.auth;

import java.time.LocalDate;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.juego.siete.islas.auth.payload.request.SignupRequest;
import org.juego.siete.islas.player.Player;
import org.juego.siete.islas.player.PlayerService;
import org.juego.siete.islas.user.Authorities;
import org.juego.siete.islas.user.AuthoritiesService;
import org.juego.siete.islas.user.User;
import org.juego.siete.islas.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final PasswordEncoder encoder;
    private final AuthoritiesService authoritiesService;
    private final UserService userService;
    private final PlayerService playerService;

    @Autowired
    public AuthService(PasswordEncoder encoder, AuthoritiesService authoritiesService, UserService userService,
            PlayerService playerService) {
        this.encoder = encoder;
        this.authoritiesService = authoritiesService;
        this.userService = userService;
        this.playerService = playerService;
    }

    @Transactional
    public void createUser(@Valid SignupRequest request) {
        Authorities role;
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        role = authoritiesService.findByAuthority("PLAYER");
        user.setAuthority(role);
        userService.saveUser(user);
        Player player = new Player();
        player.setFirstName(request.getFirstName());
        player.setLastName(request.getLastName());
        player.setRegistrationDate(LocalDate.now());
        player.setBirthdayDate(request.getBirthdayDate());
        player.setEmail(request.getEmail());
        player.setImage("Estandar");
        player.setUser(user);
        playerService.savePlayer(player,false);
    }
}
