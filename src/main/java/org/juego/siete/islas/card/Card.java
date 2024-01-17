package org.juego.siete.islas.card;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.island.Island;
import org.juego.siete.islas.model.BaseEntity;
import org.juego.siete.islas.player.Player;


@Entity
@Table(name = "cards")
@Setter
@Getter
public class Card extends BaseEntity {

    @NotNull
    CardType type;

    @NotNull
    Boolean reversed;

    @NotNull
    Boolean selected;

    @ManyToOne
    Island island;

    @ManyToOne
    Player player;

    @ManyToOne
    Game game;

}
