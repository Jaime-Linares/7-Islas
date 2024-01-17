export default function removeDependencies(game, jwt) {
    fetch(`/api/v1/games/dependencies/${game.id}`, {
        method: "PUT",
        headers: {
            "Authorization": `Bearer ${jwt}`
        },
    })
        .then((response) => response.json())
        .catch((message) => alert(message));
}