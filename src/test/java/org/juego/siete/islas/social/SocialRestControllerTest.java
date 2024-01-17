package org.juego.siete.islas.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.juego.siete.islas.configuration.SecurityConfiguration;
import org.juego.siete.islas.player.Player;
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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = SocialRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class SocialRestControllerTest {

  private static final Integer TEST_USER_ID = 2;
  private static final Integer TEST_PLAYER_ID = 1;
  private static final Integer TEST_SOCIAL_ID = 1;
  private static final String BASE_URL = "/api/v1/social";

  @MockBean
  SocialService socialService;

  @MockBean
  UserService userService;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  private Social social;
  private Player player1;
  private Player player2;
  private Player player3;

  private User user1;
    private User user3;

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

      User user2 = new User();
    user2.setId(3);
    user2.setUsername("usuario2");
    user2.setPassword("contrasenaUsuario2");
    user2.setAuthority(userAuth);

    user3 = new User();
    user3.setId(4);
    user3.setUsername("usuario3");
    user3.setPassword("contrasenaUsuario3");
    user3.setAuthority(userAuth);

    player2 = new Player();
    player2.setId(2);
    player2.setFirstName("Juan");
    player2.setLastName("Perez");
    player2.setEmail("emailplayer2@gmail.com");
    player2.setBirthdayDate(LocalDate.of(2003, 5, 3));
    player2.setRegistrationDate(LocalDate.of(2013, 7, 10));
    player2.setIsConnected(true);
    player2.setUser(user2);

    player3 = new Player();
    player3.setId(3);
    player3.setFirstName("Antonio");
    player3.setLastName("Dominguez");
    player3.setEmail("emailplayer3@gmail.com");
    player3.setBirthdayDate(LocalDate.of(2003, 5, 3));
    player3.setRegistrationDate(LocalDate.of(2013, 7, 10));
    player3.setIsConnected(true);
    player3.setUser(user3);

    social = new Social();
    social.setId(TEST_SOCIAL_ID);
    social.setPlayer1(player1);
    social.setPlayer2(player2);
    social.setAccepted(true);
  }

  @Test
  @WithMockUser(username = "player1", password = "0wn3r")
  void shouldFindFriendsById() throws Exception {
    when(this.socialService.findFriendsByPlayerId(TEST_PLAYER_ID)).thenReturn((List.of(social)));
    mockMvc.perform(get(BASE_URL + "/{userId}", TEST_PLAYER_ID)).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  @WithMockUser(username = "player1", password = "0wn3r")
  void shouldFindConnectedFriends() throws Exception {
    when(this.socialService.findConnectedFriends(TEST_PLAYER_ID)).thenReturn(List.of(social.getPlayer2()));
    mockMvc.perform(get(BASE_URL + "/{userId}/connected", TEST_PLAYER_ID)).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  @WithMockUser(username = "player1", password = "0wn3r")
  void shouldFindFriendshipByUsersId() throws Exception {
    when(this.socialService.findFriendship(player1.getUser().getId(), player2.getUser().getId())).thenReturn(social);
    when(this.userService.findCurrentUser()).thenReturn(user1);
      mockMvc.perform(get(BASE_URL + "/{user2Id}/friendship", player2.getUser().getId()))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.accepted").value(true))
          .andExpect(jsonPath("$.player1.user.username").value(player1.getUser().getUsername()))
          .andExpect(jsonPath("$.player2.user.username").value(player2.getUser().getUsername()));
  }

  @Test
  @WithMockUser(username = "player1", password = "0wn3r")
  void shouldFindFriendRequestByUserId() throws Exception {
    when(this.socialService.friendRequestsByPlayerId(TEST_PLAYER_ID)).thenReturn(List.of(social));
    mockMvc.perform(get(BASE_URL + "/{userId}/requests", TEST_PLAYER_ID)).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "player1", password = "0wn3r")
  void shouldCreateFriendRequest() throws Exception {
      when(this.userService.findUserByUsername(player3.getUser().getUsername())).thenReturn(Optional.ofNullable(user3));
    mockMvc.perform(post(BASE_URL + "/{username}", player3.getUser().getUsername()).with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(social))).andExpect(status().isCreated());
  }

}
