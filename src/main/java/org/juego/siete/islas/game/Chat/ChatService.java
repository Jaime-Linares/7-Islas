package org.juego.siete.islas.game.Chat;

import java.util.List;

import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.game.GameService;
import org.juego.siete.islas.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final GameService gameService;
    private final UserService userService;

    @Autowired
    public ChatService(ChatRepository chatRepository, GameService gameService, UserService userService) {
        this.chatRepository = chatRepository;
        this.gameService = gameService;
        this.userService = userService;
    }


    @Transactional(readOnly = true)
    public List<Chat> findChatFromGame(Integer gameId) {
        return chatRepository.findMessagesByGame(gameId);
    }

    @Transactional
    public Chat saveChat(String message) throws Exception {
        Chat newChat = new Chat();
        Game recentGame = gameService.findRecentGameByPlayerId();
        String player = userService.findCurrentUser().getUsername();

        if (recentGame.getStartedAt() != null) {
            newChat.setGame(recentGame);
            newChat.setUsername(player);
            newChat.setMessage(message);
            return chatRepository.save(newChat);
        } else {
            throw new Exception("No se pudo encontrar el juego.");
        }
    }

}
