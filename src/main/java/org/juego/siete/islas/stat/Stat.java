package org.juego.siete.islas.stat;

import java.time.LocalTime;

import org.juego.siete.islas.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "stats")
@Getter
@Setter
public class Stat extends BaseEntity {

    @NotNull
    @Column(name = "time_played")
    LocalTime timePlayed;

    @NotNull
    @Column(name = "time_longest_game")
    LocalTime timeLongestGame;

    @NotNull
    @Column(name = "time_shortest_game")
    LocalTime timeShortestGame;

    @NotNull
    @Min(0)
    @Column(name = "games_played")
    Integer numGamesPlays;

    @NotNull
    @Min(0)
    @Column(name = "games_won")
    Integer numGamesWinned;

    @NotNull
    @Min(0)
    @Column(name = "average_score")
    Double averageScore;

    @NotNull
    @Min(0)
    @Column(name = "average_cards_end_games")
    Double averageNumCardsEndGames;
    
}
