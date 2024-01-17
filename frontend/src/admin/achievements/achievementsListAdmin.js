import { useState } from "react";
import { Link } from "react-router-dom";
import { Button, ButtonGroup } from "reactstrap";
import tokenService from "../../services/token.service";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";
import getAchievementImage from "./util/getAchievementImage";

const jwt = tokenService.getLocalAccessToken();

export default function AchievementsListAdmin() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [achievements, setAchievements] = useFetchState(
        [],
        `/api/v1/achievements`,
        jwt,
        setMessage,
        setVisible
    );
    const [alerts, setAlerts] = useState([]);

    const achievementsList = achievements.map((achievement) => {
        return (
            <tr key={achievement.id}>
                <td style={{ whiteSpace: "nowrap" }}>{achievement.name}</td>
                <td>
                    {"Minimun requirements: Games played = " +
                        achievement.numGamesPlays +
                        ", Games won = " +
                        achievement.numGamesWinned}
                </td>
                <td>
                    <img
                        src={getAchievementImage(achievement.dificulty)}
                        alt="logro"
                        width="75"
                        height="75"
                    />
                </td>
                <td>
                    <ButtonGroup>
                        <Button
                            size="sm"
                            color="primary"
                            aria-label={"edit-" + achievement.name}
                            tag={Link}
                            to={"/achievements/" + achievement.id}
                        >
                            Edit
                        </Button>
                        <Button
                            size="sm"
                            color="danger"
                            aria-label={"delete-" + achievement.name}
                            onClick={() =>
                                deleteFromList(
                                    `/api/v1/achievements/${achievement.id}`,
                                    achievement.id,
                                    [achievements, setAchievements],
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
        <div className="player-ach-background">
            <div className="player-ach-main">
                <h1>ACHIEVEMENTS</h1>
                {alerts.map((a) => a.alert)}
                {modal}
                <div>
                    <Button color="success" tag={Link} to="/achievements/new">
                        Add Achievement
                    </Button>
                </div>
            </div>

            <div>
                <table className="table-ach-admin">
                    <thead>
                        <tr>
                            <th style={{ fontSize: 25, width: "5%" }}>Name</th>
                            <th style={{ fontSize: 25, width: "5%" }}>Description</th>
                            <th style={{ fontSize: 25, width: "5%" }}>Logo</th>
                            <th style={{ fontSize: 25, width: "5%" }}>Actions</th>
                        </tr>
                    </thead>
                    <tbody>{achievementsList}</tbody>
                </table>
            </div>
        </div>
    );
}
