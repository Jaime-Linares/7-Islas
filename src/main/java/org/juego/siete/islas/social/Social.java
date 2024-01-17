package org.juego.siete.islas.social;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.juego.siete.islas.model.BaseEntity;
import org.juego.siete.islas.player.Player;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "friends")
@Setter
@Getter
public class Social extends BaseEntity{
    
    @NotNull
    @Column(name = "friend_request_status")
    Boolean accepted;


    @Column(name = "invitation")
    Boolean invitation;

    @Column(name = "invitation_as")
    Boolean invitation_as;

    @ManyToOne()
	@JoinColumn(name = "player1_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
	private Player player1;

    @ManyToOne()
	@JoinColumn(name = "player2_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
	private Player player2; 

}
