package org.juego.siete.islas.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.juego.siete.islas.configuration.SecurityConfiguration;
import org.juego.siete.islas.player.Player;
import org.juego.siete.islas.player.PlayerService;
import org.juego.siete.islas.user.Authorities;
import org.juego.siete.islas.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = GameRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class GameRestControllerTest {

    private static final Integer TEST_GAME_ID = 1;
    private static final Integer TEST_USER_ID = 2;
    private static final Integer TEST_PLAYER_ID = 1;
    private static final String BASE_URL = "/api/v1/games";

    @MockBean
    GameService gameService;

    @MockBean
    PlayerService playerService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Game game1;
    private Game game2;
    private Player player1;

    @BeforeEach
    void setup() {
        Authorities userAuth = new Authorities();
        userAuth.setId(1);
        userAuth.setAuthority("player");

        User user1 = new User();
        user1.setId(TEST_USER_ID);
        user1.setUsername("usuario1");
        user1.setPassword("contrasenaUsuario1");
        user1.setAuthority(userAuth);

        player1 = new Player();
        player1.setId(TEST_PLAYER_ID);
        player1.setFirstName("Dani");
        player1.setLastName("Alonso");
        player1.setEmail("emailplayer1@gmail.com");
        player1.setBirthdayDate(LocalDate.of(2003, 1, 15));
        player1.setRegistrationDate(LocalDate.of(2013, 5, 16));
        player1.setUser(user1);

        User user2 = new User();
        user2.setId(2);
        user2.setUsername("usuario2");
        user2.setPassword("contrasenaUsuario2");
        user2.setAuthority(userAuth);

        Player player2 = new Player();
        player2.setId(2);
        player2.setFirstName("Juan");
        player2.setLastName("Perez");
        player2.setEmail("emailplayer2@gmail.com");
        player2.setBirthdayDate(LocalDate.of(2003, 5, 3));
        player2.setRegistrationDate(LocalDate.of(2013, 7, 10));
        player2.setUser(user2);

        User user3 = new User();
        user3.setId(3);
        user3.setUsername("usuario3");
        user3.setPassword("contrasenaUsuario3");
        user3.setAuthority(userAuth);

        Player player3 = new Player();
        player3.setId(3);
        player3.setFirstName("Alfonso");
        player3.setLastName("Lopez");
        player3.setEmail("emailplayer3@gmail.com");
        player3.setBirthdayDate(LocalDate.of(2003, 11, 11));
        player3.setRegistrationDate(LocalDate.of(2013, 12, 22));
        player3.setUser(user3);

        User user4 = new User();
        user4.setId(4);
        user4.setUsername("usuario4");
        user4.setPassword("contrasenaUsuario4");
        user4.setAuthority(userAuth);

        Player player4 = new Player();
        player4.setId(4);
        player4.setFirstName("Antonio");
        player4.setLastName("Fernandez");
        player4.setEmail("emailplayer4@gmail.com");
        player4.setBirthdayDate(LocalDate.of(2003, 1, 10));
        player4.setRegistrationDate(LocalDate.of(2013, 12, 12));
        player4.setUser(user3);

        game1 = new Game();
        game1.setId(TEST_GAME_ID);
        game1.setPlayers(List.of(player1, player2, player3));
        game1.setCreator(player1);
        game1.setCode("BaSh");
        game1.setStartedAt(LocalDateTime.of(2023, 12, 12, 18, 10, 0));
        game1.setFinishedAt(LocalDateTime.of(2023, 12, 12, 18, 45, 12));
        game1.setQuitters(List.of(player2));
        game1.setSpectators(List.of(player4));

        game2 = new Game();
        game2.setId(2);
        game2.setPlayers(List.of(player1, player2, player3));
        game2.setCreator(player2);
        game2.setCode("PlIf");
        game2.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 5, 12));

    }

    @Test
    @WithMockUser("admin")
    void shouldFindAllGames() throws Exception {
        when(this.gameService.findAll()).thenReturn(List.of(game1));
        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("[?(@.id == 1)].id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void shouldFindAllGamesByStatus() throws Exception {
        when(this.gameService.findGamesByStatus(GameStatus.FINISHED)).thenReturn(List.of(game1));
        mockMvc.perform(get(BASE_URL + "/status/{status}", GameStatus.FINISHED))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("$.[?(@.id == 1)].id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void shouldFindGamesUnstartedByUserId() throws Exception {
        when(this.gameService.findGamesUnstartedByUserId(TEST_USER_ID)).thenReturn(Optional.ofNullable(game2));
        mockMvc.perform(get(BASE_URL + "/reconnectLobby/{userId}", 2))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    @WithMockUser("admin")
    void shouldFindGameById() throws Exception {
        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game1);
        mockMvc.perform(get(BASE_URL + "/{gameId}", TEST_GAME_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void shouldFindGamesByUserId() throws Exception {
        when(this.playerService.findPlayerByUser(TEST_USER_ID)).thenReturn(Optional.ofNullable(player1));
        when(this.gameService.findGamesByUserId(TEST_USER_ID)).thenReturn(List.of(game1));
        mockMvc.perform(get(BASE_URL + "/user/{userId}", 2))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("[?(@.id == 1)].id").value(1));
    }


    @Test
    @WithMockUser("admin")
    void shouldFindRecentGameByUserId() throws Exception {
        when(this.gameService.findRecentGameByPlayerId()).thenReturn(game1);
        mockMvc.perform(get(BASE_URL + "/userMostRecent"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void shouldFindRecentGameByUserIdForInvitations() throws Exception {
        when(this.gameService.findRecentGameByPlayerIdForInvitations(TEST_USER_ID)).thenReturn(game1.getId());
        mockMvc.perform(get(BASE_URL + "/userMostRecent/{userId}", 1))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("admin")
    void shouldCreateGame() throws Exception {
        mockMvc.perform(post(BASE_URL).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(game1)))
            .andExpect(status().isCreated());

    }


    @Test
    @WithMockUser("admin")
    void shouldUpdatePlayerByCode() throws Exception {
        game1.setCode("ALgs");
        when(this.gameService.invitationByCode(game1.getCode())).thenReturn(game1);
        mockMvc.perform(put(BASE_URL + "/code/{codigo}", game1.getCode())
                .with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(game1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }


    @Test
    @WithMockUser("admin")
    void shouldUpdateExitPlayer() throws Exception {
        when(this.gameService.exitUserById(TEST_GAME_ID, TEST_USER_ID)).thenReturn(game1);
        mockMvc.perform(put(BASE_URL + "/game/{gameId}/exit/{userId}", 1, 2)
            .with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(game1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void shouldUpdateStartGame() throws Exception {
        when(this.gameService.startPlayGameById(TEST_GAME_ID)).thenReturn(game1);
        mockMvc.perform(put(BASE_URL + "/start/{gameId}", 1)
            .with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(game1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void shouldUpdateTurnByGameId() throws Exception {
        when(this.gameService.updateTurnByGameId(TEST_GAME_ID)).thenReturn(game1);
        mockMvc.perform(put(BASE_URL + "/{gameId}/turn", TEST_GAME_ID)
            .with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(game1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void shouldUpdateFinishGame() throws Exception {
        when(this.gameService.finishGame(TEST_GAME_ID)).thenReturn(game1);
        mockMvc.perform(put(BASE_URL + "/finish/{gameId}", TEST_GAME_ID)
            .with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(game1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void shouldUpdateGameFinished() throws Exception {
        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(new Game());
        doNothing().when(this.gameService).updateGameFinishedDependencies(TEST_GAME_ID);
        mockMvc.perform(put(BASE_URL + "/dependencies/{gameId}", TEST_GAME_ID)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Game finished!"));
    }

    @Test
    @WithMockUser("admin")
    void shouldDelete() throws Exception {
        when(this.gameService.findGameById(TEST_GAME_ID)).thenReturn(game1);

        doNothing().when(this.gameService).deleteGame(TEST_GAME_ID);
        mockMvc.perform(delete(BASE_URL + "/{gameId}", TEST_GAME_ID).with(csrf()))
            .andExpect(status().isOk());
    }

}
