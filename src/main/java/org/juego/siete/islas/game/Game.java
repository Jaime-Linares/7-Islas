package org.juego.siete.islas.game;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.juego.siete.islas.model.BaseEntity;
import org.juego.siete.islas.player.Player;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "games")
@Setter
@Getter
public class Game extends BaseEntity {

    @Column(name = "code", unique = true)
    @NotBlank
    @Size(min = 4, max = 4, message = "El código de la partida tiene que ser de longitud 4")
    String code;

    @Column(name = "created_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    @Column(name = "started_at")
    @Nullable
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime startedAt;

    @Column(name = "finished_at")
    @Nullable
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime finishedAt;

    @ManyToOne(optional = true)
    @JoinColumn(name = "creator", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    Player creator;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "game_players", joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id"),
            uniqueConstraints = { @UniqueConstraint(columnNames = { "game_id", "player_id" }) })
    List<Player> players;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "game_spectators", joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "spectator_id"),
            uniqueConstraints = { @UniqueConstraint(columnNames = { "game_id", "spectator_id" }) })
    List<Player> spectators;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "game_quitters", joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "quitter_id"),
            uniqueConstraints = { @UniqueConstraint(columnNames = { "game_id", "quitter_id" }) })
    List<Player> quitters;

    @Transient
    public Integer numPlayers() {
        return players.size();
    }

    @Transient
    public void removePlayerById(Integer playerId) {
        players.replaceAll(player -> {
            if (player.getId().equals(playerId)) {
                return null;
            }
            return player;
        });
    }

    @Transient
    public void removePlayerQuitterById(Integer playerId) {
        quitters.replaceAll(quitter -> {
            if (quitter.getId().equals(playerId)) {
                return null;
            }
            return quitter;
        });
    }

    @Transient
    public void removePlayerSpectatorById(Integer playerId) {
        spectators.replaceAll(spectator -> {
            if (spectator.getId().equals(playerId)) {
                return null;
            }
            return spectator;
        });
    }

}
