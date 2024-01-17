package org.juego.siete.islas.game;

import org.juego.siete.islas.card.CardService;
import org.juego.siete.islas.game.Chat.ChatRepository;
import org.juego.siete.islas.island.IslandService;
import org.juego.siete.islas.player.Player;
import org.juego.siete.islas.player.PlayerService;
import org.juego.siete.islas.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GameServiceTest {
    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private CardService cardService;

    @Autowired
    private IslandService islandService;

    @Autowired
    private ChatRepository chatRepository;

    @Test
    void shouldFindAll() {
        List<Game> games = this.gameService.findAll();
        assertEquals(4, games.size());
    }

    @Test
    void shouldFindGamesByStatus() {
        List<Game> games = this.gameService.findGamesByStatus(GameStatus.FINISHED);
        assertEquals(4, games.size());
    }

    @Test
    void shouldFindGameById() {
        Game game = this.gameService.findGameById(1);
        assertEquals(String.class, game.getCode().getClass());
        assertEquals(4, game.getCode().length());
    }

    @Test
    void shouldFindGamesByUserId() {
        List<Game> games = this.gameService.findGamesByUserId(2);

        assertEquals(4, games.size());
    }

    @Test
    @WithMockUser(username = "player1", password = "0wn3r")
    void shouldFindRecentGameByPlayerId() {
        assertEquals(Game.class, gameService.findRecentGameByPlayerId().getClass());
    }

    @Test
    void shouldFindRecentGameByPlayerIdForInvitations() {
        assertTrue(userService.findUserByUsername("player1").isPresent());
        assertEquals(Integer.class, gameService.findRecentGameByPlayerIdForInvitations(
            userService.findUserByUsername("player1").get().getId()
        ).getClass());
    }

    @Test
    @WithMockUser(username = "player3", password = "0wn3r")
    void findGamesUnstartedByUserId() {
        gameService.saveGame();
        assertEquals(ArrayList.class, gameService.findGamesByUserIdNotStarted(4).getClass());
        assertEquals(1, gameService.findGamesByUserIdNotStarted(4).size());
    }

    @Test
    @WithMockUser(username = "player5", password = "0wn3r")
    void findGamesUnfinishedByUserId() {
        List<Player> jugadores = (List<Player>) playerService.findAll();
        Player jugador1 = jugadores.get(1);
        Player jugador2 = jugadores.get(2);
        Player jugador3 = jugadores.get(3);
        List<Player> newPlayers = List.of(jugador1, jugador2, jugador3);

        Game partidaLista = gameService.saveGame();
        List<Player> players = partidaLista.getPlayers();
        players.addAll(newPlayers);
        partidaLista.setPlayers(players);
        gameService.updateGameByGameId(partidaLista.getId(), partidaLista);
        gameService.startPlayGameById(partidaLista.getId());

        assertTrue(gameService.findGamesUnfinishedByUserId(jugador1.getUser().getId()).isPresent());
        assertEquals(100, gameService.findGamesUnfinishedByUserId(jugador1.getUser().getId()).get().getId());
    }

    @Test
    @WithMockUser(username = "player2", password = "0wn3r")
    void findRecentGameByPlayerId() {
        assertEquals(4, gameService.findRecentGameByPlayerId().getId());
    }

    @Test
    void findRecentGameByPlayerIdForInvitations() {
        assertEquals(4, gameService.findRecentGameByPlayerIdForInvitations(3));
    }

    @Test
    @WithMockUser(username = "player1", password = "0wn3r")
    void shouldSaveGame() {
        assertTrue(userService.findUserByUsername("player1").isPresent());
        List<Game> gamesBeforeCreate = gameService.findGamesByUserId(userService.findUserByUsername("player1").get().getId());
        gameService.saveGame();
        List<Game> gamesAfterCreate = gameService.findGamesByUserId(userService.findUserByUsername("player1").get().getId());

        assertEquals(gamesBeforeCreate.size()+1, gamesAfterCreate.size());
    }

    @Test
    @WithMockUser(username = "player1", password = "0wn3r")
    void shouldInviteByCode() {
        int index = 3;
        String code = gameService.findAll().get(index).getCode();
        Game gameBeforeInviting = gameService.findGameById(gameService.findAll().get(index).getId());
        gameService.invitationByCode(code);
        Game gameAfterInviting = gameService.findGameById(gameService.findAll().get(index).getId());
        assertEquals(gameBeforeInviting.getPlayers().size()+1,gameAfterInviting.getPlayers().size());
    }

    @Test
    @WithMockUser(username = "player5", password = "0wn3r")
    void shouldInviteByCodeSpectator() {
        int index = 3;
        String code = gameService.findAll().get(index).getCode();
        Game gameBeforeInviting = gameService.findGameById(gameService.findAll().get(index).getId());
        gameService.invitationByCodeSpectators(code);
        Game gameAfterInviting = gameService.findGameById(gameService.findAll().get(index).getId());
        assertEquals(gameBeforeInviting.getSpectators().size()+1,gameAfterInviting.getSpectators().size());
    }

    @Test
    @WithMockUser(username = "player5", password = "0wn3r")
    void shouldAddPlayersToGame() {
        List<Player> jugadores = (List<Player>) playerService.findAll();
        Player jugador1 = jugadores.get(1);
        Player jugador2 = jugadores.get(2);
        Player jugador3 = jugadores.get(3);
        List<Player> newPlayers = List.of(jugador1, jugador2, jugador3);

        Game partidaEmpezada = gameService.saveGame();
        List<Player> players = partidaEmpezada.getPlayers();
        players.addAll(newPlayers);
        partidaEmpezada.setPlayers(players);
        partidaEmpezada.setStartedAt(LocalDateTime.of(2023, 12, 12, 12, 30));
        gameService.updateGameByGameId(partidaEmpezada.getId(), partidaEmpezada);

        assertEquals(LocalDateTime.of(2023, 12, 12, 12, 30), partidaEmpezada.getStartedAt());
        assertEquals(4, partidaEmpezada.getPlayers().size());
    }

    @Test
    @WithMockUser(username = "player5", password = "0wn3r")
    void shouldExitPlayerByUserIdStartedGame() {
        List<Player> jugadores = (List<Player>) playerService.findAll();
        Player jugador1 = jugadores.get(1);
        Player jugador2 = jugadores.get(2);
        Player jugador3 = jugadores.get(3);
        List<Player> newPlayers = List.of(jugador1, jugador2, jugador3);

        Game partidaLista = gameService.saveGame();
        List<Player> players = partidaLista.getPlayers();
        players.addAll(newPlayers);
        partidaLista.setPlayers(players);
        gameService.updateGameByGameId(partidaLista.getId(), partidaLista);
        Game partidaEmpezada = gameService.startPlayGameById(partidaLista.getId());

        Game partidaDespuesDeEchar = gameService.exitUserById(partidaEmpezada.getId(), jugador1.getUser().getId());

        assertEquals(3, partidaDespuesDeEchar.getPlayers().size());
        assertEquals(1, partidaDespuesDeEchar.getQuitters().size());
    }

    @Test
    @WithMockUser(username = "player5", password = "0wn3r")
    void shouldDeleteGame(){
        Integer games = gameService.findAll().size();
        Game game = gameService.saveGame();
        Integer gamesBeforeCreation = gameService.findAll().size();
        gameService.deleteGame(game.getId());
        Integer gamesAfterCreation = gameService.findAll().size();

        assertEquals(games, gamesAfterCreation);
        assertEquals(games+1, gamesBeforeCreation);
        assertEquals(gamesBeforeCreation-1, gamesAfterCreation);
    }

    @Test
    @WithMockUser(username = "player5", password = "0wn3r")
    void shouldStartGameByGameId() {
        Game partidaEmpezada = getStartedGame();

        assertNotNull(partidaEmpezada.getStartedAt());
        assertNull(partidaEmpezada.getFinishedAt());
    }

    private Game getStartedGame() {
        List<Player> jugadores = (List<Player>) playerService.findAll();
        Player jugador1 = jugadores.get(1);
        Player jugador2 = jugadores.get(2);
        Player jugador3 = jugadores.get(3);
        List<Player> newPlayers = List.of(jugador1, jugador2, jugador3);

        Game partidaLista = gameService.saveGame();
        List<Player> players = partidaLista.getPlayers();
        players.addAll(newPlayers);
        partidaLista.setPlayers(players);
        gameService.updateGameByGameId(partidaLista.getId(), partidaLista);
        return gameService.startPlayGameById(partidaLista.getId());
    }

    @Test
    @WithMockUser(username = "player5", password = "0wn3r")
    void shouldUpdateTurnByGameId() {
        Game startedGame = getStartedGame();
        Player playerInTurnBeforeUpdate = startedGame.getPlayers().stream().dropWhile(Player::getIsTurn).toList().get(0);
        Game updatedGame = gameService.updateTurnByGameId(startedGame.getId());
        Player playerInTurnAfterUpdate = updatedGame.getPlayers().stream().dropWhile(Player::getIsTurn).toList().get(0);

        assertNotEquals(playerInTurnBeforeUpdate.getId(), playerInTurnAfterUpdate.getId());
    }

    @Test
    @WithMockUser(username = "player5", password = "0wn3r")
    void shouldFinishGame() throws Exception {
        Game startedGame = getStartedGame();
        Game finishedGame = gameService.finishGame(startedGame.getId());

        assertNotNull(finishedGame.getFinishedAt());
    }

    @Test
    @WithMockUser(username = "player5", password = "0wn3r")
    void shouldUpdateGameFinishedDependencies() {
        Integer startedGameId = getStartedGame().getId();
        gameService.updateGameFinishedDependencies(startedGameId);

        assertEquals(0, cardService.findCardsByGameId(startedGameId).size());
        assertEquals(0, islandService.findIslandsByGameId(startedGameId).size());
        assertEquals(0, chatRepository.findMessagesByGame(startedGameId).size());
    }

    @Test
    @WithMockUser(username = "player5", password = "0wn3r")
    void shouldUpdateGameByGameId() {
        List<Player> jugadores = (List<Player>) playerService.findAll();
        Player jugador1 = jugadores.get(1);
        List<Player> newPlayers = List.of(jugador1);

        Game partidaLista = gameService.saveGame();
        List<Player> players = partidaLista.getPlayers();
        players.addAll(newPlayers);
        partidaLista.setPlayers(players);
        Game updatedGame = gameService.updateGameByGameId(partidaLista.getId(), partidaLista);

        assertEquals(2, updatedGame.getPlayers().size());
    }

}
