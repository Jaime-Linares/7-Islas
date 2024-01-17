package org.juego.siete.islas.player;

import org.juego.siete.islas.card.Card;
import org.juego.siete.islas.card.CardService;
import org.juego.siete.islas.card.CardType;
import org.juego.siete.islas.game.GameService;
import org.juego.siete.islas.island.Island;
import org.juego.siete.islas.island.IslandService;
import org.juego.siete.islas.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PlayerServiceTest {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private CardService cardService;

    @Autowired
    private GameService gameService;

    @Autowired
    private IslandService islandService;

    @Test
    void shouldFindAllUsers() {
        List<Player> players = (List<Player>) this.playerService.findAll();
        assertEquals(5, players.size());
    }

    @Test
    void shouldFindPlayerById() {
        Player player = this.playerService.findPlayerById(1);
        assertEquals("player1", player.getUser().getUsername());
    }

    @Test
    void shouldFindPlayerByUserId() {
        Optional<Player> player = this.playerService.findPlayerByUser(2);

        assertTrue(player.isPresent());
        assertEquals("player1", player.get().getUser().getUsername());
    }

    @Test
    void shouldNotFindUserByPlayerId() {
        Optional<Player> player = this.playerService.findPlayerByUser(1);
        assertFalse(player.isPresent());
    }

    @Test
    void shouldFindCardsByPlayerId() {
        Card card = new Card();
        Island island = new Island();
        island.setGame(gameService.findGameById(2));
        island.setNumber(2);
        islandService.saveIsland(island);

        card.setGame(gameService.findGameById(2));
        card.setPlayer(playerService.findPlayerById(1));
        card.setReversed(false);
        card.setSelected(false);
        card.setType(CardType.BarrilDeRon);
        card.setIsland(island);
        this.cardService.saveCard(card);
        List<Card> cards = this.playerService.findCardsByPlayerId(1);
        assertEquals(cards.size(), 1);
    }

    @Test
    void shouldsavePlayer() {
        Player player = playerService.findPlayerById(1);
        player.setFirstName("TEST_NAME");
        playerService.savePlayer(player, false);
        Player savedPlayer = playerService.findPlayerById(1);
        assertEquals("TEST_NAME", savedPlayer.getFirstName());
    }

    @Test
    void shouldupdatePlayer(){
        Player player = playerService.findPlayerById(2);
        PlayerEditDTO playerEdit = new PlayerEditDTO("TEST_USERNAME",
        "image",
         "0wn3r",
          player.getFirstName(),
          player.getLastName(),
          player.getBirthdayDate(),
          player.getEmail()
        );
        playerService.updatePlayer(playerEdit, 2);

        Player savedPlayer = playerService.findPlayerById(2);
        assertEquals("TEST_USERNAME", savedPlayer.getUser().getUsername());
    }

    @Test
    void shouldUpdatePlayerConnection(){
        Player player = playerService.findPlayerById(2);
        playerService.updatePlayerConnection(2);
        Player savedPlayer = playerService.findPlayerById(2);
        assertEquals(player.getIsConnected(), !savedPlayer.getIsConnected());
    }

    @Test
    void shouldDeletePlayer(){
        List<Player> players = (List<Player>) playerService.findAll();
        int playerCount = players.size();
        playerService.deletePlayer(2);
        players = (List<Player>) playerService.findAll();
        Integer playerCountAfterDelete = players.size();
        assertEquals(playerCount-1, playerCountAfterDelete);
    }

    @Test
    void shouldFindUserById(){
        User user = playerService.findUserById(1);
        assertNotNull(user);
    }

    @Test
    void shouldGetNumberPlayer(){
        Map<String, Object> map = playerService.getNumberPlayers();
        List<Player> players = (List<Player>) playerService.findAll();
        assertEquals(map.get("totalPlayers"), players.size());
    }

    @Test
    void shouldUpdatePlayerForAchievements(){
        Player player = playerService.findPlayerById(2);
        Player changedPlayerTurn = playerService.updatePlayerForAchievements(player, 2);
        assertEquals(player.getId(), changedPlayerTurn.getId());
    }
}
