import useFetchState from "../../util/useFetchState";
import tokenService from "../../services/token.service";
import { useState } from "react";
import getErrorModal from "../../util/getErrorModal";
import '../../static/css/player/reconnect.css';

const jwt = tokenService.getLocalAccessToken();

export default function BackToGame() {
    const user = tokenService.getUser();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const modal = getErrorModal(setVisible, visible, message);

    const emptyPlayer = {
        id: "",
        firstName: "",
        lastName: "",
        registrationDate: "",
        user: ""
    };
    const emptyItemGame = {
        id: "",
        code: "",
        createdAt: "",
        startedAt: "",
        finishedAt: "",
        players: [emptyPlayer, emptyPlayer, emptyPlayer, emptyPlayer],
        creator: ""
    };

    const [lobby, setLobby] = useFetchState(
        emptyItemGame,
        `/api/v1/games/reconnectLobby/${user.id}`,
        jwt,
        setMessage,
        setVisible,
        user.id
    );

    const [game, setGame] = useFetchState(
        emptyItemGame,
        `/api/v1/games/reconnectGame/${user.id}`,
        jwt,
        setMessage,
        setVisible,
        user.id
    );

    function handleBackToLobbyClick() {
        if (!lobby.id) {
            setMessage("No estás en ninguna lobby o en la que estabas ya no está disponible, prueba a unirte a una nueva.")
            setVisible(true)
            return
        } else {
            window.location.href = `/game/${lobby.id}/waitingRoom/`;
        }
    }

    function handleBackToGameClick() {
        if (!game.id) {
            setMessage("No estás en ninguna partida en juego o en la que estabas ya no está disponible, prueba a unirte a una nueva.")
            setVisible(true)
            return
        } else {
            window.location.href = `/gameRoom/${game.id}`;
        }
    }



    return (
        <div className="player-reconnect">
            {modal}
            <div className="custom-button-row">
                <button type="submit" className="auth-button" onClick={handleBackToLobbyClick}>
                    BackToLobby
                </button>
                <button type="submit" className="auth-button" onClick={handleBackToGameClick}>
                    BackToGame
                </button>

            </div>
        </div>
    );
}