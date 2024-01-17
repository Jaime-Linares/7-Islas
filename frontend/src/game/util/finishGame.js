export default function finishGame(game, jwt, setMessage, setVisible) {
    fetch(`/api/v1/games/finish/${game.id}`, {
        method: "PUT",
        headers: {
            "Authorization": `Bearer ${jwt}`
        },
    })
        .then((response) => response.json())
        .then((json) => {
            if (json.message) {
                setMessage(json.message);
                setVisible(true);
            } else {
                window.location.href = `/game/${game.id}/endRoom`;
            }
        })
        .catch((message) => alert(message));
}