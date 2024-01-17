package org.juego.siete.islas.social;

import java.util.ArrayList;
import java.util.List;

import org.juego.siete.islas.exceptions.AccessDeniedException;
import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.game.GameService;
import org.juego.siete.islas.player.Player;
import org.juego.siete.islas.player.PlayerService;
import org.juego.siete.islas.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;


@Service
public class SocialService {

    private final PlayerService playerService;
    private final SocialRepository socialRepository;
    private final UserService userService;
    private final GameService gameService;

    @Autowired
    public SocialService(PlayerService ps, SocialRepository sc, UserService us, GameService gs){
        this.playerService=ps;
        this.socialRepository=sc;
        this.userService=us;
        this.gameService=gs;
    }


    public List<Social> findAll(){
        return (List<Social>) this.socialRepository.findAll();
    }

    public Social findById(Integer socialId){
        return this.socialRepository.findById(socialId)
            .orElseThrow(()->new ResourceNotFoundException("No se han encontrado estos amigos"));
    }
    
    public List<Social> findFriendsByPlayerId(Integer userId) throws DataAccessException{
        return (List<Social>) this.socialRepository.findFriendsByPlayerId(playerService.findPlayerByUser(userId).get().getId())
            .orElseThrow(()-> new ResourceNotFoundException("Social","ID",userId));
    }

    public List<Social> friendRequestsByPlayerId(Integer userId) throws DataAccessException{
        return (List<Social>) this.socialRepository.friendRequestsByPlayerId(playerService.findPlayerByUser(userId).get().getId())
            .orElseThrow(()-> new ResourceNotFoundException("Social","ID",userId));
    }

    public Social findFriendship(Integer userId1, Integer userId2)throws DataAccessException {
		return this.socialRepository.friendship(playerService.findPlayerByUser(userId1).get().getId(), 
                                                playerService.findPlayerByUser(userId2).get().getId())
            .orElseThrow(()->new ResourceNotFoundException("You are not friend with player this player"));
	}

    @Transactional
    public void deleteFriend(Integer socialId) throws DataAccessException {
		socialRepository.deleteById(socialId);
	}

    public Social createFriend(Integer userId2) {
        if(socialRepository.areFriends(playerService.findPlayerByUser(userService.findCurrentUser().getId()).get().getId(), 
                                       playerService.findPlayerByUser(userId2).get().getId())){
            throw new AccessDeniedException("You are already friends");
        }
        Social newFriend = new Social();
		newFriend.setPlayer1(playerService.findPlayerByUser(userService.findCurrentUser().getId()).get());
        newFriend.setPlayer2(playerService.findPlayerByUser(userId2).get());
        newFriend.invitation=null;
        newFriend.accepted = false;
        newFriend.invitation_as=null;
        socialRepository.save(newFriend);
        return newFriend;
    }

    @Transactional
	public Social acceptFriend(Integer newFriendshipId) throws DataAccessException {
        Social acceptedFriend = this.socialRepository.findById(newFriendshipId)
            .orElseThrow(()->new ResourceNotFoundException("Friendship not found"));
        if(acceptedFriend.accepted){
            throw new AccessDeniedException("They are friends");
        }
        acceptedFriend.setAccepted(true);
		socialRepository.save(acceptedFriend);
		return acceptedFriend;
	}

    public List<SocialDTO> getInvitations(String playerInvitedUsername) throws ResourceNotFoundException {
        List<Social> invitations =(List<Social>) this.socialRepository.getInvitations(userService.findPlayerByUsername(playerInvitedUsername).getId()).get();
        List<SocialDTO> res = new ArrayList<>();
        for(Social invitation:invitations){
            Integer inviterId = invitation.invitation ? invitation.getPlayer2().getUser().getId() : invitation.getPlayer1().getUser().getId();
            SocialDTO socialdto=new SocialDTO();
            socialdto.setSocial(invitation);
            Integer gameId=gameService.findRecentGameByPlayerIdForInvitations(inviterId);
            socialdto.setGameInvitedId(gameId);
            res.add(socialdto);
        }
        return res;
    }

