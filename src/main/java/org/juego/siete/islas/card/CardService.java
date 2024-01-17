package org.juego.siete.islas.card;

import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.island.Island;
import org.juego.siete.islas.player.Player;
import org.juego.siete.islas.player.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


@Service
public class CardService {
    private final CardRepository cardRepository;
    private final PlayerService playerService;

    @Autowired
    public CardService(CardRepository cardRepository, PlayerService playerService) {
        this.cardRepository = cardRepository;
        this.playerService = playerService;
    }

    @Transactional(readOnly = true)
    public Iterable<Card> findAll() throws DataAccessException {
        return cardRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Card findCardById(int id) throws DataAccessException {
        return this.cardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Card", "ID", id));
    }

    @Transactional
    public Card saveCard(Card card) throws DataAccessException {
        cardRepository.save(card);
        return card;
    }

    @Transactional
    public void deleteCard(int id) throws DataAccessException {
        Card card = findCardById(id);
        cardRepository.delete(card);
    }

    @Transactional(readOnly = true)
    public List<Card> findCardsByGameId(Integer gameId) {
        return cardRepository.findCardsByGameId(gameId);
    }

    @Transactional
    public Card updateCard(Integer cardId, Integer playerId) {
        Card cardToUpdate = findCardById(cardId);
        // cogemos carta de la isla
        Island isla = cardToUpdate.getIsland();
        cardToUpdate.setIsland(null);
        // asignamos carta al jugador
        Player player = playerService.findPlayerById(playerId);
        cardToUpdate.setPlayer(player);
        // rellenamos la isla con la primer carta del mazo
        Card cartaParaIsla = cogerCartaMazo(cardToUpdate);
        if (cartaParaIsla != null) {
            cartaParaIsla.setIsland(isla);
            saveCard(cartaParaIsla);
        }
        // guardamos la carta
        return saveCard(cardToUpdate);
    }

    private Card cogerCartaMazo(Card cardUpdate) {
        Integer gameId = cardUpdate.getGame().getId();
        List<Card> cartasMazo = cardRepository.findCardsMazoByGameId(gameId, 7);
        Collections.shuffle(cartasMazo);
        return cartasMazo.isEmpty() ? null : cartasMazo.get(0);
    }

    @Transactional
    public Card updateSelectedCards(SelectedCardsDTO selectedCardsDTO, Integer cardId,
                                    Integer playerId) {
        Card cardToUpdate = updateCard(cardId, playerId);
        for (Card card : selectedCardsDTO.getSelectedCards()) {
            card.setPlayer(null);
            saveCard(card);
        }
        return cardToUpdate;
    }

    @Transactional
    public Card updateSelected(Integer cardId) {
        Card cardToUpdate = findCardById(cardId);
        cardToUpdate.setSelected(!cardToUpdate.selected);
        saveCard(cardToUpdate);
        return cardToUpdate;
    }
}
