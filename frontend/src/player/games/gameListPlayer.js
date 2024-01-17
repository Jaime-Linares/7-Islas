import { useState } from "react";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";


const user = tokenService.getUser();
const jwt = tokenService.getLocalAccessToken();

export default function GameListPlayer() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [games, setGames] = useFetchState(
        [],
        `api/v1/games/user/${user.id}`,
        jwt,
        setMessage,
        setVisible
    );

    const gameList = games.map((game) => {
        let playersAux = game.players.map((g) => g.user.username + " ");
        let quittersAux = game.quitters.map((g) => g.user.username + " ");
        let spectatorsAux = game.spectators.map((g) => g.user.username + " ");
        let State = "Finished";
        let color = "Black"
        let rol;
        let spectator = false;
        let quitter = false;

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

        for (var i = 0; i < game.spectators.length; i++) {
            if (game.spectators[i].user.id === user.id)
                spectator = true
        }

        for (var i = 0; i < game.quitters.length; i++) {
            if (game.quitters[i].user.id === user.id)
                quitter = true
        }

        if (game.creator !== null) {
            if (game.creator.user.id === user.id) {
                rol = "Creator"
                if (State === "Finished") {
                    color = "Purple"
                }
            } else {
                rol = "Player"
            }
        }
        if (spectator) {
            rol = "Spectator";
            if (State === "Finished") {
                color = "#FF8C00"
            }
        }
        if (quitter) {
            rol = "Quitter";
            if (State === "Finished") {
                color = "#8B0000"
            }
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
                <td style={{ color: color, padding: "8px" }}>{rol}</td>
            </tr>
        );
    });

    const modal = getErrorModal(setVisible, visible, message);

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
                            <th style={{ fontSize: 25, width: "5%" }}>Code</th>
                            <th style={{ fontSize: 25, width: "5%" }}>Creator</th>
                            <th style={{ fontSize: 25, width: "10%" }}>Players</th>
                            <th style={{ fontSize: 25, width: "7%" }}>CreatedAt</th>
                            <th style={{ fontSize: 25, width: "7%" }}>StartedAt</th>
                            <th style={{ fontSize: 25, width: "7%" }}>FinishedAt</th>
                            <th style={{ fontSize: 25, width: "5%" }}>Quitters</th>
                            <th style={{ fontSize: 25, width: "5%" }}>Spectators</th>
                            <th style={{ fontSize: 25, width: "5%" }}>State</th>
                            <th style={{ fontSize: 25, width: "5%" }}>Rol</th>
                        </tr>
                    </thead>
                    <tbody>{gameList}</tbody>
                </table>
            </div>
        </div>
    );
}