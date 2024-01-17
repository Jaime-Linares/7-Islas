import { Form, Input, Label } from "reactstrap";
import sendMessageOnGame from "../util/sendMessageOnGame";
import ShowChat from "./showChat";


export default function SendMessage(props) {
    function handleChange(event) {
        props.setChatMessage(event.target.value);
    }

    function handleSubmit(event) {
        sendMessageOnGame(event, props.jwt, props.chatMessage);
    }

    return (
        <div className="game-chat-input">
            
            {/* Podran enviar mensaje solo los jugadores activos en la partida */}
            {!props.isSpectator &&
                <Form onSubmit={handleSubmit}>
                    <div className="custom-form-input">
                        <Label size="lg" for="chatMessage" className="custom-form-input-label">
                            CHAT
                        </Label>
                        <Input
                            type="text"
                            required
                            name="chatMessage"
                            id="insertMessage"
                            value={props.chatMessage || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                </Form>
            }
        </div>
    );
}