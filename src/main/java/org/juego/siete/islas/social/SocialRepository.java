package org.juego.siete.islas.social;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface SocialRepository extends CrudRepository<Social, Integer>{

    @Query("SELECT f FROM Social f WHERE f.player1.id = :playerId AND f.accepted=TRUE UNION SELECT f FROM Social f WHERE f.player2.id = :playerId AND f.accepted=TRUE")
	Optional<Iterable<Social>> findFriendsByPlayerId(Integer playerId);

    @Query("SELECT f FROM Social f WHERE f.player2.id= :playerId AND f.accepted = FALSE")
    Optional<Iterable<Social>> friendRequestsByPlayerId(Integer playerId); 

    @Query("SELECT EXISTS (SELECT f FROM Social f WHERE ((f.player1.id= :playerId1 AND f.player2.id= :playerId2) OR (f.player1.id= :playerId2 AND f.player2.id= :playerId1)) AND f.accepted=TRUE)")
    Boolean areFriends(Integer playerId1, Integer playerId2);

    @Query("SELECT f FROM Social f WHERE ((f.player1.id= :playerId1 AND f.player2.id= :playerId2) OR (f.player1.id= :playerId2 AND f.player2.id= :playerId1))")
    Optional<Social> friendship(Integer playerId1, Integer playerId2);

    @Query("SELECT f FROM Social f WHERE (f.player1.id= :invitedPlayerId AND f.invitation = TRUE) OR (f.player2.id= :invitedPlayerId AND f.invitation = FALSE)")
    Optional<Iterable<Social>> getInvitations(Integer invitedPlayerId);
}
