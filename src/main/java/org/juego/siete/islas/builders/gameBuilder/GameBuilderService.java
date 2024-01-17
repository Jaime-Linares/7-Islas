package org.juego.siete.islas.builders.gameBuilder;

import org.juego.siete.islas.card.Card;
import org.juego.siete.islas.card.CardService;
import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.island.Island;
import org.juego.siete.islas.island.IslandService;
import org.juego.siete.islas.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GameBuilderService {

    private final CardBuilderService cardBuilderService;
    private final DistributionBuilderService distributionBuilderService;
    private final IslandBuilderService islandBuilderService;
    private final TurnBuilderService turnBuilderService;
    private final CardService cardService;
    private final IslandService islandService;

    @Autowired
    public GameBuilderService(CardBuilderService cardBuilderService, DistributionBuilderService distributionBuilderService, 
    IslandBuilderService islandBuilderService, TurnBuilderService turnBuilderService,CardService cardService, 
    IslandService islandService, PlayerService playerService) {
        this.cardBuilderService = cardBuilderService;
        this.distributionBuilderService = distributionBuilderService;
        this.islandBuilderService = islandBuilderService;
        this.turnBuilderService = turnBuilderService;
        this.cardService = cardService;
        this.islandService = islandService;
    }


    public void startGame(Game game) {
        // Crea un mazo nuevo de cartas
        cardBuilderService.createCards(game);
        // Crea las islas de la partida
        islandBuilderService.createIslands(game);
        // Reparte las cartas del mazo creado anteriormente
        List<Card> deck = cardService.findCardsByGameId(game.getId());
        List<Island> islas = islandService.findIslandsByGameId(game.getId());
        distributionBuilderService.distributeCards(game, deck, islas);
        // Establece el sistema de turnos de la partida
        turnBuilderService.distributionTurns(game);
    }

}
