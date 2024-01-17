import { Form, Input, Label } from "reactstrap";
import { useState } from 'react';
import tokenService from "../../../services/token.service";
import getErrorModal from "../../../util/getErrorModal";


const jwt = tokenService.getLocalAccessToken();

export default function AddFriend() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [friendRequest, setFriendRequest] = useState(null);

  function sendFriendRequest(event) {
    event.preventDefault();

    fetch("/api/v1/social/" + friendRequest, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify(friendRequest),
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.message) {
          setMessage(data.message);
          setVisible(true);
        } else {
          setMessage("Solicitud de amistad enviada con éxito");
          setVisible(true);
        }
      })
      .catch((message) => alert(message));
  }

  function handleChange(event) {
    const target = event.target;
    const value = target.value;
    setFriendRequest(value);
  }

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="player-social-agregar">
      <h3>ADD FRIEND</h3>
      {modal}
      <Form onSubmit={sendFriendRequest}>
        <div className="custom-form-input">
          <Label size="lg" for="friendRequest" className="custom-form-input-label">
          </Label>
          <Input
            type="text"
            required
            name="friendRequest"
            id="friendRequest"
            value={friendRequest || ""}
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