export default function ShowChat(props) {
    const chatList = props.chat.map((message) => {
        return (
            <div>
                {message.username} : {message.message}
            </div>
        )
    });

    return (
        <div className="game-chat-mensajes">
            {chatList}
        </div>
    );
}