    @Transactional
    public List<SocialDTO> inviteToGameAsPlayer(String playerInvitedUsername){
        Integer playerInvitedId = userService.findPlayerByUsername(playerInvitedUsername).getId();
        Integer playerInvitedUserId = userService.findUserByUsername(playerInvitedUsername).orElse(null).getId();
        Integer actualPlayerId = playerService.findPlayerByUser(userService.findCurrentUser().getId()).get().getId();
        if(socialRepository.areFriends(playerInvitedId, actualPlayerId)){
            Social friendship = findFriendship(userService.findCurrentUser().getId(), playerInvitedUserId);
            friendship.invitation_as=false;
            if(friendship.getPlayer1().getId()==actualPlayerId){
                friendship.invitation=false;
            } else {
                friendship.invitation=true;
            }
            socialRepository.save(friendship);
        } else {
            throw new ResourceNotFoundException("They are not friends");
        }
        return getInvitations(playerInvitedUsername);
    }

        @Transactional
    public List<SocialDTO> inviteToGameAsSpectator(String playerInvitedUsername){
        Integer playerInvitedId = userService.findPlayerByUsername(playerInvitedUsername).getId();
        Integer playerInvitedUserId = userService.findUserByUsername(playerInvitedUsername).orElse(null).getId();
        Integer actualPlayerId = playerService.findPlayerByUser(userService.findCurrentUser().getId()).get().getId();
        if(socialRepository.areFriends(playerInvitedId, actualPlayerId)){
            Social friendship = findFriendship(userService.findCurrentUser().getId(), playerInvitedUserId);
            friendship.invitation_as=true;
            if(friendship.getPlayer1().getId()==actualPlayerId){
                friendship.invitation=false;
            } else {
                friendship.invitation=true;
            }
            socialRepository.save(friendship);
        } else {
            throw new ResourceNotFoundException("They are not friends");
        }
        return getInvitations(playerInvitedUsername);
    }

    @Transactional
    public Game acceptInvitation(Social Friend) throws DataAccessException{
        Social invitationAccepted = Friend;
        Integer gameId;
        if(Friend.invitation==true){
            gameId = gameService.findRecentGameByPlayerIdForInvitations(Friend.getPlayer2().getUser().getId());
        }else{
            gameId = gameService.findRecentGameByPlayerIdForInvitations(Friend.getPlayer1().getUser().getId());
        }
        Game game = gameService.findGameById(gameId);
        if(game == null){
            throw new AccessDeniedException("You cannot access that game");
        }
        if(Friend.invitation==true){
            if(Friend.invitation_as){
                List<Player> gamers = game.getPlayers();
                Boolean friendsWithAll = true;
                for(Player p : gamers){
                    if(findFriendship(p.getUser().getId(), Friend.getPlayer1().getUser().getId()).getId() == null){
                        friendsWithAll = false;
                    }
                }
                if(friendsWithAll){
                    gameService.invitationByCodeSpectators(game.getCode());
                }else{
                    throw new AccessDeniedException("He is not friends with all players");
                }
            }else{
                gameService.invitationByCode(game.getCode());
            }
        }else if(Friend.invitation==false){
            if(Friend.invitation_as){
                List<Player> gamers = game.getPlayers();
                Boolean friendsWithAll = true;
                for(Player p : gamers){
                    if(!socialRepository.areFriends(p.getId(), Friend.getPlayer2().getId())){
                        friendsWithAll = false;
                    }
                }
                if(friendsWithAll){
                    gameService.invitationByCodeSpectators(game.getCode());
                }else{
                    throw new AccessDeniedException("He is not friends with all players");
                }
            }else{
                gameService.invitationByCode(game.getCode());
            }
        }
        invitationAccepted.invitation=null;
        invitationAccepted.invitation_as=null;
        socialRepository.save(invitationAccepted);
        return game;
    }

    @Transactional
    public Social rejectInvitation(Social Friend) throws DataAccessException{
        Social invitationAccepted = Friend;
        invitationAccepted.invitation=null;
        invitationAccepted.invitation_as=null;
        socialRepository.save(invitationAccepted);
        return Friend;
    }

    @Transactional(readOnly = true)
    public List<Player> findConnectedFriends(Integer userId){
        Integer playerId = playerService.findPlayerByUser(userId).get().getId();
        List<Social> socials = (List<Social>)findFriendsByPlayerId(userId);
        List<Player> connectedFriends = new ArrayList<>();
        for(Social s: socials){
            if(s.getPlayer1().getId() == playerId){
                if(s.getPlayer2().getIsConnected()){
                    connectedFriends.add(s.getPlayer2());
                }
            }else{
                if(s.getPlayer1().getIsConnected()){
                    connectedFriends.add(s.getPlayer1());
                }
            }
        }
        return connectedFriends;
    }
}
