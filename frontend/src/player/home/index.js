import '../../static/css/player/homePlayer.css';
import { Button, NavLink } from 'reactstrap';
import { Link } from 'react-router-dom';
import { useState } from 'react';
import tokenService from "../../services/token.service";
import getErrorModal from "../../util/getErrorModal";
import useIntervalFetchState from "../../util/useIntervalFetchState";


export default function HomePlayer() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [game, setGame] = useState(null)
  const jwt = tokenService.getLocalAccessToken();
  const [connectedFriends] = useIntervalFetchState(
    [], 
    `/api/v1/social/${tokenService.getUser().id}/connected`, 
    jwt, 
    setMessage, 
    setVisible, 
    tokenService.getUser().id, 
    2000);
  let state = ""

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div>
      <div className="player-home-background">
        <div>
          Tienes {connectedFriends.length} amigos conectados!
        </div>
        <div className='player-home-page-container'>
          <h1 style={{ color: "#292929" }} className='home-title'>¡Bienvenido bucanero!</h1>
          {modal}
        </div>

        <div className='player-home-page-container2'>
          <h2 style={{ color: "#292929" }}>¿Que te apetece hacer hoy?</h2>
        </div>

        <div className='player-home-enlaces'>
          <div className='player-home-create-game'>
            <h3>
              <Button
                color='transparent'
                onClick={() =>
                  fetch("/api/v1/games", {
                    headers: {
                      Authorization: `Bearer ${jwt}`,
                      "Content-Type": "application/json"
                    },
                    method: "POST",
                  })
                    .then(function (response) {
                      if (response.status === 201) {
                        state = "201";
                        return response.json();
                      } else {
                        state = "";
                        return response.json();
                      }
                    })
                    .then(function (data) {
                      if (state !== "201") {
                        alert(data.message);
                      } else {
                        setGame(data);
                        window.location.href = `/game/${data.id}/waitingRoom`;
                      }
                    })
                    .catch((message) => {
                      alert(message);
                    })
                }
              >
                Crear partida
              </Button>
            </h3>
          </div>

          <div className='player-home-page-rules'>
            <h1>
              <NavLink tag={Link} to="/rules">?</NavLink>
            </h1>
          </div>

          <div className='player-home-join-game'>
            <h3>
              <Button
                color='transparent'
                onClick={() => window.location.href = `/gameCodeRoom`}
              >
                Unirse partida
              </Button>
            </h3>
          </div>
        </div>
      </div>
    </div>
  );
}
