package org.juego.siete.islas.social;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SocialDTO {

    @NotNull
    private Social social;

    @NotNull
    private Integer gameInvitedId;

    public SocialDTO(){

    }

    public SocialDTO(Integer gameInvitedId, Social social){
        this.social=social;
        this.gameInvitedId=gameInvitedId;
    }
}
