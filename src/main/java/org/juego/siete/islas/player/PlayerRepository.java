package org.juego.siete.islas.player;
import java.util.List;
import java.util.Optional;

import org.juego.siete.islas.card.Card;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface PlayerRepository extends CrudRepository<Player,Integer> {

    @Query("SELECT DISTINCT p FROM Player p WHERE p.user.id = :userId")
	public Optional<Player> findByUser(int userId);


    @Query("SELECT p FROM Player p WHERE p.user.username = :username")
	public Optional<Player> findByUsername(String username);

    @Query("SELECT COUNT(p) FROM Player p")
	public Integer countAll();

    @Query("SELECT c FROM Card c WHERE c.player.id = :playerId")
    public List<Card> findCardsByPlayerId(int playerId);
    
}
