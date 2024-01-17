package org.juego.siete.islas.island;

import org.juego.siete.islas.card.Card;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface IslandRepository extends CrudRepository<Island, Integer> {

    @Query("SELECT i FROM Island i WHERE i.game.id = ?1")
    public List<Island> findIslandByGameId(Integer gameId);

    @Query("SELECT c FROM Card c WHERE c.island.id = :islandId")
    public List<Card> findCardsByIslandId(int islandId);

}
