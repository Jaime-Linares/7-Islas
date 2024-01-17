package org.juego.siete.islas.stat;

import org.juego.siete.islas.card.CardService;
import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.game.GameService;
import org.juego.siete.islas.island.IslandService;
import org.juego.siete.islas.player.Player;
import org.juego.siete.islas.player.PlayerService;
import org.juego.siete.islas.user.User;
import org.juego.siete.islas.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StatServiceTest {

    @Autowired
    protected PlayerService playerService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected GameService gameService;

    @Autowired
    protected StatService statService;

    @Autowired
    protected IslandService islandService;

    @Autowired
    protected CardService cardService;

    @Test
    void findStatById() {
        Stat savedStat = statService.findStatById(1);
        assertEquals(1, savedStat.getId());
    }

    @Test
    void shouldntFindStatById() {
        assertThrows(ResourceNotFoundException.class, () -> statService.findStatsByUserId(-1));
    }

    @Test
    void findStatByUserId() {
        Player player1 = playerService.findPlayerById(1);
        Stat statSearch = statService.findStatsByUserId(player1.getUser().getId());
        assertEquals(1, statSearch.getId());
    }

    @Test
    void shouldntFindStatByUserId() {
        User admin1 = playerService.findUserById(1);
        assertThrows(ResourceNotFoundException.class, () -> statService.findStatsByUserId(admin1.getId()));
    }

    @Test
    void saveStat() {
        Stat statToSave = new Stat();
        statToSave.setTimePlayed(LocalTime.of(0, 0, 0));
		statToSave.setTimeLongestGame(LocalTime.of(0, 0, 0));
		statToSave.setTimeShortestGame(LocalTime.of(0, 0, 0));
		statToSave.setNumGamesPlays(0);
		statToSave.setNumGamesWinned(0);
		statToSave.setAverageScore(0.0);
		statToSave.setAverageNumCardsEndGames(0.0);

        Stat savedStat = statService.saveStat(statToSave);

        assertEquals(100, savedStat.getId());
    }

    @Test
    void findRankingByAtributte() {
        List<Player> players = (List<Player>) playerService.findAll();
        List<Player> ranking = statService.findRankingByAtributte("averageScore");
        assertEquals(players.size(), ranking.size());
    }

    @Test
    void shouldntFindRankingByAtributte() {
        assertThrows(ResourceNotFoundException.class, () -> statService.findRankingByAtributte("timePlayed"));
    }

    @Test
    void shouldUpdateStat() {
        Stat statToUpdate = statService.findStatById(1);
        statToUpdate.setNumGamesPlays(100);
        Stat statUpdated = statService.updateStat(statToUpdate, statToUpdate.getId());

        assertEquals(100, statUpdated.getNumGamesPlays());
    }

    @Test
    @WithMockUser(username = "player5", password = "0wn3r")
    void shouldUpdateStatAfterFinishGame() throws Exception {
        User currentUser = userService.findCurrentUser();
        Stat statsBeforeUpdating = statService.findStatsByUserId(currentUser.getId());
        Game game = getStartedGame();

        //Dentro del siguiente metodo se ejecuta la funcion a la que referimos en el test
        gameService.finishGame(game.getId());

        Stat statsAfterUpdating = statService.findStatsByUserId(currentUser.getId());

        assertEquals(statsBeforeUpdating.getNumGamesPlays() + 1, statsAfterUpdating.getNumGamesPlays());
    }

    private Game getStartedGame() {
        List<Player> jugadores = (List<Player>) playerService.findAll();
        Player jugador1 = jugadores.get(1);
        List<Player> newPlayers = List.of(jugador1);

        Game partidaLista = gameService.saveGame();
        List<Player> players = partidaLista.getPlayers();
        players.addAll(newPlayers);
        partidaLista.setPlayers(players);
        gameService.updateGameByGameId(partidaLista.getId(), partidaLista);
        return gameService.startPlayGameById(partidaLista.getId());
    }

}
