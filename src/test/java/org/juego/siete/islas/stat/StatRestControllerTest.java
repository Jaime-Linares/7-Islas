package org.juego.siete.islas.stat;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.juego.siete.islas.configuration.SecurityConfiguration;
import org.juego.siete.islas.player.Player;
import org.juego.siete.islas.user.Authorities;
import org.juego.siete.islas.user.User;
import org.juego.siete.islas.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(value = StatRestController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class StatRestControllerTest {

    private static final Integer TEST_USER_ID = 2;
    private static final Integer TEST_PLAYER_ID = 1;
    private static final Integer TEST_STAT_ID = 1;
    private static final String BASE_URL = "/api/v1/stats";

    @MockBean
    StatService statService;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private Stat stat1;
    private Player player1;
    private User user1;

    @BeforeEach
    void setup() {
        stat1 = new Stat();
        stat1.setId(TEST_STAT_ID);
        stat1.setTimePlayed(LocalTime.of(8, 42, 34));
        stat1.setTimeLongestGame(LocalTime.of(1, 12, 43));
        stat1.setTimeShortestGame(LocalTime.of(0, 5, 30));
        stat1.setNumGamesPlays(20);
        stat1.setNumGamesWinned(10);
        stat1.setAverageScore(5.0);
        stat1.setAverageNumCardsEndGames(8.2);


        Authorities userAuth = new Authorities();
        userAuth.setId(1);
        userAuth.setAuthority("player");

        user1 = new User();
        user1.setId(TEST_USER_ID);
        user1.setUsername("usuario1");
        user1.setPassword("contrasenaUsuario1");
        user1.setAuthority(userAuth);

        player1 = new Player();
        player1.setId(TEST_PLAYER_ID);
        player1.setFirstName("Dani");
        player1.setLastName("Alonso");
        player1.setEmail("emailplayer1@gmail.com");
        player1.setBirthdayDate(LocalDate.of(2003, 1, 15));
        player1.setRegistrationDate(LocalDate.of(2013, 5, 16));
        player1.setUser(user1);
        player1.setStats(stat1);

    }

    @Test
    @WithMockUser()
    void shouldFindStatsByUserId() throws Exception {
        when(this.statService.findStatsByUserId(TEST_USER_ID)).thenReturn(stat1);
        when(this.userService.findUserById(TEST_USER_ID)).thenReturn(user1);
        mockMvc.perform(get(BASE_URL + "/user/{userId}", TEST_USER_ID))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser()
    void shouldFindRankingByAttribute() throws Exception {
        when(this.statService.findRankingByAtributte("Average Score")).thenReturn(List.of(player1));
        mockMvc.perform(get(BASE_URL + "/ranking/{atributo}", "Average Score")).andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("[?(@.id == 1)].firstName").value("Dani"));
    }

}
