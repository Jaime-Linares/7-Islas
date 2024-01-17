package org.juego.siete.islas.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.juego.siete.islas.exceptions.AccessDeniedException;
import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.player.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthoritiesService authService;

    @Test
    @WithMockUser(username = "admin1", password = "4dm1n")
    void shouldFindCurrentUser() {
        User user = this.userService.findCurrentUser();
        assertEquals("admin1", user.getUsername());
    }

    @Test
    @WithMockUser(username = "userTest")
    void shouldNotFindCorrectCurrentUser() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findCurrentUser());
    }

    @Test
    void shouldNotFindAuthenticated() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findCurrentUser());
    }

    @Test
    void shouldFindAllUsers() {
        List<User> users = (List<User>) this.userService.findAll();
        assertEquals(7, users.size());
    }

    @Test
    void shouldFindUsersByUsername() {
        Optional<User> optionalUser = this.userService.findUserByUsername("admin1");
        assertTrue(optionalUser.isPresent());
        User user = optionalUser.get();
        assertEquals("admin1", user.getUsername());
    }

    @Test
    void shouldFindUsersByAuthority() {
        List<User> users = (List<User>) this.userService.findAllUserByAuthority("ADMIN");
        assertEquals(2, users.size());

        List<User> admins = (List<User>) this.userService.findAllUserByAuthority("PLAYER");
        assertEquals(5, admins.size());
    }

    @Test
    void shouldNotFindUserByIncorrectUsername() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findUserByUsername("usernamenotexists"));
    }

    @Test
    void shouldFindSinglePlayerByUsername() {
        Player player = this.userService.findPlayerByUsername("player1");
        assertEquals("player1", player.getUser().getUsername());
    }

    @Test
    void shouldNotFindSingleOwnerWithBadUsername() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findPlayerByUsername("badusername"));
    }

    @Test
    void shouldFindSingleOwnerByUserId() {
        Player player = this.userService.findPlayerByUserId(3);
        assertEquals("player2", player.getUser().getUsername());
    }

    @Test
    void shouldNotFindSingleUserOwnerWithBadUserId() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findPlayerByUserId(100));
    }

    @Test
    void shouldFindSingleUser() {
        User user = this.userService.findUserById(1);
        assertEquals("admin1", user.getUsername());
    }

    @Test
    void shouldNotFindSingleUserWithBadID() {
        assertThrows(ResourceNotFoundException.class, () -> this.userService.findUserById(100));
    }

    @Test
    void shouldExistUser() {
        assertEquals(true, this.userService.existsUserByUsername("admin1"));
    }

    @Test
    void shouldNotExistUser() {
        assertEquals(false, this.userService.existsUserByUsername("player10000"));
    }

    @Test
    @WithMockUser(username = "player1", password = "0wn3r")
    void playerCanUpdateOwnPlayer() {
        Optional<User> optionalUser = this.userService.findUserByUsername("player1");
        assertTrue(optionalUser.isPresent());
        User user = optionalUser.get();
        user.setUsername("Change");
        userService.updateUser(user, user.getId());
        user = this.userService.findUserById(user.getId());
        assertEquals("Change", user.getUsername());
    }

    @Test
    @Transactional
    @WithMockUser(username = "player2", password = "0wn3r")
    void playerCantUpdateOtherPlayer() {
        Optional<User> optionalUserToUpdate = this.userService.findUserByUsername("player1");
        assertTrue(optionalUserToUpdate.isPresent());
        User user = optionalUserToUpdate.get();
        assertThrows(AccessDeniedException.class, () -> this.userService.updateUser(user, user.getId()));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin1", password = "4dm1n")
    void adminCanUpdateOtherPlayer() {
        Optional<User> optionalUserToUpdate = userService.findUserByUsername("player1");
        assertTrue(optionalUserToUpdate.isPresent());
        User userToUpdate = optionalUserToUpdate.get();
        userToUpdate.setUsername("Change");
        userService.updateUser(userToUpdate, userToUpdate.getId());
        userToUpdate = this.userService.findUserById(userToUpdate.getId());
        assertEquals("Change", userToUpdate.getUsername());
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin1", password = "4dm1n")
    void adminCantUpdateOtherAdmin() {
        Optional<User> optionalAdminToUpdate = userService.findUserByUsername("admin2");
        assertTrue(optionalAdminToUpdate.isPresent());
        User user = optionalAdminToUpdate.get();
        assertThrows(AccessDeniedException.class, () -> this.userService.updateUser(user, user.getId()));
    }

    @Test
    @Transactional
    void shouldCreateUser() {
        int count = ((Collection<User>) this.userService.findAll()).size();

        User user = new User();
        user.setUsername("Sam");
        user.setPassword("password");
        user.setAuthority(authService.findByAuthority("ADMIN"));
        this.userService.saveUser(user);
        assertNotEquals(0, user.getId().longValue());
        assertNotNull(user.getId());

        int finalCount = ((Collection<User>) this.userService.findAll()).size();
        assertEquals(count + 1, finalCount);
    }

    @Test
    @Transactional
    @WithMockUser(username = "player1", password = "0wn3r")
    void playerCanDeleteOwnPlayer() {
        Integer firstCount = ((Collection<User>) userService.findAll()).size();
        Optional<User> optionalUserToDelete = userService.findUserByUsername("player1");
        assertTrue(optionalUserToDelete.isPresent());
        User userToDelete = optionalUserToDelete.get();
        userService.deleteUser(userToDelete.getId());
        int lastCount = ((Collection<User>) userService.findAll()).size();
        assertEquals(firstCount, lastCount + 1);
    }

    @Test
    @Transactional
    @WithMockUser(username = "player2", password = "0wn3r")
    void playerCantDeleteOtherPlayer() {
        Optional<User> optionalUser = userService.findUserByUsername("player1");
        assertTrue(optionalUser.isPresent());
        User user = optionalUser.get();
        System.out.println(user);
        assertThrows(AccessDeniedException.class, () -> this.userService.deleteUser(user.getId()));
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin1", password = "4dm1n")
    void adminCanDeleteOtherPlayer() {
        Integer firstCount = ((Collection<User>) userService.findAll()).size();
        Optional<User> optionalUserToDelete = userService.findUserByUsername("player1");
        assertTrue(optionalUserToDelete.isPresent());
        User userToDelete = optionalUserToDelete.get();
        userService.deleteUser(userToDelete.getId());
        int lastCount = ((Collection<User>) userService.findAll()).size();
        assertEquals(firstCount, lastCount + 1);
    }

    @Test
    @Transactional
    @WithMockUser(username = "admin1", password = "4dm1n")
    void adminCantDeleteOtherAdmin() {
        Optional<User> optionalAdmin = userService.findUserByUsername("admin2");
        assertTrue(optionalAdmin.isPresent());
        User user = optionalAdmin.get();
        assertThrows(AccessDeniedException.class, () -> this.userService.deleteUser(user.getId()));
    }

    @Test
    void shouldFindAllPagination(){
        Page<User> pageUser = userService.findAllPagination(Pageable.ofSize(3));
        assertEquals(pageUser.get().toList().size(), 3);
    }

    @Test
    void shouldFindAllUserByAuthorityPagination(){
        Page<User> pageUser = userService.findAllUserByAuthorityPagination("ADMIN", Pageable.ofSize(1));
        assertEquals(pageUser.get().toList().size(), 1);
    }
}
