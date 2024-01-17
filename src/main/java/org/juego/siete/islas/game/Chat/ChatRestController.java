package org.juego.siete.islas.game.Chat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("api/v1/games/chat")
@Tag(name = "Chats", description = "API for the management of game chats")
@SecurityRequirement(name = "bearerAuth")
public class ChatRestController {

    private ChatService chatService;

    @Autowired
    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }


    @GetMapping(value = "/{gameId}")
    public ResponseEntity<List<Chat>> findChatByGameId(@PathVariable("gameId") Integer gameId) {
        List<Chat> res = chatService.findChatFromGame(gameId);
        return new ResponseEntity<List<Chat>>(res, HttpStatus.OK);
    }

    @PostMapping(value = "/{message}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Chat> create(@PathVariable("message") String chat) throws Exception {
        Chat newchat = chatService.saveChat(chat);
        return new ResponseEntity<>(newchat, HttpStatus.CREATED);
    }

}
