package org.juego.siete.islas.card;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.game.GameService;
import org.juego.siete.islas.island.Island;
import org.juego.siete.islas.island.IslandService;
import org.juego.siete.islas.player.PlayerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CardServiceTest {
    @Autowired
    protected IslandService islandService;

    @Autowired
    protected PlayerService playerService;

    @Autowired
    protected CardService cardService;

    @Autowired
    protected GameService gameService;

    @Test
    void findAll() {
        Card carta1 = new Card();
        carta1.setType(CardType.Caliz);
        carta1.setReversed(false);
        carta1.setSelected(true);

        Card carta2 = new Card();
        carta2.setType(CardType.Revolver);
        carta2.setReversed(false);
        carta2.setSelected(true);

        cardService.saveCard(carta1);
        cardService.saveCard(carta2);

        List<Card> cards = (List<Card>) cardService.findAll();

        assertEquals(2, cards.size());
    }

    @Test
    void findCardById() {
        Island newIsland = new Island();
        newIsland.setGame(gameService.findGameById(2));
        newIsland.setNumber(2);
        newIsland.setId(100);
        islandService.saveIsland(newIsland);

        Card card = new Card();
        card.setGame(gameService.findGameById(2));
        card.setIsland(newIsland);
        card.setPlayer(playerService.findPlayerById(2));
        card.setReversed(false);
        card.setSelected(true);
        card.setType(CardType.BarrilDeRon);

        cardService.saveCard(card);

        Card createdCard = cardService.findCardById(101);
        assertEquals(101, createdCard.getId());
    }

    @Test
    void shouldntfindCardById() {
        assertThrows(ResourceNotFoundException.class, () -> this.cardService.findCardById(10000));
    }

    @Test
    void saveCard() {
        Island newIsland = new Island();
        newIsland.setGame(gameService.findGameById(2));
        newIsland.setNumber(2);
        newIsland.setId(100);
        islandService.saveIsland(newIsland);

        Card card = new Card();
        card.setGame(gameService.findGameById(2));
        card.setIsland(newIsland);
        card.setPlayer(playerService.findPlayerById(2));
        card.setReversed(false);
        card.setSelected(true);
        card.setType(CardType.BarrilDeRon);

        cardService.saveCard(card);

        Card createdCard = cardService.findCardById(101);
        assertEquals(101, createdCard.getId());
    }

    @Test
    void deleteCard() {
        Island newIsland = new Island();
        newIsland.setGame(gameService.findGameById(2));
        newIsland.setNumber(2);
        newIsland.setId(100);
        islandService.saveIsland(newIsland);

        Card card = new Card();
        card.setGame(gameService.findGameById(2));
        card.setIsland(newIsland);
        card.setPlayer(playerService.findPlayerById(2));
        card.setReversed(false);
        card.setSelected(true);
        card.setType(CardType.BarrilDeRon);

        cardService.saveCard(card);

        List<Card> cardsCreated = (List<Card>) cardService.findAll();
        cardService.deleteCard(cardsCreated.get(0).getId());
        List<Card> cardsDeleted = (List<Card>) cardService.findAll();
        assertEquals(cardsCreated.size() - 1, cardsDeleted.size());
    }

    @Test
    void findCardByGameId() {
        Card card = new Card();
        card.setGame(gameService.findGameById(2));
        card.setReversed(false);
        card.setSelected(true);
        card.setType(CardType.BarrilDeRon);

        cardService.saveCard(card);

        assertEquals(1, cardService.findCardsByGameId(2).size());
    }

    @Test
    void shouldUpdateCard() {
        Island newIsland = new Island();
        newIsland.setGame(gameService.findGameById(2));
        newIsland.setNumber(2);
        newIsland.setId(100);
        islandService.saveIsland(newIsland);

        Card card = new Card();
        card.setGame(gameService.findGameById(2));
        card.setIsland(newIsland);
        card.setPlayer(playerService.findPlayerById(2));
        card.setReversed(false);
        card.setSelected(true);
        card.setType(CardType.BarrilDeRon);

        cardService.saveCard(card);

        Card createdCard = cardService.findCardById(101);

        card.setSelected(false);
        cardService.updateCard(createdCard.getId(), 2);
    }

    @Test
    void shouldUpdateSelectedCard() {
        Island newIsland = new Island();
        newIsland.setGame(gameService.findGameById(2));
        newIsland.setNumber(2);
        newIsland.setId(100);
        islandService.saveIsland(newIsland);

        Card card = new Card();
        card.setGame(gameService.findGameById(2));
        card.setIsland(newIsland);
        card.setPlayer(playerService.findPlayerById(2));
        card.setReversed(false);
        card.setSelected(true);
        card.setType(CardType.BarrilDeRon);

        cardService.saveCard(card);
        Card createdCard = cardService.findCardById(101);

        cardService.updateSelected(createdCard.getId());
        Card updateCard = cardService.findCardById(101);

        assertEquals(createdCard.getSelected(), !updateCard.getSelected());
    }

    @Test
    void shouldcogerCartaMazo() {
        Island newIsland = new Island();
        newIsland.setGame(gameService.findGameById(2));
        newIsland.setNumber(2);
        newIsland.setId(100);
        islandService.saveIsland(newIsland);

        Card card = new Card();
        card.setGame(gameService.findGameById(2));
        card.setIsland(newIsland);
        card.setPlayer(playerService.findPlayerById(2));
        card.setReversed(false);
        card.setSelected(true);
        card.setType(CardType.BarrilDeRon);

        cardService.saveCard(card);

        List<Card> cards = cardService.findCardsByGameId(2);
        SelectedCardsDTO selectedCardsDTO = new SelectedCardsDTO(cards);
        cardService.updateSelectedCards(selectedCardsDTO, 101, 2);

        List<Card> updatedCards = cardService.findCardsByGameId(2);
        for (Card updatedCard : updatedCards) {
            Assertions.assertNull(updatedCard.getPlayer(), "El jugador de la carta seleccionada no se estableció como null.");
        }
    }

    @Test
    void shouldUpdateSelected() {
        Island newIsland = new Island();
        newIsland.setGame(gameService.findGameById(2));
        newIsland.setNumber(2);
        newIsland.setId(100);
        islandService.saveIsland(newIsland);

        Card card = new Card();
        card.setGame(gameService.findGameById(2));
        card.setIsland(newIsland);
        card.setPlayer(playerService.findPlayerById(2));
        card.setReversed(false);
        card.setSelected(true);
        card.setType(CardType.BarrilDeRon);
        cardService.saveCard(card);

        cardService.updateSelected(card.getId());
        Card newCard = cardService.findCardById(card.getId());
        assertEquals(card.getSelected(), !newCard.getSelected());
    }
}
