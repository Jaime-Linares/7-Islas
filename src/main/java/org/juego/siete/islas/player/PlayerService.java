package org.juego.siete.islas.player;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.juego.siete.islas.user.User;
import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.achievements.Achievement;
import org.juego.siete.islas.achievements.AchievementRepository;
import org.juego.siete.islas.card.Card;
import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.game.GameRepository;
import org.juego.siete.islas.stat.Stat;
import org.juego.siete.islas.stat.StatRepository;
import org.juego.siete.islas.user.UserRepository;
import org.juego.siete.islas.user.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PlayerService {

	private final PlayerRepository playerRepository;
	private final UserRepository userRepository;
	private final UserService userService;
	private final GameRepository gameRepository;
	private final PasswordEncoder encoder;
	private final StatRepository statRepository;
	private final AchievementRepository achievementRepository;

	@Autowired
	public PlayerService(PlayerRepository playerRepository, UserRepository userRepository, UserService userService,
			GameRepository gameRepository, PasswordEncoder encoder, StatRepository statRepository, 
			AchievementRepository achievementRepository) {
		this.playerRepository = playerRepository;
		this.userRepository = userRepository;
		this.userService = userService;
		this.gameRepository = gameRepository;
		this.encoder = encoder;
		this.statRepository = statRepository;
		this.achievementRepository = achievementRepository;
	}


	@Transactional(readOnly = true)
	public Iterable<Player> findAll() throws DataAccessException {
		return playerRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Player findPlayerById(int id) throws DataAccessException {
		return this.playerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Player", "ID", id));
	}

	@Transactional(readOnly = true)
	public Optional<Player> findPlayerByUser(int userId) throws DataAccessException {
		return this.playerRepository.findByUser(userId);
	}

	@Transactional(readOnly = true)
	public Optional<Player> findPlayerByUsername(String username) throws DataAccessException {
		return this.playerRepository.findByUsername(username);
	}

	@Transactional(readOnly = true)
	public List<Card> findCardsByPlayerId(int playerID) {
		return this.playerRepository.findCardsByPlayerId(playerID);
	}

	@Transactional
	public Player savePlayer(Player player, Boolean update) throws DataAccessException {
		if (player.getIsConnected() == null) {
			player.setIsConnected(true);
		}
		if (!update) {
			Stat stat = createStat();
			statRepository.save(stat);
			player.setStats(stat);
			asignarLogros(player);
		}
		playerRepository.save(player);
		return player;
	}

	private Stat createStat() {
		Stat stat = new Stat();
		stat.setTimePlayed(LocalTime.of(0, 0, 0));
		stat.setTimeLongestGame(LocalTime.of(0, 0, 0));
		stat.setTimeShortestGame(LocalTime.of(0, 0, 0));
		stat.setNumGamesPlays(0);
		stat.setNumGamesWinned(0);
		stat.setAverageScore(0.0);
		stat.setAverageNumCardsEndGames(0.0);
		return stat;
	}

	private void asignarLogros(Player player) {
		List<Achievement> todosLosLogros = (List<Achievement>) achievementRepository.findAll();
		List<Achievement> logrosAsignados = new ArrayList<>();
		for(Achievement achievement: todosLosLogros) {
			if(achievement.getNumGamesPlays() == 0 && achievement.getNumGamesWinned() == 0) {
				logrosAsignados.add(achievement);
			}
		}
		player.setAchievements(logrosAsignados);
	}


	@Transactional
	public Player updatePlayerForAchievements(Player player, Integer playerId) {
		Player playerToUpdate = new Player();
		BeanUtils.copyProperties(player, playerToUpdate, "id");
		return playerRepository.save(player);
	} 

	@Transactional
	public Player updatePlayer(PlayerEditDTO player, int id) throws DataAccessException {
		// actualizacion del player
		Player playerToUpdate = findPlayerById(id);
		LocalDate registrationDate = playerToUpdate.getRegistrationDate();
		BeanUtils.copyProperties(player, playerToUpdate, "username", "password");
		playerToUpdate.setRegistrationDate(registrationDate);

		// actualizacion del user del player
		User userToUpdate = userService.findUserById(playerToUpdate.getUser().getId());
		if (!userToUpdate.getPassword().equals(player.getPassword())) { // si password ha cambiado, la codificamos
			player.setPassword(encoder.encode(player.getPassword()));
			BeanUtils.copyProperties(player, userToUpdate, "id");
		} else { // si password no ha cambiado, no la codificamos otra vez
			BeanUtils.copyProperties(player, userToUpdate, "firstName", "image", 
				"lastName", "birthdayDate", "email", "password");
		}
		userRepository.save(userToUpdate);

		return savePlayer(playerToUpdate, true);
	}

	@Transactional
	public Player updatePlayerTurn(Player player, Integer id) {
		Player playerToUpdate = findPlayerById(id);
		BeanUtils.copyProperties(player, playerToUpdate, "id");
		return savePlayer(playerToUpdate, true);
	}

	@Transactional
	public Player updatePlayerConnection(Integer id) {
		Player playerToUpdate = findPlayerById(id);
		playerToUpdate.isConnected = !playerToUpdate.isConnected;
		return savePlayer(playerToUpdate, true);
	}

	@Transactional
	public void deletePlayer(int id) throws DataAccessException {
		Player player = findPlayerById(id);
		Integer userId = player.getUser().getId();
		User user = findUserById(userId);
		// ponemos a null el jugador de la lista de jugadores de las partidas que haya jugado
		List<Game> games = gameRepository.findGamesByPlayerId(id);
		if (!games.isEmpty()) {
			for (Game game : games) {
				game.removePlayerById(id);
				game.removePlayerQuitterById(id);
				game.removePlayerSpectatorById(id);
				gameRepository.save(game);
			}
		}
		playerRepository.delete(player);
		userRepository.delete(user);
	}

	@Transactional(readOnly = true)
	public Map<String, Object> getNumberPlayers() {
		Map<String, Object> res = new HashMap<>();
		Integer totalPlayers = this.playerRepository.countAll();

		res.put("totalPlayers", totalPlayers);
		return res;
	}

	@Transactional(readOnly = true)
	public User findUserById(Integer id) {
		return userRepository.findUserById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
	}

}
