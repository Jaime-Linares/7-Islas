package org.juego.siete.islas.stat;

import java.util.List;

import org.juego.siete.islas.player.Player;
import org.juego.siete.islas.user.UserService;
import org.juego.siete.islas.util.RestPreconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/v1/stats")
@Tag(name = "Stats", description = "API for the management of stats")
@SecurityRequirement(name = "bearerAuth")
public class StatRestController {

    private final StatService statService;
    private final UserService userService;

    @Autowired
    public StatRestController(StatService statService, UserService userService) {
        this.statService = statService;
        this.userService = userService;
    }

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<Stat> findStatsByUserId(@PathVariable("userId") Integer userId) {
        RestPreconditions.checkNotNull(userService.findUserById(userId),
                "User", "ID", userId);
        return new ResponseEntity<>(statService.findStatsByUserId(userId), HttpStatus.OK);
    }

    @GetMapping(value = "/ranking/{atributo}")
    public ResponseEntity<List<Player>> findRankingByAtributte(@PathVariable("atributo") String atributo) {
        return new ResponseEntity<>(statService.findRankingByAtributte(atributo), HttpStatus.OK);
    }

}
