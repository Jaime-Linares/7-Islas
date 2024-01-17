import changeTurn from "./changeTurn";


class selectedCardsDTO {
    constructor(selectedCards) {
        this.selectedCards = selectedCards;
    }
}

export default function move(game, card, resultadoTirada, mano, player, jwt, setMessage, setVisible, setDiceIsThrown, diceIsThrown) {
    let cartasSeleccionadas = mano.filter(carta => carta.selected === true)
    if (!diceIsThrown || resultadoTirada === 0) {
        window.alert("Para jugar, tira el dado.");
    } else if (resultadoTirada === card.island.number && cartasSeleccionadas.length === 0) {
        fetch(`/api/v1/cards/${card.id}/player/${player.id}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${jwt}`,
            },
        })
            .then((response) => response.json())
            .then((json) => {
                if (json.message) {
                    setMessage(json.message);
                    setVisible(true);
                } else {
                    setDiceIsThrown(false);
                    changeTurn(game, jwt, setMessage, setVisible);
                }
            })
            .catch((message) => alert(message));
    } else if (Math.abs(card.island.number - resultadoTirada) === cartasSeleccionadas.length) {
        let cartasManoSeleccionadas = new selectedCardsDTO(cartasSeleccionadas);
        fetch(`/api/v1/cards/${card.id}/player/${player.id}/selectedCards`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(cartasManoSeleccionadas),
        })
            .then((response) => response.json())
            .then((json) => {
                if (json.message) {
                    setMessage(json.message);
                    setVisible(true);
                } else {
                    setDiceIsThrown(false);
                    changeTurn(game, jwt, setMessage, setVisible);
                }
            })
            .catch((message) => alert(message));
    } else {
        window.alert("Cartas seleccionadas: " + cartasSeleccionadas.length + ". Tirada: " + resultadoTirada + 
            ". No puedes viajar a esta isla.");
    }
}
