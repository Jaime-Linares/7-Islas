export default function abandonGame(game, user, setVisible, setMessage, jwt) {
    fetch(`/api/v1/games/game/${game.id}/exit/${user.id}`, {
        method: "PUT",
        headers: {
            Authorization: `Bearer ${jwt}`,
        },
    })
        .then((response) => response.json())
        .then((data) => {
            if (data.message) {
                setMessage(data.message);
                setVisible(true);
            } else {
                window.location.href = `/homePlayer`;
            }
        })
        .catch((message) => alert(message));
};
