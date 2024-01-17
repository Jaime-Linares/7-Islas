package org.juego.siete.islas.chat;

import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.game.GameService;
import org.juego.siete.islas.game.Chat.Chat;
import org.juego.siete.islas.game.Chat.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ChatServiceTest {

    @Autowired
    protected ChatService chatService;

    @Autowired
    protected GameService gameService;

    @Test
    @WithMockUser(username = "player5", password = "0wn3r")
    void shouldFindChatFromGame() throws Exception {
        Game game = gameService.saveGame();
        gameService.startPlayGameById(game.getId());

        chatService.saveChat("hola");
        chatService.saveChat("hola");

        gameService.exitUserById(game.getId(), 7);

        List<Chat> chats = chatService.findChatFromGame(game.getId());
        assertEquals(2, chats.size());
    }

    @Test
    @WithMockUser(username = "player5", password = "0wn3r")
    void shouldCreateChat() throws Exception{
        Game game = gameService.saveGame();
        gameService.startPlayGameById(game.getId());
        Chat chat = chatService.saveChat("hola");

        assertEquals("hola", chat.getMessage());
    }
}
