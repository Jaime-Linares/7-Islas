package org.juego.siete.islas.game.Chat;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface ChatRepository extends CrudRepository<Chat,Integer>{

    @Query("SELECT c FROM Chat c WHERE c.game.id = :gameId")
    List<Chat> findMessagesByGame(Integer gameId);
    
}
