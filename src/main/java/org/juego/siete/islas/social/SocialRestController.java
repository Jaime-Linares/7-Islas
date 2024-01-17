package org.juego.siete.islas.social;

import java.net.URISyntaxException;
import java.util.List;

import org.juego.siete.islas.auth.payload.response.MessageResponse;
import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.player.Player;
import org.juego.siete.islas.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/social")
@Tag(name = "Friends", description = "API for the management of Friends")
@SecurityRequirement(name = "bearerAuth")
public class SocialRestController {

    private final SocialService socialService;
    private final UserService userService;

    @Autowired
    public SocialRestController(SocialService ss, UserService us) {
        this.socialService = ss;
        this.userService = us;
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<List<Social>> findFriendsById(@PathVariable("userId") int id) {
        return new ResponseEntity<>(socialService.findFriendsByPlayerId(id), HttpStatus.OK);
    }

    @GetMapping(value = "/{userId}/connected")
    public ResponseEntity<List<Player>> findConnectedFriends(@PathVariable("userId") int id) {
        return new ResponseEntity<>(socialService.findConnectedFriends(id), HttpStatus.OK);
    }

    @GetMapping(value = "/{user2Id}/friendship")
    public ResponseEntity<Social> findFriendshipByUsersId(@PathVariable("user2Id") int user2Id) {
        return new ResponseEntity<>(
                socialService.findFriendship(userService.findCurrentUser().getId(), user2Id), HttpStatus.OK);
    }

    @GetMapping(value = "/{userId}/requests")
    public ResponseEntity<List<Social>> findFriendRequestsByUserId(@PathVariable("userId") int id) {
        return new ResponseEntity<>(socialService.friendRequestsByPlayerId(id), HttpStatus.OK);
    }

    @PostMapping(value = "/{username}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Social> createFriendRequest(@PathVariable("username") String username)
            throws URISyntaxException {
        Social savedFriend = this.socialService
                .createFriend(userService.findUserByUsername(username).orElse(null).getId());
        return new ResponseEntity<>(savedFriend, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{socialId}/acceptRequest")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Social> acceptFriendRequest(@PathVariable("socialId") int socialId) {
        this.socialService.acceptFriend(socialId);
        return new ResponseEntity<>(this.socialService.acceptFriend(socialId), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{socialId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> delete(@PathVariable("socialId") Integer socialId) {
        socialService.deleteFriend(socialId);
        return new ResponseEntity<>(new MessageResponse("Friend deleted!"), HttpStatus.OK);
    }

    @GetMapping(value = "/{username}/invitations")
    public ResponseEntity<List<SocialDTO>> getInvitationsByUserId(@PathVariable("username") String username) {
        return new ResponseEntity<>((List<SocialDTO>) socialService.getInvitations(username), HttpStatus.OK);
    }

    @PutMapping(value = "/{username}/invite")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<SocialDTO>> inviteFriendAsPlayer(@PathVariable("username") String username) {
        return new ResponseEntity<>(socialService.inviteToGameAsPlayer(username), HttpStatus.OK);
    }

    @PutMapping(value = "/{username}/inviteAsSpectator")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<SocialDTO>> inviteFriendAsSpectator(@PathVariable("username") String username) {
        return new ResponseEntity<>(socialService.inviteToGameAsSpectator(username), HttpStatus.OK);
    }

    @PutMapping(value = "/{socialId}/acceptInvitation")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Game> acceptInvitation(@PathVariable("socialId") int socialId) {
        return new ResponseEntity<>(socialService.acceptInvitation(socialService.findById(socialId)), HttpStatus.OK);
    }

    @PutMapping(value = "/{socialId}/rejectInvitation")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Social> rejectInvitation(@PathVariable("socialId") int socialId) {
        return new ResponseEntity<>(socialService.rejectInvitation(socialService.findById(socialId)), HttpStatus.OK);
    }
}
