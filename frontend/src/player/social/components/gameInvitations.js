import tokenService from "../../../services/token.service";
import { Button } from "reactstrap";
import useIntervalFetchState from "../../../util/useIntervalFetchState"
import { useState } from 'react';
import getErrorModal from "../../../util/getErrorModal";


export default function GameInvitations() {
  const user = tokenService.getUser();
  const jwt = tokenService.getLocalAccessToken();

  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);

  const [gameInvitations] = useIntervalFetchState(
    [],
    `/api/v1/social/${user.username}/invitations`,
    jwt,
    setMessage,
    setVisible,
    null,
    1000
  );

  const acceptGameInvitations = async function (value) {
    fetch(`/api/v1/social/${value.social.id}/acceptInvitation`, {
      method: "PUT",
      headers: {
        "Authorization": `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      }
    })
      .then((response) => {
        if (response.ok) {
          window.location.href = "/game/" + value.gameInvitedId + "/waitingRoom"
          return response.text();
        } else {
          setMessage("No eres amigo con todos los jugadores")
          setVisible(true);
          return response.text();
        }
      })
      .catch((err) => {
        console.log(err);
        alert("Error deleting entity")
      });
  }

  const rejectGameInvitations = function (value) {
    fetch(`/api/v1/social/${value}/rejectInvitation`, {
      method: "PUT",
      headers: {
        "Authorization": `Bearer ${jwt}`,
        Accept: "application/json",
        "Content-Type": "application/json",
      }
    })
      .then((response) => {
        return response.text();
      })
      .catch((err) => {
        console.log(err);
        alert("Error deleting entity")
      });
  }

  const gameInvitationsList = gameInvitations.map((gameInvitation) => {
    return (
      <>
        <div>{(gameInvitation.social.invitation === true && gameInvitation.social.player2.user.username) ||
          (gameInvitation.social.invitation === false && gameInvitation.social.player1.user.username)} te ha invitado a {!gameInvitation.social.invitation_as ? "jugar!!" : "observar!!"}</div>
        <div className="horizontal">
          <Button onClick={() => acceptGameInvitations(gameInvitation)}>
            ACCEPT
          </Button>
          <Button color="danger" onClick={() => rejectGameInvitations(gameInvitation.social.id)}>
            REJECT
          </Button>
        </div>
      </>
    )
  });

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="table-social">
      {modal}
      <div>{gameInvitationsList}</div>
    </div>
  )
}