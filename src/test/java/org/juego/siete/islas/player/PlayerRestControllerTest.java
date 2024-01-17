package org.juego.siete.islas.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.juego.siete.islas.card.Card;
import org.juego.siete.islas.card.CardType;
import org.juego.siete.islas.configuration.SecurityConfiguration;
import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.island.Island;
import org.juego.siete.islas.user.Authorities;
import org.juego.siete.islas.user.User;
import org.juego.siete.islas.user.UserService;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PlayerRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class PlayerRestControllerTest {
    private static final Integer TEST_PLAYER_ID = 1;
    private static final Integer TEST_USER_ID = 2;
    private static final String BASE_URL = "/api/v1/players";

    @MockBean
    PlayerService ps;
    @MockBean
    UserService us;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Player player1;
    private Player player2;
    private Player player3;
    private User user1;

    private Card card1;

    private PlayerEditDTO editDTO;

    @BeforeEach
    void setup() {
        Authorities userAuth = new Authorities();
        userAuth.setId(1);
        userAuth.setAuthority("player");

        user1 = new User();
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
        player1.setImage("Default");
        player1.setIsConnected(false);

        User user2 = new User();
        user2.setId(2);
        user2.setUsername("usuario2");
        user2.setPassword("contrasenaUsuario2");
        user2.setAuthority(userAuth);

        player2 = new Player();
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

        player3 = new Player();
        player3.setId(3);
        player3.setFirstName("Alfonso");
        player3.setLastName("Lopez");
        player3.setEmail("emailplayer3@gmail.com");
        player3.setBirthdayDate(LocalDate.of(2003, 11, 11));
        player3.setRegistrationDate(LocalDate.of(2013, 12, 22));
        player3.setUser(user3);

        editDTO = new PlayerEditDTO();
        editDTO.setFirstName(player1.getFirstName());
        editDTO.setLastName(player1.getLastName());
        editDTO.setEmail(player1.getEmail());
        editDTO.setUsername(player1.getUser().getUsername());
        editDTO.setPassword(player1.getUser().getPassword());
        editDTO.setBirthdayDate(player1.getBirthdayDate());

        Game game1 = new Game();
        game1.setId(1);
        game1.setCode("BsaA");

        Island island1 = new Island();
        island1.setId(1);
        island1.setNumber(1);

        card1 = new Card();
        card1.setPlayer(player1);
        card1.setSelected(false);
        card1.setReversed(false);
        card1.setGame(game1);
        card1.setType(CardType.BarrilDeRon);
        card1.setId(1);
        card1.setIsland(island1);

    }

    @Test
    @WithMockUser("admin")
    void adminShouldFindAll() throws Exception {
        when(this.ps.findAll()).thenReturn(List.of(player1, player2, player3));
        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$[?(@.id == 1)].firstName").value("Dani"))
                .andExpect(jsonPath("$[?(@.id == 2)].firstName").value("Juan"))
                .andExpect(jsonPath("$[?(@.id == 3)].firstName").value("Alfonso"));
    }

    @Test
    @WithMockUser("player")
    void playerShouldReturnPlayer() throws Exception {
        when(this.ps.findPlayerById(TEST_PLAYER_ID)).thenReturn(player1);
        mockMvc.perform(get(BASE_URL + "/{id}", TEST_PLAYER_ID)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_PLAYER_ID))
                .andExpect(jsonPath("$.firstName").value(player1.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(player1.getLastName()))
                .andExpect(jsonPath("$.email").value(player1.getEmail()));
    }

    @Test
    @WithMockUser("admin")
    void adminShouldReturnPlayer() throws Exception {
        when(this.ps.findPlayerById(TEST_PLAYER_ID)).thenReturn(player1);
        mockMvc.perform(get(BASE_URL + "/{id}", TEST_PLAYER_ID)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_PLAYER_ID))
                .andExpect(jsonPath("$.firstName").value(player1.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(player1.getLastName()))
                .andExpect(jsonPath("$.email").value(player1.getEmail()));
    }

    @Test
    @WithMockUser("player")
    void playerShouldReturnNotFoundPlayer() throws Exception {
        when(this.ps.findPlayerById(TEST_PLAYER_ID)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get(BASE_URL + "/{id}", TEST_PLAYER_ID)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("player")
    void adminShouldReturnNotFoundPlayer() throws Exception {
        when(this.ps.findPlayerById(TEST_PLAYER_ID)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get(BASE_URL + "/{id}", TEST_PLAYER_ID)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("player")
    void playerShouldCreatePlayer() throws Exception {
        Player player = new Player();
        player.setFirstName("Prueba");
        player.setLastName("Prueba");
        player.setEmail("emaildeprueba@gmail.com");
        player.setId(TEST_PLAYER_ID);
        player.setRegistrationDate(LocalDate.of(2010, 10, 10));
        player.setBirthdayDate(LocalDate.of(2001, 1, 1));
        player.setUser(user1);
        player.setImage("Default");
        mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player))).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("admin")
    void shouldFindCardsByPlayerId() throws Exception {
        when(this.ps.findCardsByPlayerId(TEST_PLAYER_ID)).thenReturn(List.of(card1));

        mockMvc.perform(get(BASE_URL + "/{playerId}/cards", TEST_PLAYER_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(1))
            .andExpect(jsonPath("$.[?(@.id == 1)].id").value(1));
    }

    @Test
    @WithMockUser("admin")
    void adminShouldCreatePlayer() throws Exception {
        Player player = new Player();
        player.setFirstName("Prueba");
        player.setLastName("Prueba");
        player.setEmail("emaildeprueba@gmail.com");
        player.setId(TEST_PLAYER_ID);
        player.setRegistrationDate(LocalDate.of(2010, 10, 10));
        player.setBirthdayDate(LocalDate.of(2001, 1, 1));
        player.setUser(user1);
        player.setImage("Default");

        mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(player))).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("player")
    void shouldUpdatePlayer() throws Exception {
        player1.setFirstName("UPDATED");
        player1.setEmail("updatedMail@gmail.com");
        player1.setImage("Default");


        editDTO.setFirstName(player1.getFirstName());
        editDTO.setEmail(player1.getEmail());
        editDTO.setImage(player1.getImage());

        when(ps.findPlayerByUsername(player1.getUser().getUsername())).thenReturn(Optional.ofNullable(player1));
        when(ps.findPlayerById(TEST_PLAYER_ID)).thenReturn(player1);
        when(ps.updatePlayer(any(PlayerEditDTO.class), any(Integer.class))).thenReturn(player1);

        mockMvc.perform(put(BASE_URL + "/{id}", TEST_PLAYER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editDTO))).andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Player edited successfully!"));
    }

    @Test
    @WithMockUser("player")
    void shouldReturnNotFoundUpdateOwner() throws Exception {
        player1.setFirstName("UPDATED");
        player1.setLastName("UPDATED");

        editDTO.setFirstName(player1.getFirstName());
        editDTO.setEmail(player1.getEmail());
        editDTO.setImage("Default");

        when(this.ps.findPlayerById(TEST_PLAYER_ID)).thenThrow(ResourceNotFoundException.class);
        when(this.ps.updatePlayer(any(PlayerEditDTO.class), any(Integer.class))).thenReturn(player1);

        mockMvc.perform(put(BASE_URL + "/{id}", TEST_PLAYER_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editDTO))).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("admin")
    void shouldUpdateConnection() throws Exception {
        player1.setIsConnected(true);

        when(this.ps.findPlayerByUser(TEST_USER_ID)).thenReturn(Optional.ofNullable(player1));
        when(this.ps.updatePlayerConnection(any(Integer.class))).thenReturn(player1);

        mockMvc.perform(put(BASE_URL + "/{userId}/connection", TEST_USER_ID)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TEST_USER_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isConnected").value(true));
    }

    @Test
    @WithMockUser("player")
    void playerShouldDeletePlayer() throws Exception {
        when(this.ps.findPlayerById(TEST_PLAYER_ID)).thenReturn(player1);

        doNothing().when(this.ps).deletePlayer(TEST_PLAYER_ID);
        mockMvc.perform(delete(BASE_URL + "/{id}", TEST_PLAYER_ID).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("admin")
    void adminShouldDeletePlayer() throws Exception {
        when(this.ps.findPlayerById(TEST_PLAYER_ID)).thenReturn(player1);

        doNothing().when(this.ps).deletePlayer(TEST_PLAYER_ID);
        mockMvc.perform(delete(BASE_URL + "/{id}", TEST_PLAYER_ID).with(csrf()))
                .andExpect(status().isOk());
    }

}
