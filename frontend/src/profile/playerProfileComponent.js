import { useState } from "react";
import "../static/css/auth/authButton.css";
import "../static/css/auth/authPage.css";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import getProfileImage from "../game/util/getProfileImage";
import getErrorModal from "../util/getErrorModal";
import "../static/css/player/Profile.css";


const jwt = tokenService.getLocalAccessToken();

export default function PlayerProfileComponent() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [user, setUser] = useFetchState(
    [],
    `/api/v1/users/${tokenService.getUser().id}`,
    jwt,
    setMessage,
    setVisible
  );
  const [player, setPlayer] = useFetchState(
    [],
    `/api/v1/users/${tokenService.getUser().id}/player`,
    jwt,
    setMessage,
    setVisible
  )

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <>
      <div className="perfil-centrado">
        {modal}
        <img alt='Imagen Perfil' src={getProfileImage(player.image)} style={{ height: 200, width: 200 }} />
        <h3>Username: {user.username}</h3>
        <h3>First Name: {player.firstName}</h3>
        <h3>Last Name: {player.lastName}</h3>
        <h3>Birthday Date: {player.birthdayDate}</h3>
        <h3>Email: {player.email}</h3>
        <h3>Registration Date: {player.registrationDate}</h3>
      </div>
    </>
  );
}