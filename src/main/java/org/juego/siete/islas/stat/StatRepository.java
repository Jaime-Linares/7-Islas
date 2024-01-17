package org.juego.siete.islas.stat;

import java.util.List;

import org.juego.siete.islas.player.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface StatRepository extends CrudRepository<Stat,Integer> {

    @Query("SELECT p.stats FROM Player p WHERE p.id = :playerId")
    public Stat findStatByPlayerId(Integer playerId);
    
    @Query("SELECT p FROM Player p ORDER BY p.stats.numGamesWinned DESC")
    public List<Player> findRankingByGamesWon();

    @Query("SELECT p FROM Player p ORDER BY p.stats.averageScore DESC")
    public List<Player> findRankingByAverageScore();

}
