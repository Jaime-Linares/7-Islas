package org.juego.siete.islas.achievements;

import java.util.List;
import java.util.Optional;

import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.player.Player;
import org.juego.siete.islas.player.PlayerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final PlayerService playerService;

    @Autowired
    public AchievementService (AchievementRepository achievementRepository, PlayerService playerService) {
        this.achievementRepository = achievementRepository;
        this.playerService = playerService;
    }


    @Transactional(readOnly = true)
    public Iterable<Achievement> findAll() {
        return achievementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Achievement findAchievementById(Integer id) {
        return achievementRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Achievement", "ID", id));
    }

    @Transactional(readOnly = true)
    public Optional<Achievement> findAchievementByName(String name) {
        Optional<Achievement> res = achievementRepository.findAchievementByName(name);
        if(!res.isPresent()) {
            throw new ResourceNotFoundException("Achievement with name", "name", name);
        }
        return res;
    }

    @Transactional(readOnly = true)
    public List<Achievement> findAchievementByUserId(int userId) {
        Optional<Player> player = playerService.findPlayerByUser(userId);
        if(!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", userId);
        }
        return achievementRepository.findAchievementsByPlayerId(player.get().getId());
    }

    @Transactional
    public Achievement saveAchievement(Achievement achievement) throws Exception {
        if(achievement.getNumGamesWinned() > achievement.getNumGamesPlays()) {
            throw new Exception("The minimum number of games won cannot be more than the number of games played.");
        }
        achievementRepository.save(achievement);
        asignarAJugadores(achievement);
        return achievement;
    }

    private void asignarAJugadores(Achievement achievement) {
        List<Player> players = (List<Player>) playerService.findAll();
        for(Player player: players) {
            if(player.getStats().getNumGamesPlays() >= achievement.getNumGamesPlays() && 
            player.getStats().getNumGamesWinned() >= achievement.getNumGamesWinned()) {
                List<Achievement> logrosPlayer = player.getAchievements();
                if(!logrosPlayer.contains(achievement)) {
                    logrosPlayer.add(achievement);
                    player.setAchievements(logrosPlayer);
                    playerService.updatePlayerForAchievements(player, player.getId()); 
                }
            }
        }
    }


    @Transactional
    public Achievement updateAchievement(Achievement achievement, Integer id) throws Exception {
        if(achievement.getNumGamesWinned() > achievement.getNumGamesPlays()) {
            throw new Exception("The minimum number of games won cannot be more than the number of games played.");
        }
        Achievement achievementToUpdate = findAchievementById(id);
        BeanUtils.copyProperties(achievement, achievementToUpdate, "id");
        // quitamos logro al player que ya no cumpla las restricciones
        eliminarLogrosAJugadores(achievementToUpdate, id);
        // añadimos logro a los players que lo cumplan
        return saveAchievement(achievementToUpdate);
    }

    private void eliminarLogrosAJugadores(Achievement achievementToUpdate, Integer achievementId) {
        List<Player> jugadoresConLogro = achievementRepository.findPlayersByAchievementId(achievementId);
        if(jugadoresConLogro.size() > 0) {
            for(Player player: jugadoresConLogro) {
                if(player.getStats().getNumGamesPlays() < achievementToUpdate.getNumGamesPlays() ||
                player.getStats().getNumGamesWinned() < achievementToUpdate.getNumGamesWinned()) {
                    List<Achievement> logrosJugador = player.getAchievements();
                    logrosJugador.remove(achievementToUpdate);
                    player.setAchievements(logrosJugador);
                }
            }
        }
    }

    @Transactional
    public void deleteAchievement(Integer id) {
        Achievement achievement = findAchievementById(id);
        // eliminamos el logro de la lista de logros de todos los jugadores
        List<Player> players = (List<Player>) playerService.findAll();
        for(Player player: players) {
            if(player.getAchievements().contains(achievement)) {
                List<Achievement> achievements = player.getAchievements();
                achievements.remove(achievement);
                player.setAchievements(achievements);
            }
        } 
        achievementRepository.delete(achievement);
    }
    
    @Transactional(readOnly = true)
    public Boolean existsAchievementByName(String name) {
		return achievementRepository.existsByName(name);
	}

}
