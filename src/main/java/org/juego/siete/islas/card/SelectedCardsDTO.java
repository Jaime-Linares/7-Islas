package org.juego.siete.islas.card;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SelectedCardsDTO {
    
    @NotNull
    @NotEmpty
    List<Card> selectedCards;

    public SelectedCardsDTO() {

    }

    public SelectedCardsDTO(List<Card> cards) {
        this.selectedCards = cards;
    }
    
}
