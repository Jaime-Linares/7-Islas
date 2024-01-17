export default async function calculatePoints (player, jwt, setMessage, setVisible) {
    try {
        const response = await fetch(`/api/v1/players/${player.id}/cards`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${jwt}`,
            },
        });
        const json = await response.json();
        if (response.status !== 200) {
            setMessage(json.message);
            setVisible(true);
        } else {
            const playerCards = json;
            const puntosPorDoblones = calcularPuntosDeDoblones(playerCards);
            const puntosPorTesoros = calcularPuntosDeTesoros(playerCards);
            return puntosPorDoblones + puntosPorTesoros;
        }
    } catch(error) {
        console.error("Error fetching game:", error);
    }
}


function calcularPuntosDeDoblones(cards) {
    let puntos = 0;
    for(let i=0; i<cards.length; i++) {
        if(cards[i].type === 'Doblon') {
            puntos += 1;    // +1 por cada doblon
        }
    }
    return puntos;
}


function calcularPuntosDeTesoros(cards) {
    // diccionario {tipoCarta: [cartas de ese tipo]}
    let cardsByType = {};
    for(let i=0; i<cards.length; i++) {
        let card = cards[i];
        let cardType = card.type;
        if(cardType !== 'Doblon') {  // doblon ya lo hemos hecho a parte
            if(!cardsByType[cardType]) {
                cardsByType[cardType] = [];
            }
            cardsByType[cardType].push(card);
        }
    }
    // miramos el numero de tipos de cartas diferentes que hay y asignamos puntos
    const cardTypesByPlayer = Object.keys(cardsByType);
    let puntos = 0;
    if(cardTypesByPlayer.length === 1) {
        puntos = 1;
    } else if(cardTypesByPlayer.length === 2) {
        puntos = 3;
    } else if(cardTypesByPlayer.length === 3) {
        puntos = 7;
    } else if(cardTypesByPlayer.length === 4) {
        puntos = 13;
    } else if(cardTypesByPlayer.length === 5) {
        puntos = 21;
    } else if(cardTypesByPlayer.length === 6) {
        puntos = 30;
    } else if(cardTypesByPlayer.length === 7) {
        puntos = 40;
    } else if(cardTypesByPlayer.length === 8) {
        puntos = 50;
    } else if(cardTypesByPlayer.length === 9) {
        puntos = 60;
    }

    return puntos;
}
