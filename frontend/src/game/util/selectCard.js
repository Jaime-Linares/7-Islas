export default function seleccionarCarta(card, jwt, setMessage, setVisible) {
    fetch(`/api/v1/cards/${card.id}/updateSelection`, {
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
            }
        })
        .catch((message) => alert(message));
}