package org.juego.siete.islas.player;

import java.net.URISyntaxException;
import java.util.List;

import org.juego.siete.islas.auth.payload.response.MessageResponse;
import org.juego.siete.islas.card.Card;
import org.juego.siete.islas.user.User;
import org.juego.siete.islas.user.UserService;
import org.juego.siete.islas.util.RestPreconditions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/players")
@Tag(name="Players",description = "API for the management of players")
@SecurityRequirement(name = "bearerAuth")
public class PlayerRestController {

    private final PlayerService playerService;
    private final UserService userService;

    @Autowired
    public PlayerRestController(PlayerService playerService, UserService userService){
        this.playerService = playerService;
        this.userService = userService;
    }


    @GetMapping
	public ResponseEntity<List<Player>> findAll() {
		return new ResponseEntity<>((List<Player>) playerService.findAll(), HttpStatus.OK);
	}

    @GetMapping(value = "{playerId}")
	public ResponseEntity<Player> findById(@PathVariable("playerId") int id) {
		return new ResponseEntity<>(playerService.findPlayerById(id), HttpStatus.OK);
	}

    @GetMapping("user/{userId}")
    public ResponseEntity<Player> findByUser(@PathVariable("userId") int userId) {
        return new ResponseEntity<>(playerService.findPlayerByUser(userId).get(), HttpStatus.OK);
    }

	@GetMapping(value = "{playerId}/cards")
	public ResponseEntity<List<Card>> findCardsByPlayerId(@PathVariable("playerId") int playerId) {
		return new ResponseEntity<>(playerService.findCardsByPlayerId(playerId), HttpStatus.OK);
	}

    @PostMapping()
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Player> create(@RequestBody @Valid Player player) throws URISyntaxException {
		Player newPlayer = new Player();
		BeanUtils.copyProperties(player, newPlayer, "id");
		newPlayer.setImage("Estandar");
		User user = userService.findCurrentUser();
		newPlayer.setUser(user);
		Player savedPlayer = this.playerService.savePlayer(newPlayer,false);

		return new ResponseEntity<>(savedPlayer, HttpStatus.CREATED);
	}

    @PutMapping(value = "{playerId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> update(@PathVariable("playerId") int playerId, @RequestBody @Valid PlayerEditDTO player) {
		Player repPlayer = playerService.findPlayerByUsername(player.getUsername()).orElse(null);
        if (repPlayer != null) {
            if (userService.existsUserByUsername(player.getUsername()).equals(true) && repPlayer.getId() != playerId) {
                return ResponseEntity.badRequest().body(new MessageResponse("Este nombre de usuario ya ha sido escogido, prueba con otro."));
            }
        }
		RestPreconditions.checkNotNull(playerService.findPlayerById(playerId), "Player", "ID", playerId);
		playerService.updatePlayer(player, playerId);
		return ResponseEntity.ok(new MessageResponse("Player edited successfully!"));
	}

	@PutMapping(value = "{userId}/connection")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Player> updateConnection(@PathVariable("userId") int userId) {
		RestPreconditions.checkNotNull(playerService.findPlayerByUser(userId).get(), "Player", "ID", userId);
		return new ResponseEntity<>(this.playerService.updatePlayerConnection(playerService.findPlayerByUser(userId).get().getId()), HttpStatus.OK);
	}

    @DeleteMapping(value = "{playerId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<MessageResponse> delete(@PathVariable("playerId") int id) {
		RestPreconditions.checkNotNull(playerService.findPlayerById(id), "Player", "ID", id);
		playerService.deletePlayer(id);
		return new ResponseEntity<>(new MessageResponse("Player deleted!"), HttpStatus.OK);
	}

}
