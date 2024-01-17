export default function changeTurn(game, jwt, setMessage, setVisible) {
    fetch(`/api/v1/games/${game.id}/turn`, {
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
