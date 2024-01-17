package org.juego.siete.islas.builders.gameBuilder;

import org.juego.siete.islas.card.Card;
import org.juego.siete.islas.card.CardService;
import org.juego.siete.islas.card.CardType;
import org.juego.siete.islas.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CardBuilderService {

    private final CardService cardService;

    @Autowired
    public CardBuilderService(CardService cardService) {
        this.cardService = cardService;
    }


    public void createCards(Game game) {
        List<Card> cards = new ArrayList<>();
        // Crear las 66 cartas
        // 27 doblones
        createDoblones(cards, game);
        // 3 calices, rubis, diamantes
        createCalizRubiDiamante(cards,game);
        // 4 collares, mapas y coronas
        createCollarMapaCorona(cards,game);
        // 6 revolveres, espadas y barriles
        createRevolverEspadaBarril(cards,game);

        cards.forEach(cardService::saveCard);
    }


    private void createRevolverEspadaBarril(List<Card> cards,Game game) {
        for (int i = 0; i < 6; i++) {
            Card revolver = new Card();
            revolver.setType(CardType.Revolver);
            revolver.setReversed(false);
            revolver.setSelected(false);
            revolver.setGame(game);
            cards.add(revolver);

            Card espada = new Card();
            espada.setType(CardType.Espada);
            espada.setReversed(false);
            espada.setSelected(false);
            espada.setGame(game);
            cards.add(espada);

            Card barril = new Card();
            barril.setType(CardType.BarrilDeRon);
            barril.setReversed(false);
            barril.setSelected(false);
            barril.setGame(game);
            cards.add(barril);
        }
    }

    private void createCollarMapaCorona(List<Card> cards,Game game) {
        for (int i = 0; i < 4; i++) {
            Card collar = new Card();
            collar.setType(CardType.Collar);
            collar.setReversed(false);
            collar.setSelected(false);
            collar.setGame(game);
            cards.add(collar);

            Card mapa = new Card();
            mapa.setType(CardType.MapaDelTesoro);
            mapa.setReversed(false);
            mapa.setSelected(false);
            mapa.setGame(game);
            cards.add(mapa);

            Card corona = new Card();
            corona.setType(CardType.Corona);
            corona.setReversed(false);
            corona.setSelected(false);
            corona.setGame(game);
            cards.add(corona);
        }
    }

    private void createCalizRubiDiamante(List<Card> cards,Game game) {
        for (int i = 0; i < 3; i++) {
            Card caliz = new Card();
            caliz.setType(CardType.Caliz);
            caliz.setReversed(false);
            caliz.setSelected(false);
            caliz.setGame(game);
            cards.add(caliz);

            Card rubi = new Card();
            rubi.setType(CardType.Rubi);
            rubi.setReversed(false);
            rubi.setSelected(false);
            rubi.setGame(game);
            cards.add(rubi);

            Card diamante = new Card();
            diamante.setType(CardType.Diamante);
            diamante.setReversed(false);
            diamante.setSelected(false);
            diamante.setGame(game);
            cards.add(diamante);
        }
    }

    private void createDoblones(List<Card> cards,Game game) {
        for (int i = 0; i < 27; i++) {
            Card doblon = new Card();
            doblon.setType(CardType.Doblon);
            doblon.setReversed(false);
            doblon.setSelected(false);
            doblon.setGame(game);
            cards.add(doblon);
        }
    }

}
