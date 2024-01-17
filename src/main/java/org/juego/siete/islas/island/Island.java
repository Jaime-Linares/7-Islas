package org.juego.siete.islas.island;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.model.BaseEntity;

@Entity
@Table(name = "islands")
@Setter
@Getter
public class Island extends BaseEntity {

    @NotNull
    Integer number;

    @ManyToOne
    Game game;

}
