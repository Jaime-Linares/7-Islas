package org.juego.siete.islas.card;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.juego.siete.islas.auth.payload.response.MessageResponse;
import org.juego.siete.islas.player.PlayerService;
import org.juego.siete.islas.util.RestPreconditions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;


@RestController
@RequestMapping("/api/v1/cards")
@Tag(name = "Cards", description = "API for the management of cards")
@SecurityRequirement(name = "bearerAuth")
public class CardRestController {

    private final CardService cardService;
    private final PlayerService playerService;

    @Autowired
    public CardRestController(CardService cardService, PlayerService playerService) {
        this.cardService = cardService;
        this.playerService = playerService;
    }


    @GetMapping
    public ResponseEntity<List<Card>> findAll() {
        return new ResponseEntity<>((List<Card>) cardService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/{cardId}")
    public ResponseEntity<Card> findById(@PathVariable("cardId") int id) {
        return new ResponseEntity<>(cardService.findCardById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/game/{gameId}" )
    public ResponseEntity<List<Card>> findCardsByGameId(@PathVariable("gameId") Integer gameId) {
        return new ResponseEntity<>(cardService.findCardsByGameId(gameId), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Card> create(@RequestBody @Valid Card card) throws URISyntaxException {
        Card newCard = new Card();
        BeanUtils.copyProperties(card, newCard, "id");
        Card savedCard = this.cardService.saveCard(newCard);
        return new ResponseEntity<>(savedCard, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{cardId}/player/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Card> updateCard(@PathVariable("cardId") int cardId,
    @PathVariable("playerId") int playerId){
        RestPreconditions.checkNotNull(cardService.findCardById(cardId),
            "Card", "ID", findById(cardId));
        RestPreconditions.checkNotNull(playerService.findPlayerById(playerId),
            "Player", "ID", playerService.findPlayerById(playerId));
        return new ResponseEntity<>(cardService.updateCard(cardId, playerId), HttpStatus.OK);
    }

    @PutMapping(value = "/{cardId}/player/{playerId}/selectedCards")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Card> updateSelectedCards(@RequestBody @Valid SelectedCardsDTO selectedCardsDTO,
    @PathVariable("cardId") int cardId, @PathVariable("playerId") int playerId) {
        RestPreconditions.checkNotNull(cardService.findCardById(cardId),
            "Card", "ID", findById(cardId));
        RestPreconditions.checkNotNull(playerService.findPlayerById(playerId),
            "Player", "ID", playerService.findPlayerById(playerId));
        for(Card card : selectedCardsDTO.getSelectedCards()) {
            RestPreconditions.checkNotNull(cardService.findCardById(card.getId()),
            "Card", "ID", findById(card.getId()));
        }
        return new ResponseEntity<>(cardService.updateSelectedCards(selectedCardsDTO, cardId, playerId), HttpStatus.OK);
    }

    @PutMapping(value = "/{cardId}/updateSelection")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Card> updateSelection(@PathVariable("cardId") int cardId) {
        RestPreconditions.checkNotNull(cardService.findCardById(cardId),
            "Card", "ID", findById(cardId));
        return new ResponseEntity<>(cardService.updateSelected(cardId), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> delete(@PathVariable("cardId") int id) {
        RestPreconditions.checkNotNull(cardService.findCardById(id), "Card", "ID", id);
        cardService.deleteCard(id);
        return new ResponseEntity<>(new MessageResponse("Card deleted!"), HttpStatus.OK);
    }

}
