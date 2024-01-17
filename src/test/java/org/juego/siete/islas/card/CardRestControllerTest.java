package org.juego.siete.islas.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.juego.siete.islas.configuration.SecurityConfiguration;
import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.island.Island;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CardRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class CardRestControllerTest {

    private static final Integer TEST_USER_ID = 2;
    private static final Integer TEST_PLAYER_ID = 1;
    private static final Integer TEST_CARD_ID = 1;
    private static final Integer TEST_GAME_ID = 1;
    private static final String BASE_URL = "/api/v1/cards";

    @MockBean
    CardService cardService;
    @MockBean
    PlayerService playerService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Card card1;
    private Card card2;
    private Island island2;

    private Player player1;

    private SelectedCardsDTO selectedCardsDTO;

    @BeforeEach
    void setup() {
        Game game1 = new Game();
        game1.setId(TEST_GAME_ID);

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


        Island island1 = new Island();
        island1.setNumber(1);
        island1.setGame(game1);

        island2 = new Island();
        island1.setNumber(2);
        island1.setGame(game1);

        card1 = new Card();
        card1.setId(TEST_CARD_ID);
        card1.setPlayer(player1);
        card1.setReversed(false);
        card1.setSelected(false);
        card1.setGame(game1);
        card1.setType(CardType.BarrilDeRon);

        card2 = new Card();
        card2.setReversed(true);
        card2.setId(2);
        card2.setType(CardType.Caliz);
        card2.setSelected(false);
        card2.setGame(game1);

        Card card3 = new Card();
        card3.setReversed(true);
        card3.setIsland(island1);
        card3.setGame(game1);
        card3.setPlayer(null);
        card3.setType(CardType.Doblon);
        card3.setSelected(false);

        selectedCardsDTO = new SelectedCardsDTO();
        selectedCardsDTO.setSelectedCards(List.of());

    }

    @Test
    @WithMockUser("admin")
    void shouldFindAll() throws Exception {
        when(this.cardService.findAll()).thenReturn(List.of(card1, card2));
        mockMvc.perform(get(BASE_URL)).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[?(@.id == 1)].reversed").value(false))
                .andExpect(jsonPath("$[?(@.id == 2)].reversed").value(true));
    }

    @Test
    @WithMockUser("admin")
    void shouldReturnCardById() throws Exception {
        when(this.cardService.findCardById(TEST_CARD_ID)).thenReturn(card1);
        mockMvc.perform(get(BASE_URL + "/{id}", TEST_CARD_ID)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_CARD_ID))
                .andExpect(jsonPath("$.reversed").value(card1.getReversed()))
                .andExpect(jsonPath("$.type").value(card1.getType().toString()));
    }

    @Test
    @WithMockUser("admin")
    void shouldReturnCardByGameId() throws Exception {
        when(this.cardService.findCardsByGameId(TEST_GAME_ID)).thenReturn(List.of(card1));

        mockMvc.perform(get(BASE_URL + "/game" + "/{id}", TEST_GAME_ID)).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[?(@.id == 1)].reversed").value(false));
    }

    @Test
    @WithMockUser("admin")
    void shouldReturnNotFoundCard() throws Exception {
        when(this.cardService.findCardById(TEST_CARD_ID)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(get(BASE_URL + "/{id}", TEST_CARD_ID)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("admin")
    void shouldCreateCard() throws Exception {
        mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(card1))).andExpect(status().isCreated());
    }
    @Test
    @WithMockUser("admin")
    void shouldUpdateCard() throws Exception {
        card1.setIsland(island2);

        when(this.cardService.findCardById(1)).thenReturn(card1);
        when(this.playerService.findPlayerById(TEST_PLAYER_ID)).thenReturn(player1);
        when(this.cardService.updateCard(card1.getId(), TEST_PLAYER_ID)).thenReturn(card1);

        mockMvc.perform(put(BASE_URL + "/{cardId}/player/{playerId}", 1, 1)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(card1)))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("admin")
    void shouldUpdateSelectedCards() throws Exception {
        card1.setSelected(true);
        selectedCardsDTO.setSelectedCards(List.of(card1));

        when(this.cardService.findCardById(TEST_CARD_ID)).thenReturn(card1);
        when(this.playerService.findPlayerById(TEST_PLAYER_ID)).thenReturn(player1);
        when(this.cardService.updateSelectedCards
            (any(SelectedCardsDTO.class), any(Integer.class), any(Integer.class)))
            .thenReturn(card1);


        mockMvc.perform(put(BASE_URL + "/{cardId}/player/{playerId}/selectedCards", 1,1)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(selectedCardsDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.selected").value(true));

    }

    @Test
    @WithMockUser("admin")
    void shouldUpdateSelected() throws Exception {
        card1.setSelected(true);
        when(this.cardService.findCardById(TEST_CARD_ID)).thenReturn(card1);
        when(this.cardService.updateSelected(any(Integer.class))).thenReturn(card1);

        mockMvc.perform(put(BASE_URL + "/{cardId}/updateSelection", 1)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(card1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.selected").value(true));
    }

    @Test
    @WithMockUser("admin")
    void shouldDeleteCard() throws Exception {
        when(this.cardService.findCardById(TEST_CARD_ID)).thenReturn(card1);

        doNothing().when(this.cardService).deleteCard(TEST_CARD_ID);
        mockMvc.perform(delete(BASE_URL + "/{id}", TEST_CARD_ID).with(csrf()))
                .andExpect(status().isOk());
    }
}
