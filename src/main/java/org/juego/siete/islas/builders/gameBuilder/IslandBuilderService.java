package org.juego.siete.islas.builders.gameBuilder;

import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.island.Island;
import org.juego.siete.islas.island.IslandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class IslandBuilderService {

    private final IslandService islandService;

    @Autowired
    public IslandBuilderService(IslandService islandService) {
        this.islandService = islandService;
    }

    
    public void createIslands(Game game) {
        List<Island> islas = new ArrayList<>();
        createIslas(islas, game);
        islas.forEach(islandService::saveIsland);
    }

    private static void createIslas(List<Island> islas, Game game) {
        for (int i = 0; i < 7; i++) {
            Island isla = new Island();
            isla.setNumber(i + 1);
            isla.setGame(game);
            islas.add(isla);
            islas.add(isla);
        }
    }

}
