package org.juego.siete.islas.social;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.juego.siete.islas.exceptions.AccessDeniedException;
import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.game.GameService;
import org.juego.siete.islas.player.Player;
import org.juego.siete.islas.player.PlayerService;
import org.juego.siete.islas.user.AuthoritiesService;
import org.juego.siete.islas.user.User;
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
class SocialServiceTest {
    @Autowired
    protected SocialService socialService;
    @Autowired
    protected PlayerService playerService;
    @Autowired
    protected AuthoritiesService authService;

    @Autowired
    protected GameService gameService;

    @Autowired
    protected UserService userService;

    @Test
    void shouldFindAll(){
        List<Social> socialList = socialService.findAll();

        assertEquals(5, socialList.size());
    }

        @Test
    void shouldFindFriendshipById() {
    Social existingFriendship = socialService.findById(1);
    assertNotNull(existingFriendship);
    }

    @Test
    void shouldFindFriendsByPlayerId() {
    List<Social> userFriends = socialService.findFriendsByPlayerId(2);
    assertNotNull(userFriends);
    }

    @Test
    void shouldntFindFriendsByPlayerId() {
        assertThrows(NoSuchElementException.class, () -> socialService.findFriendsByPlayerId(100000));
    }

    @Test
    void shouldFindFriendRequest() {
        List<Social> friendRequest = this.socialService.friendRequestsByPlayerId(4);
        assertEquals(1, friendRequest.size());
    }
    @Test
    void shouldNotFindFriendRequest() {
        assertThrows(ResourceNotFoundException.class, ()->this.socialService.friendRequestsByPlayerId(this.playerService.findPlayerById(100).getId()));
    }
    @Test
    @WithMockUser(username = "player1", password = "0wn3r")
    void shouldDeleteFriend(){
        List<Social> allList= this.socialService.findAll();
        Optional<User> user1 = userService.findUserByUsername("player1");
        Optional<User> user2 = userService.findUserByUsername("player2");
        assertTrue(user1.isPresent());
        assertTrue(user2.isPresent());
        Social socialToDelete = this.socialService.findFriendship(user1.get().getId(), user2.get().getId());
        this.socialService.deleteFriend(socialToDelete.getId());

        List<Social> newList= this.socialService.findAll();
        assertEquals(allList.size(), newList.size() + 1);
    }

    @Test
    void shouldFindFriendship(){
        Social friendship = this.socialService.findFriendship(2, 3);
        assertEquals(Social.class, friendship.getClass());
    }
    @Test
    void shouldNotFindFriendship(){
        assertThrows(ResourceNotFoundException.class, ()->this.socialService.findFriendship(7, 6));
    }
    @Test
    void shouldAcceptFriendRequest(){
        List<Social> friendRequest = this.socialService.friendRequestsByPlayerId(4);
        Social firstFriendRequest = friendRequest.get(0);
        socialService.acceptFriend(firstFriendRequest.getId());
        firstFriendRequest=socialService.findById(firstFriendRequest.getId());
        assertTrue(firstFriendRequest.accepted);
    }

    @Test
    void shouldNotAcceptFriend(){
        assertThrows(AccessDeniedException.class, ()-> this.socialService.acceptFriend(socialService.findFriendship(2, 3).getId()));
    }

    @Test
    @WithMockUser(username = "player4", password = "0wn3r")
    void shouldCreateFriend(){
        Optional<User> user3Optional = userService.findUserByUsername("player3");
        assertTrue(user3Optional.isPresent());
        socialService.createFriend(user3Optional.get().getId());
        Optional<User> user4Optional = userService.findUserByUsername("player4");
        assertTrue(user4Optional.isPresent());
        assertNotNull(socialService.findFriendship(user3Optional.get().getId(), user4Optional.get().getId()).getId());
    }

    @Test
    @WithMockUser(username = "player1", password = "0wn3r")
    void shouldNotCreateFriend(){
        Optional<User> userOptional = userService.findUserByUsername("player2");
        assertTrue(userOptional.isPresent());
        assertThrows(AccessDeniedException.class, ()-> this.socialService.createFriend(userOptional.get().getId()));
    }

    @Test
    @WithMockUser(username = "player1", password = "0wn3r")
    void shouldGetInvitations(){
        assertEquals(0, socialService.getInvitations("player1").size());
    }

    @Test
    @WithMockUser(username = "player4", password = "0wn3r")
    void shouldInviteToGameAsPlayer() {
    socialService.inviteToGameAsPlayer("player2");
        assertFalse(socialService.findFriendship(3, 6).getInvitation_as());
    }

    @Test
    @WithMockUser(username = "player4", password = "0wn3r")
    void shouldInviteToGameAsSpectator() {
    socialService.inviteToGameAsSpectator("player2");
    assertTrue(socialService.findFriendship(3, 6).getInvitation_as());
    }

    @Test
    @WithMockUser(username = "player4", password = "0wn3r")
    void shouldRejectInvitation() {
    socialService.inviteToGameAsSpectator("player2");
    socialService.rejectInvitation(socialService.findFriendship(3, 6));
    assertNull(socialService.findFriendship(3, 6).getInvitation());
    assertNull(socialService.findFriendship(3, 6).getInvitation_as());
    }

    @Test
    @WithMockUser(username = "player4", password = "0wn3r")
    void shouldFindConnectedFriends(){
        Player player = playerService.findPlayerById(2);
        player.setIsConnected(true);
        playerService.savePlayer(player,true);
        List<Player> players = socialService.findConnectedFriends(6);
        assertEquals(1, players.size());
    }
}
