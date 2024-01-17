import { useState } from "react";
import { Form, Input, Label } from "reactstrap";
import { Link } from "react-router-dom";
import tokenService from "../services/token.service";
import getErrorModal from "../util/getErrorModal";
import '../static/css/game/gameCodeRoom.css';


const jwt = tokenService.getLocalAccessToken();

export default function GameCodeRoom() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [code, setCode] = useState(null);

    function handleSubmit(event) {
        event.preventDefault();

        fetch("/api/v1/games/code/" + code, {
            method: "PUT",
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(code),
        })
            .then((response) => response.json())
            .then((data) => {
                if (data.message) {
                    setMessage(data.message);
                    setVisible(true);
                } else {
                    window.location.href = `/game/${data.id}/waitingRoom`;
                }
            })
            .catch((message) => alert(message));
    }

    function handleChange(event) {
        const target = event.target;
        const value = target.value;
        setCode(value);
    }

    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div className="game-code-home-background">
            <div className="game-code-page-container-head">
                <h1>PUT THE CODE OF THE GAME YOU WANT TO JOIN</h1>
                {modal}
            </div>
            <div className="game-code-input">
                <div className="auth-form-container">
                    <Form onSubmit={handleSubmit}>
                        <div className="custom-form-input">
                            <Label size="lg" for="code" className="custom-form-input-label">
                                Code
                            </Label>
                            <Input
                                type="text"
                                required
                                name="code"
                                id="code"
                                value={code || ""}
                                onChange={handleChange}
                                className="custom-input"
                            />
                        </div>
                        <div className="custom-button-row">
                            <button className="game-code-button">Confirmar</button>
                            <Link

                                to={`/homePlayer`}
                                className="game-code-button2"
                                style={{ textDecoration: "none" }}
                            >
                                Cancelar
                            </Link>
                        </div>
                    </Form>
                </div>
            </div>
        </div>
    );
}