package org.juego.siete.islas.player;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.juego.siete.islas.achievements.Achievement;
import org.juego.siete.islas.model.Person;
import org.juego.siete.islas.stat.Stat;
import org.juego.siete.islas.user.User;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "players")
@Setter
@Getter
public class Player extends Person {

    @Column(name = "registration_date")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @NotNull
    LocalDate registrationDate;

    @Column(name = "birthday_date")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @NotNull
    LocalDate birthdayDate;

    @Column(name = "email")
    @NotBlank
    String email;

    @Column(name = "image")
    @ColumnDefault("'Estandar'")
    @NotBlank
    String image;

    @Column(name = "is_turn")
    Boolean isTurn;

    @Column(name = "is_connected")
    @ColumnDefault("false")
    Boolean isConnected;

    @OneToOne(cascade = { CascadeType.DETACH, CascadeType.REFRESH, CascadeType.PERSIST })
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;

    @ManyToMany()
    @JoinTable(name = "player_achievements", joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "achievement_id"),
            uniqueConstraints = { @UniqueConstraint(columnNames = { "player_id", "achievement_id" }) })
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Achievement> achievements;

    @OneToOne(optional = false, cascade = CascadeType.REMOVE)
    Stat stats;

}
