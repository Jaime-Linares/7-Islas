package org.juego.siete.islas.game.Chat;

import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "chats")
@Setter
@Getter
public class Chat extends BaseEntity{

    @NotNull
    @Size(max = 100)
    String message;

    @NotNull
	String username;

    @ManyToOne()
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    Game game;
    
}
