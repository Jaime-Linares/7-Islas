package org.juego.siete.islas.achievements;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.juego.siete.islas.configuration.SecurityConfiguration;
import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.player.Player;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AchievementRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class AchievementRestControllerTest {

  private static final Integer TEST_USER_ID = 2;
  private static final Integer TEST_PLAYER_ID = 1;
  private static final Integer TEST_PLAYER_ID2 = 2;
  private static final Integer TEST_ACHIEVEMENT_ID = 1;
  private static final String BASE_URL = "/api/v1/achievements";

  @MockBean
  AchievementService achievementService;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  private Achievement achievement1;
  private Achievement achievement2;

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

    Player player1 = new Player();
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
    player2.setId(TEST_PLAYER_ID2);
    player2.setFirstName("Juan");
    player2.setLastName("Perez");
    player2.setEmail("emailplayer2@gmail.com");
    player2.setBirthdayDate(LocalDate.of(2003, 5, 3));
    player2.setRegistrationDate(LocalDate.of(2013, 7, 10));
    player2.setUser(user2);

    achievement1 = new Achievement();
    achievement1.setId(1);
    player1.setAchievements(List.of(achievement1));
    achievement1.setName("Nuevo pirata!!!");
    achievement1.setNumGamesPlays(0);
    achievement1.setNumGamesWinned(0);
    achievement1.setDificulty("Easy");

    achievement2 = new Achievement();
    achievement2.setId(2);
    player2.setAchievements(List.of(achievement2));
    achievement2.setName("Gana una partida");
    achievement2.setNumGamesWinned(1);
    achievement2.setNumGamesPlays(1);

  }

  @Test
  @WithMockUser("player")
  void shouldFindAll() throws Exception {
    when(this.achievementService.findAll()).thenReturn(List.of(achievement1, achievement2));
    mockMvc.perform(get(BASE_URL)).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[?(@.id == 1)].name").value("Nuevo pirata!!!"))
        .andExpect(jsonPath("$[?(@.id == 2)].name").value("Gana una partida"))
        .andExpect(jsonPath("$[?(@.id == 1)].numGamesPlays").value(0))
        .andExpect(jsonPath("$[?(@.id == 2)].numGamesPlays").value(1))
        .andExpect(jsonPath("$[?(@.id == 1)].numGamesWinned").value(0))
        .andExpect(jsonPath("$[?(@.id == 2)].numGamesWinned").value(1));
  }

  @Test
  @WithMockUser("player")
  void shouldFindAchievementById() throws Exception {
    when(this.achievementService.findAchievementById(TEST_ACHIEVEMENT_ID)).thenReturn(achievement1);
    mockMvc.perform(get(BASE_URL + "/{id}", TEST_ACHIEVEMENT_ID)).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(TEST_ACHIEVEMENT_ID))
        .andExpect(jsonPath("$.name").value("Nuevo pirata!!!"))
        .andExpect(jsonPath("$.numGamesPlays").value(0))
        .andExpect(jsonPath("$.numGamesWinned").value(0));
  }

  @Test
  @WithMockUser()
  void shouldFindAchievementByUserId() throws Exception {
    when(this.achievementService.findAchievementByUserId(TEST_USER_ID)).thenReturn(List.of(achievement1));
    mockMvc.perform(get(BASE_URL + "/user/{userId}", TEST_USER_ID)).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1))
        .andExpect(jsonPath("$[?(@.id == 1)].name").value("Nuevo pirata!!!"))
        .andExpect(jsonPath("$[?(@.id == 1)].numGamesPlays").value(0))
        .andExpect(jsonPath("$[?(@.id == 1)].numGamesWinned").value(0));
  }

  @Test
  @WithMockUser("admin")
  void shouldCreateAchievement() throws Exception {
      mockMvc.perform(post(BASE_URL).with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(achievement1)))
              .andExpect(status().isCreated());
  }

  @Test
  @WithMockUser("admin")
  void shouldUpdateAchievement() throws Exception {
    achievement1.setName("UPDATED");
    achievement1.setNumGamesPlays(2);

    when(achievementService.findAchievementById(TEST_ACHIEVEMENT_ID)).thenReturn(achievement1);
    when(achievementService.updateAchievement(any(Achievement.class), any(Integer.class))).thenReturn(achievement1);

    mockMvc.perform(put(BASE_URL + "/{id}", TEST_ACHIEVEMENT_ID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(achievement1))).andExpect(status().isOk())
        .andExpect(result -> {
            String content = result.getResponse().getContentAsString();
            objectMapper.readValue(content, Achievement.class);
            assertEquals("UPDATED", achievement1.getName());
            assertEquals(2, achievement1.getNumGamesPlays());
        });
  }

  @Test
  @WithMockUser("admin")
  void shouldDeleteAchievement() throws Exception {
    when(this.achievementService.findAchievementById(TEST_ACHIEVEMENT_ID)).thenReturn(achievement1);

    doNothing().when(this.achievementService).deleteAchievement(TEST_ACHIEVEMENT_ID);
    mockMvc.perform(delete(BASE_URL + "/{id}", TEST_ACHIEVEMENT_ID).with(csrf()))
        .andExpect(status().isOk());

  }

  @Test
  @WithMockUser("admin")
  void shouldReturnNotFoundAchievement() throws Exception {
    when(this.achievementService.findAchievementById(TEST_ACHIEVEMENT_ID)).thenThrow(ResourceNotFoundException.class);
    mockMvc.perform(get(BASE_URL + "/{id}", TEST_ACHIEVEMENT_ID)).andExpect(status().isNotFound());
  }

}
