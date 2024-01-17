import { useState } from "react";
import tokenService from "../../services/token.service";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";
import logoAchievement from "../../static/images/achievement.jpg"
import "../../static/css/player/achievements.css";
import GetAchievementImage from "../../admin/achievements/util/getAchievementImage";


const user = tokenService.getUser();
const jwt = tokenService.getLocalAccessToken();

export default function Achievements() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [achievements, setAchievements] = useFetchState(
    [],
    `/api/v1/achievements/user/${user.id}`,
    jwt,
    setMessage,
    setVisible
  );

  const achievementsList = achievements.map((achievement) => {
    return (
      <tr key={achievement.id}>
        <td style={{ whiteSpace: "nowrap" }}>
          {achievement.name}
        </td>
        <td>
          {"Minimun requirements: Games played = " + achievement.numGamesPlays + ", Games won = " + achievement.numGamesWinned}
        </td>
        <td>
          <img src={GetAchievementImage(achievement.dificulty)} alt="logro" width="75" height="75" />
        </td>
      </tr>
    );
  });

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="player-ach-background">
      <div className="player-ach-main">
        <h1>ACHIEVEMENTS</h1>
        {modal}
      </div>

      <div >
        <table className="table-ach">
          <thead>
            <tr>
              <th style={{ fontSize: 25, width: "5%" }}>Name</th>
              <th style={{ fontSize: 25, width: "10%" }}>Description</th>
              <th style={{ fontSize: 25, width: "5%" }}>Logo</th>
            </tr>
          </thead>
          <tbody>{achievementsList}</tbody>
        </table>
      </div>
    </div>
  );

}
