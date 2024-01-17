package org.juego.siete.islas.achievements;

import java.util.List;
import java.util.Optional;

import org.juego.siete.islas.player.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface AchievementRepository extends CrudRepository<Achievement,Integer> {
    
    @Query("SELECT a FROM Player p INNER JOIN p.achievements a WHERE p.id = :playerId")
    public List<Achievement> findAchievementsByPlayerId(int playerId);  
    
    public Optional<Achievement> findAchievementByName(String name);

    public Boolean existsByName(String name);

    @Query("SELECT DISTINCT p FROM Player p JOIN p.achievements a WHERE a.id = :achievementId")
    public List<Player> findPlayersByAchievementId(Integer achievementId);

}
