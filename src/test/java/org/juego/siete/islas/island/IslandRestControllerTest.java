package org.juego.siete.islas.island;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.juego.siete.islas.card.Card;
import org.juego.siete.islas.card.CardType;
import org.juego.siete.islas.configuration.SecurityConfiguration;
import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.game.Game;
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

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(value = IslandRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class IslandRestControllerTest {

  @MockBean
  IslandService islandService;

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;

  private static final String BASE_URL = "/api/v1/islands";

  private static final Integer TEST_GAME_ID = 1;
  private static final Integer TEST_ISLAND_ID = 1;

  Island island1;
  Island island2;
  Card card1;
  Card card2;

  @BeforeEach
  void setup() {
    Game game1 = new Game();
    game1.setId(TEST_GAME_ID);

    island1 = new Island();
    island1.setGame(game1);
    island1.setId(TEST_ISLAND_ID);
    island1.setNumber(1);

    island2 = new Island();
    island2.setGame(game1);
    island2.setId(2);
    island2.setNumber(2);

    card1 = new Card();
    card1.setId(1);
    card1.setGame(game1);
    card1.setType(CardType.Doblon);
    card1.setReversed(false);
    card1.setSelected(true);
    card1.setIsland(island1);

    card2 = new Card();
    card2.setReversed(true);
    card2.setId(2);
    card2.setType(CardType.Caliz);
    card2.setSelected(false);
    card2.setGame(game1);
    card2.setIsland(island2);
  }

  @Test
  @WithMockUser("admin")
  void shouldFindAll() throws Exception {
    when(this.islandService.findAll()).thenReturn(List.of(island1, island2));
    mockMvc.perform(get(BASE_URL)).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("[?(@.id == 1)].number").value(1))
        .andExpect(jsonPath("[?(@.id == 2)].number").value(2));
  }

  @Test
  @WithMockUser("admin")
  void shouldFindIslandById() throws Exception {
    when(this.islandService.findIslandById(island1.getId())).thenReturn(island1);
    mockMvc.perform(get(BASE_URL + "/{islandId}", TEST_ISLAND_ID)).andExpect(status().isOk())
        .andExpect(jsonPath("$.number").value(1))
        .andExpect(jsonPath("$.game.id").value(TEST_GAME_ID));
  }

  @Test
  @WithMockUser("admin")
  void shouldFindIslandsByGameId() throws Exception {
    when(this.islandService.findIslandsByGameId(TEST_GAME_ID)).thenReturn(List.of(island1, island2));
    mockMvc.perform(get(BASE_URL + "/game/{gameId}", TEST_GAME_ID)).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("[?(@.id == 1)].number").value(1))
        .andExpect(jsonPath("[?(@.id == 2)].number").value(2));
  }

  @Test
  @WithMockUser("admin")
  void shouldFindCardsByIslandId() throws Exception {
    when(this.islandService.findCardsByIslandId(TEST_ISLAND_ID)).thenReturn(List.of(card1));
    mockMvc.perform(get(BASE_URL + "/{islandId}/cards", TEST_ISLAND_ID)).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("[?(@.id == 1)].reversed").value(false));
  }

  @Test
  @WithMockUser("admin")
  void shouldCreateIsland() throws Exception {
    mockMvc.perform(post(BASE_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(island1))).andExpect(status().isCreated());
  }

  @Test
  @WithMockUser("admin")
  void shouldDeleteIsland() throws Exception {
    when(this.islandService.findIslandById(TEST_ISLAND_ID)).thenReturn(island1);

    doNothing().when(this.islandService).deleteIsland(TEST_ISLAND_ID);
    mockMvc.perform(delete(BASE_URL + "/{id}", TEST_ISLAND_ID).with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser("admin")
  void shouldNotFindIslandById() throws Exception {
    when(this.islandService.findIslandById(TEST_ISLAND_ID)).thenThrow(ResourceNotFoundException.class);
    mockMvc.perform(get(BASE_URL + "/{islandId}", TEST_ISLAND_ID)).andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser("admin")
  void shouldNotFindIslandByGameId() throws Exception {
    when(this.islandService.findIslandsByGameId(TEST_GAME_ID)).thenThrow(ResourceNotFoundException.class);
    mockMvc.perform(get(BASE_URL + "/game/{gameId}", TEST_GAME_ID)).andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser("admin")

  void shouldNotFindCardsByIslandId() throws Exception {
    when(this.islandService.findCardsByIslandId(TEST_ISLAND_ID)).thenThrow(ResourceNotFoundException.class);
    mockMvc.perform(get(BASE_URL + "/{islandId}/cards", TEST_ISLAND_ID)).andExpect(status().isNotFound());
  }

}
