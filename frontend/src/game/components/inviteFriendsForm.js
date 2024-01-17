import tokenService from "../../services/token.service";
import { useState } from "react";
import '../../static/css/game/gameWaitingRoom.css';
import { Form, Input, Label } from 'reactstrap';
import getErrorModal from "../../util/getErrorModal";


const jwt = tokenService.getLocalAccessToken();

export default function InviteFriendsForm() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [friendInvitation, setFriendInvitation] = useState(null);

    function handleChange(event) {
        const target = event.target;
        const value = target.value;
        setFriendInvitation(value);
    }

    function sendGameInvitation(event) {
        event.preventDefault();
        fetch("/api/v1/social/" + friendInvitation + "/invite", {
            method: "PUT",
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(friendInvitation),
        })
            .then((response) => response.json())
            .then((data) => {
                if (data.message) {
                    setMessage(data.message);
                    setVisible(true);
                } else {
                    window.alert("Invitación enviada con éxito.");
                }
            })
            .catch((message) => alert(message));
    }

    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div className="game-home-izquierda">
            <h3>INVITE FRIENDS</h3>
            {modal}
            <Form onSubmit={sendGameInvitation}>
                <div className="custom-form-input">
                    <Label size="lg" for="friendInvitation" className="custom-form-input-label">
                    </Label>
                    <Input
                        type="text"
                        required
                        name="friendInvitation"
                        id="friendInvitation"
                        value={friendInvitation || null}
                        onChange={handleChange}
                        className="custom-input"
                    />
                </div>
                <div className="custom-button-row">
                    <button className="game-code-button">Confirm</button>
                </div>
            </Form>
        </div>
    );
}