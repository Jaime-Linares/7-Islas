import tokenService from "../../services/token.service";
import { useState } from "react";
import useFetchState from "../../util/useFetchState";
import getErrorModal from "../../util/getErrorModal";


const user = tokenService.getUser();
const jwt = tokenService.getLocalAccessToken();

export default function Stats() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const emptyStat = {
    timePlayed: "",
    timeLongestGame: "",
    timeShortestGame: "",
    numGamesPlays: "",
    numGamesWinned: "",
    averageScore: "",
    averageNumCardsEndGames: ""
  };
  const [stat] = useFetchState(
    emptyStat,
    `api/v1/stats/user/${user.id}`,
    jwt,
    setMessage,
    setVisible
  );

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="container-page">
      <h1>STATS</h1>
      {modal}
      <div className="player-profile-container2">
        <h3>Username: {user.username}</h3>
        <h3>Time played: {stat.timePlayed}</h3>
        <h3>Time longest game: {stat.timeLongestGame}</h3>
        <h3>Time shortest game: {stat.timeShortestGame}</h3>
        <h3>Games played: {stat.numGamesPlays}</h3>
        <h3>Games won: {stat.numGamesWinned}</h3>
        <h3>Average score: {stat.averageScore}</h3>
        <h3>Average cards at the end of games: {stat.averageNumCardsEndGames}</h3>
      </div>
    </div>
  );
}
