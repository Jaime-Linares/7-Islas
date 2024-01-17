package org.juego.siete.islas.game;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface GameRepository extends CrudRepository<Game, Integer> {

    List<Game> findAll();

    @Query("SELECT g FROM Game g WHERE g.startedAt = null")
    List<Game> findByStartedAtIsNull();

    @Query("SELECT g from Game g WHERE g.finishedAt = null and g.startedAt != null")
    List<Game> findByFinishAtIsNull();

    @Query("SELECT g from Game g WHERE g.finishedAt != null")
    List<Game> findByFinishAtIsNotNull();

    @Query("SELECT DISTINCT g FROM Game g JOIN g.players p LEFT JOIN g.quitters q LEFT JOIN g.spectators s WHERE p.id = :playerId OR q.id = :playerId OR s.id = :playerId ")
    List<Game> findGamesByPlayerId(@Param("playerId") Integer playerId);

    @Query("SELECT g FROM Game g JOIN g.players p WHERE (p.id = :playerId AND g.startedAt = Null)")
    List<Game> findGamesByPlayerIdNotStarted(Integer playerId);

    @Query("SELECT g FROM Game g JOIN g.players p WHERE (p.id = :playerId) ORDER BY g.createdAt DESC LIMIT 1")
    Game findMostRecentGameByPlayerId(Integer playerId);

    @Query("SELECT g FROM Game g WHERE g.code = :codigo")
    Optional<Game> findGameByCode(String codigo);

    @Query("SELECT g.id FROM Game g WHERE g.creator.id = :playerId ORDER BY g.createdAt DESC LIMIT 1")
    Integer findMostRecentGameByPlayerIdForInvitation(Integer playerId);

    @Query("SELECT g FROM Game g JOIN g.players p WHERE p.id= :playerId AND g.startedAt = Null")
    Optional<Game> findUnstartedGameByPlayerId(Integer playerId);

    @Query("SELECT g FROM Game g JOIN g.players p WHERE p.id= :playerId AND g.startedAt != Null AND g.finishedAt = Null")
    Optional<Game> findUnfinishedGameByPlayerId(Integer playerId);

    @Query("SELECT g FROM Game g JOIN g.players p WHERE (p.id= :playerId AND g.createdAt != Null AND g.startedAt != Null AND g.finishedAt != Null)")
    List<Game> findFinishedGameByPlayerId(Integer playerId);

}
