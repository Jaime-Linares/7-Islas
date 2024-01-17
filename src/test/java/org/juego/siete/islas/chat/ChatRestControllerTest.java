package org.juego.siete.islas.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.juego.siete.islas.configuration.SecurityConfiguration;
import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.game.Chat.Chat;
import org.juego.siete.islas.game.Chat.ChatRestController;
import org.juego.siete.islas.game.Chat.ChatService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = ChatRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class ChatRestControllerTest {

  @MockBean
  ChatService chatService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private static final Integer TEST_GAME_ID = 1;
  private static final String BASE_URL = "/api/v1/games/chat";

  private final Game game1 = new Game();
  private final Chat chat = new Chat();

  @BeforeEach
  void setup() {

    chat.setId(1);
    chat.setGame(game1);
    chat.setMessage("Hola");
    chat.setUsername("usuario1");

    game1.setId(TEST_GAME_ID);
    game1.setCode("CDfg");
    game1.setCreatedAt(LocalDateTime.of(2024, 1, 2, 11, 32, 0));
    game1.setFinishedAt(null);

  }

  @Test
  @WithMockUser("admin")
  void shouldFindChatByGameId() throws Exception {
    when(this.chatService.findChatFromGame(TEST_GAME_ID)).thenReturn(List.of(chat));
    mockMvc.perform(get(BASE_URL + "/{gameId}", TEST_GAME_ID)).andExpect(status().isOk());
  }

  @Test
  @WithMockUser("admin")
  void shouldNotFindChatByGameId() throws Exception {
    when(this.chatService.findChatFromGame(1000)).thenThrow(ResourceNotFoundException.class);
    mockMvc.perform(get(BASE_URL + "/1000")).andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser()
  void shouldCreateChat() throws Exception {
    mockMvc.perform(post(BASE_URL + "/Hola").with(csrf()).contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(chat))).andExpect(status().isCreated());
  }

}
