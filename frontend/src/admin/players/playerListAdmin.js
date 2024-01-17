import { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/admin/adminPage.css";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function PlayerListAdmin() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [players, setPlayers] = useFetchState(
        [],
        `/api/v1/players`,
        jwt,
        setMessage,
        setVisible
    );
    const [alerts, setAlerts] = useState([]);

    const playerList = players.map((player) => {
        return (
            <tr key={player.id}>
                <td style={{ whiteSpace: "nowrap" }}>
                    {player.firstName} {player.lastName}
                </td>
                <td>{player.registrationDate}</td>
                <td>{player.birthdayDate}</td>
                <td>{player.email}</td>
                <td>{player.user.username}</td>
                <td>
                    <ButtonGroup>
                        <Button
                            size="sm"
                            color="primary"
                            aria-label={"edit-" + player.user.username}
                            tag={Link}
                            to={"/players/" + player.id}
                        >
                            Edit
                        </Button>
                        <Button
                            size="sm"
                            color="danger"
                            aria-label={"delete-" + player.user.username}
                            onClick={() =>
                                deleteFromList(
                                    `/api/v1/players/${player.id}`,
                                    player.id,
                                    [players, setPlayers],
                                    [alerts, setAlerts],
                                    setMessage,
                                    setVisible
                                )
                            }
                        >
                            Delete
                        </Button>
                    </ButtonGroup>
                </td>
            </tr>
        );
    });

    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div className="admin-page-container">

            <div className="admin-page-container-head">
                <h1 className="text-center">Players</h1>
                {alerts.map((a) => a.alert)}
                {modal}
                <div className="admin-centrado">
                    <Button color="success" tag={Link} to="/players/new">
                        Add Player
                    </Button>
                </div>
            </div>

            <div>
                <table className="table-admin">
                    <thead>
                        <tr>
                            <th style={{ fontSize: 25, width: "5%" }}>Name</th>
                            <th style={{ fontSize: 25, width: "5%" }}>Registration Date</th>
                            <th style={{ fontSize: 25, width: "5%" }}>Birthday Date</th>
                            <th style={{ fontSize: 25, width: "5%" }}>Email</th>
                            <th style={{ fontSize: 25, width: "5%" }}>User</th>
                            <th style={{ fontSize: 25, width: "5%" }}>Actions</th>
                        </tr>
                    </thead>
                    <tbody>{playerList}</tbody>
                </table>
            </div>

        </div>
    );
}