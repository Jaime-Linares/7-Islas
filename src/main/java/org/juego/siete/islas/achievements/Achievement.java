package org.juego.siete.islas.achievements;

import org.juego.siete.islas.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "achievements")
@Getter
@Setter
public class Achievement extends BaseEntity {
    
    @NotNull
    @Column(unique = true)
    String name;

    @NotNull
    @Min(0)
    @Column(name = "games_played")
    Integer numGamesPlays;

    @NotNull
    @Min(0)
    @Column(name = "games_won")
    Integer numGamesWinned;

    @NotNull
    @Size(min = 3, max = 50)
    String dificulty;

    @Transient
    public String description() {
        String description = "Minimum requirements. Games played = " + numGamesPlays + ", Games won = " + numGamesWinned;
        return description;
    }

}
