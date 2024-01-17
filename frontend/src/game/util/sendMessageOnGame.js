export default function sendMessageOnGame(event, jwt, chatMessage) {
    event.preventDefault();

    fetch(`/api/v1/games/chat/${chatMessage}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${jwt}`,
            Accept: 'application/json',
        }
    })
        .then((response) => {
            return response.json();
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}