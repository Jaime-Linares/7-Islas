package org.juego.siete.islas.builders.gameBuilder;

import java.util.Collections;
import java.util.List;

import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.player.Player;
import org.juego.siete.islas.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TurnBuilderService {

    private final PlayerService playerService;

    @Autowired
    public TurnBuilderService(PlayerService playerService) {
        this.playerService = playerService;
    }


    public void distributionTurns(Game game) {
        List<Player> players = game.getPlayers();
        Collections.shuffle(players);
        // tras barajar, el primero sera el que empiece
        players.get(0).setIsTurn(true);
        // los demas tendran que esperar su turno
        for(int i=1; i<players.size(); i++) {
            players.get(i).setIsTurn(false);
        }
        // los actualizamos en la base de datos
        for(Player p: players) {
            playerService.updatePlayerTurn(p, p.getId());
        }
    }
    
}
