package org.juego.siete.islas.game;

import java.util.List;

import org.juego.siete.islas.auth.payload.response.MessageResponse;
import org.juego.siete.islas.util.RestPreconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("api/v1/games")
@Tag(name="Games",description = "API for the management of games")
@SecurityRequirement(name="bearerAuth")
public class GameRestController {

    private GameService gameService;

    @Autowired
    public GameRestController(GameService gameService) {
        this.gameService= gameService;
    }


    @GetMapping
    public ResponseEntity<List<Game>> findAllGames() {
        List<Game> res= gameService.findAll();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value = "/status/{status}")
    public ResponseEntity<List<Game>> findAllGamesByStatus(@PathVariable("status") GameStatus status) {
        List<Game> res= gameService.findGamesByStatus(status);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value = "/reconnectLobby/{userId}")
    public ResponseEntity<Game> findGamesUnstartedByUserId(@PathVariable("userId") Integer userId) {
        Game game= gameService.findGamesUnstartedByUserId(userId).orElse(new Game());
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @GetMapping(value = "/reconnectGame/{userId}")
    public ResponseEntity<Game> findGamesUnfinishedByUserId(@PathVariable("userId") Integer userId) {
        Game game= gameService.findGamesUnfinishedByUserId(userId).orElse(new Game());
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @GetMapping(value = "/{gameId}")
    public ResponseEntity<Game> findGameById(@PathVariable("gameId") Integer gameId) {
        return new ResponseEntity<>(gameService.findGameById(gameId), HttpStatus.OK);
    }

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<List<Game>> findGamesByUserId(@PathVariable("userId") Integer userId) {
        return new ResponseEntity<>(gameService.findGamesByUserId(userId), HttpStatus.OK);
    }

    @GetMapping(value = "/userMostRecent")
    public ResponseEntity<Game> findRecentGameByUserId() {
        return new ResponseEntity<>(gameService.findRecentGameByPlayerId(), HttpStatus.OK);
    }

    @GetMapping(value = "/userMostRecent/{userId}")
    public ResponseEntity<Integer> findRecentGameByUserIdForInvitations(@PathVariable("userId") Integer userId) {
        return new ResponseEntity<>(gameService.findRecentGameByPlayerIdForInvitations(userId), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Game> create() {
        return new ResponseEntity<>(gameService.saveGame(), HttpStatus.CREATED);
    }

    @PutMapping(value = "/code/{codigo}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> updatePlayerByCode(@PathVariable("codigo") String codigo) {
        return new ResponseEntity<>(gameService.invitationByCode(codigo), HttpStatus.OK);
    }

    @PutMapping(value = "/game/{gameId}/exit/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> updateExitPlayer(@PathVariable("gameId") Integer gameId, @PathVariable("userId") Integer userId) {
        return new ResponseEntity<>(gameService.exitUserById(gameId, userId), HttpStatus.OK);
    }

    @PutMapping(value = "/start/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> updateStartGame(@PathVariable("gameId") Integer gameId) {
        return new ResponseEntity<Game>(gameService.startPlayGameById(gameId), HttpStatus.OK);
    }

    @PutMapping(value = "/{gameId}/turn")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> updateTurnByGameId(@PathVariable("gameId") Integer gameId) {
        return new ResponseEntity<>(gameService.updateTurnByGameId(gameId), HttpStatus.OK);
    }

    @PutMapping(value = "/finish/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> updateFinishGame(@PathVariable("gameId") Integer gameId) throws Exception {
        return new ResponseEntity<>(gameService.finishGame(gameId), HttpStatus.OK);
    }

    @PutMapping(value = "/dependencies/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> updateGameFinished(@PathVariable("gameId") Integer gameId) {
        RestPreconditions.checkNotNull(gameService.findGameById(gameId), "Game", "ID", gameId);
        gameService.updateGameFinishedDependencies(gameId);
        return new ResponseEntity<>(new MessageResponse("Game finished!"), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> delete(@PathVariable("gameId") Integer gameId) {
        RestPreconditions.checkNotNull(gameService.findGameById(gameId), "Game", "ID", gameId);
		gameService.deleteGame(gameId);
		return new ResponseEntity<>(new MessageResponse("Game deleted!"), HttpStatus.OK);
    }

}
