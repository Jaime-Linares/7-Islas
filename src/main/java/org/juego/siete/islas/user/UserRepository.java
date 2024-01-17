package org.juego.siete.islas.user;

import java.util.Optional;

import org.juego.siete.islas.player.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends CrudRepository<User, String> {

	@Query("SELECT p FROM Player p WHERE p.user.username = :username")
	Optional<Player> findPlayerByUsername(String username);

	@Query("SELECT p FROM Player p WHERE p.user.id = :id")
	Optional<Player> findPlayerByUserId(int id);

	Optional<User> findUserByUsername(String username);

	Boolean existsByUsername(String username);

	Optional<User> findUserById(Integer id);

	@Query("SELECT u FROM User u WHERE u.authority.authority = :auth")
	Page<User> findAllUserByAuthorityPagination(@Param("auth") String authority, Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.authority.authority = :auth")
	Iterable<User> findAllUserByAuthority(String auth);

	@Query("SELECT u FROM User u")
    Page<User> findAllPagination(Pageable pageable);

	@Query("DELETE FROM Player p WHERE p.user.id = :userId")
	@Modifying
	void deletePlayerRelation(int userId);

	@Query("DELETE FROM User u WHERE u.id = :userId")
	void deleteUserById(Integer userId);

}
