package org.juego.siete.islas.achievements;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.juego.siete.islas.user.User;

import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.game.GameService;
import org.juego.siete.islas.player.PlayerService;
import org.juego.siete.islas.social.SocialService;
import org.juego.siete.islas.user.AuthoritiesService;
import org.juego.siete.islas.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AchievementServiceTest {
    @Autowired
    protected AchievementService achievementService;

    @Autowired
    protected PlayerService playerService;

    @Autowired
    protected AuthoritiesService authService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected GameService gameService;

    @Autowired
    protected SocialService socialService;

    @Test
    void shouldFindAll() {
        List<Achievement> achievementList = (List<Achievement>) achievementService.findAll();
        assertEquals(5, achievementList.size());
    }

    @Test
    void shouldFindAchievementById() {
        Achievement achievement = this.achievementService.findAchievementById(1);
        assertEquals("Nuevo pirata!!!", achievement.name);
        assertEquals(0, achievement.numGamesPlays);
        assertEquals(0, achievement.numGamesWinned);
    }

    @Test
    void shouldFindAchievementByName() {
        Optional<Achievement> achievement = this.achievementService.findAchievementByName("Nuevo pirata!!!");
        assertEquals(1, achievement.get().getId());
    }

    @Test
    void shouldNotFindAchievementByName() {
        assertThrows(ResourceNotFoundException.class, () -> this.achievementService.findAchievementByName("Antiguo pirata!!!"));
    }

    @Test
    void shouldFindAchievementByUserId() {
        User user = userService.findUserById(2);

        List<Achievement> achievements = this.achievementService.findAchievementByUserId(user.getId());
        assertEquals("Nuevo pirata!!!", achievements.get(0).name);
        assertEquals(0, achievements.get(0).numGamesPlays);
        assertEquals(0, achievements.get(0).numGamesWinned);
    }

    @Test
    @WithMockUser(username = "admin1", password = "4dm1n")
    void shouldDeleteAchievement() {
        List<Achievement> achievements = (List<Achievement>) this.achievementService.findAll();
        Achievement achievementToDelete = this.achievementService.findAchievementById(2);

        this.achievementService.deleteAchievement(achievementToDelete.getId());
        List<Achievement> newList = (List<Achievement>) this.achievementService.findAll();
        assertTrue(achievements.size() == newList.size() + 1);

    }

    @Test
    void shouldNotFindAchievementById() {
        assertThrows(ResourceNotFoundException.class, () -> this.achievementService.findAchievementById(100));
    }

    @Test
    void shouldNotFindAchievementInPlayer() {
        User user = userService.findUserById(2);
        Achievement achievement = achievementService.findAchievementById(2);

        assertThrows(ResourceNotFoundException.class, () -> {
            if (!this.achievementService.findAchievementByUserId(user.getId()).contains(achievement)) {
                throw new ResourceNotFoundException("Achievement not found for user with ID: " + user.getId());
            }
        });
    }

    @Test
    void shouldSaveAchievement() throws Exception {
        Achievement newAchievement = new Achievement();
        newAchievement.setName("nuevo");
        newAchievement.setDificulty("dificil");
        newAchievement.setNumGamesWinned(135);
        newAchievement.setNumGamesPlays(200);
        Achievement achievement = achievementService.saveAchievement(newAchievement);
        assertEquals(100, achievement.getId());
    }

    @Test
    void shouldNotSaveAchievement() throws Exception {
        Achievement newAchievement = new Achievement();
        newAchievement.setName("nuevo");
        newAchievement.setDificulty("dificil");
        newAchievement.setNumGamesWinned(250);
        newAchievement.setNumGamesPlays(200);
        assertThrows(Exception.class, () -> achievementService.saveAchievement(newAchievement));
    }

    @Test
    void shouldUpdateAchievement() throws Exception {
        Achievement achievementToUpdate = achievementService.findAchievementById(1);
        achievementToUpdate.setDificulty("dificil");
        Achievement updatedAchievement = achievementService.updateAchievement(achievementToUpdate, 1);
        assertEquals("dificil", updatedAchievement.getDificulty());
    }

    @Test
    void shouldNotUpdateAchievement() throws Exception {
        Achievement achievementToUpdate = achievementService.findAchievementById(1);
        achievementToUpdate.setNumGamesWinned(1);
        assertThrows(Exception.class, () -> achievementService.updateAchievement(achievementToUpdate, 1));
    }

    @Test
    void shouldDetectAchievementByName() {
        assertTrue(achievementService.existsAchievementByName("Nuevo pirata!!!"));
    }

    @Test
    void shouldNotDetectAchievementByName() {
        assertFalse(achievementService.existsAchievementByName("Antiguo pirata!!!"));
    }
}
