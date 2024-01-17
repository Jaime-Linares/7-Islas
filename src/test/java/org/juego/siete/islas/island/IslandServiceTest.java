package org.juego.siete.islas.island;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.juego.siete.islas.card.Card;
import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.game.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IslandServiceTest {
    @Autowired
    protected IslandService islandService;

    @Autowired
    protected GameService gameService;

    @Test
    void shouldFindAll(){
        Island nuevaIsla1 = new Island();
        nuevaIsla1.setNumber(1);
        Island nuevaIsla2 = new Island();
        nuevaIsla2.setNumber(2);

        islandService.saveIsland(nuevaIsla1);
        islandService.saveIsland(nuevaIsla2);

        List<Island> islandList = (List<Island>)islandService.findAll();
        assertEquals(2, islandList.size());
    }

    @Test
    void shouldFindIslandById(){
        Island newIsland = new Island();
        newIsland.setGame(gameService.findGameById(2));
        newIsland.setNumber(2);
        newIsland.setId(100);
        islandService.saveIsland(newIsland);
        List<Island> islandList = (List<Island>)islandService.findAll();
        Island island = islandService.findIslandById(islandList.get(0).getId());
        assertSame(island.getId(), newIsland.getId());
    }

    @Test
    void shouldNotFindIslandById(){
        assertThrows(ResourceNotFoundException.class, ()->this.islandService.findIslandById(0));
    }

    @Test
    void shouldSaveIsland(){
        Island newIsland = new Island();
        newIsland.setNumber(1);

        Island savedIsland = islandService.saveIsland(newIsland);

        assertEquals(100, savedIsland.getId());
    }

    @Test
    void shouldFindCardsByIslandId(){
        Island newIsland = new Island();
        newIsland.setGame(gameService.findGameById(2));
        newIsland.setNumber(2);
        islandService.saveIsland(newIsland);
        List<Card> cards = islandService.findCardsByIslandId(0);
        assertEquals(0, cards.size());
    }

    @Test
    void shouldDeleteIsland(){
        Island newIsland = new Island();
        newIsland.setGame(gameService.findGameById(2));
        newIsland.setNumber(2);
        newIsland.setId(100);
        islandService.saveIsland(newIsland);
        List<Island> islandListCreated = (List<Island>)islandService.findAll();
        islandService.deleteIsland(100);
        List<Island> islandListDeleted = (List<Island>)islandService.findAll();
        assertEquals(islandListCreated.size()-1, islandListDeleted.size());
    }

    @Test
    void shouldFindIslandByGameId(){
        Island newIsland = new Island();
        newIsland.setGame(gameService.findGameById(2));
        newIsland.setNumber(2);
        newIsland.setId(100);
        islandService.saveIsland(newIsland);
        newIsland.setNumber(3);
        newIsland.setId(101);
        islandService.saveIsland(newIsland);
        List<Island> islands = islandService.findIslandsByGameId(2);
        assertEquals(islands.size(), 2);
    }
}
