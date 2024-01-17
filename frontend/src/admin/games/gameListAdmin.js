import { useState } from "react";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function GameListAdmin() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const modal = getErrorModal(setVisible, visible, message);

    const [games, setGames] = useFetchState(
        [],
        `api/v1/games`,
        jwt,
        setMessage,
        setVisible
    );

    const gameList = games.map((game) => {
        let playersAux = game.players.map((g) => g.user.username + " ");
        let quittersAux = game.quitters.map((g) => g.user.username + " ");
        let spectatorsAux = game.spectators.map((g) => g.user.username + " ");
        let State;
        let color;
        if (game.createdAt && !game.startedAt) {
            State = "Waiting for start";
            color = "DarkBlue"
        } else if (game.createdAt && game.startedAt && !game.finishedAt) {
            State = "In progress";
            color = "DarkGreen"
        } else {
            State = "Finished";
            color = "Black"
        }

        return (
            <tr key={game.id}>
                <td style={{ color: color, padding: "8px" }}>{game.code}</td>
                <td style={{ color: color, padding: "8px" }}> {game.creator !== null ? game.creator.user.username : "delete account"}</td>
                <td style={{ color: color, padding: "8px" }}>{playersAux}</td>
                <td style={{ color: color, padding: "8px" }}>{game.createdAt}</td>
                <td style={{ color: color, padding: "8px" }}>{game.startedAt}</td>
                <td style={{ color: color, padding: "8px" }}>{game.finishedAt}</td>
                <td style={{ color: color, padding: "8px" }}>{quittersAux}</td>
                <td style={{ color: color, padding: "8px" }}>{spectatorsAux}</td>
                <td style={{ color: color, padding: "8px" }}>{State}</td>
            </tr>
        );
    });

    return (
        <div className="admin-page-container">
            <div className="admin-page-container-head">
                <h1 className="text-center">Games</h1>
                {modal}
            </div>
            <div>
                <table className="table-admin">
                    <thead>
                        <tr>
                            <th style={{ fontSize: 25, width: "5%", padding: "10px" }}>Code</th>
                            <th style={{ fontSize: 25, width: "5%", padding: "10px" }}>Creator</th>
                            <th style={{ fontSize: 25, width: "10%", padding: "10px" }}>Players</th>
                            <th style={{ fontSize: 25, width: "7%", padding: "10px" }}>CreatedAt</th>
                            <th style={{ fontSize: 25, width: "7%", padding: "10px" }}>StartedAt</th>
                            <th style={{ fontSize: 25, width: "7%", padding: "10px" }}>FinishedAt</th>
                            <th style={{ fontSize: 25, width: "5%", padding: "10px" }}>Quitters</th>
                            <th style={{ fontSize: 25, width: "5%", padding: "10px" }}>Spectators</th>
                            <th style={{ fontSize: 25, width: "5%", padding: "10px" }}>State</th>
                        </tr>
                    </thead>
                    <tbody>{gameList}</tbody>
                </table>
            </div>
        </div>
    );
}