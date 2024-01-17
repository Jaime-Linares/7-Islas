package org.juego.siete.islas.card;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface CardRepository  extends CrudRepository<Card, Integer> {

    @Query("SELECT c FROM Card c WHERE c.game.id = ?1")
    public List<Card> findCardsByGameId(Integer gameId);

    @Query("SELECT c FROM Card c WHERE c.game.id = :gameId AND c.island.number = :mazo")
    public List<Card> findCardsMazoByGameId(Integer gameId, Integer mazo);

}
