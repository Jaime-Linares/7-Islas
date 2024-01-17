import { useState } from "react";
import { Link } from "react-router-dom";
import "../../static/css/auth/authButton.css";
import "../../static/css/auth/authPage.css";
import tokenService from "../../services/token.service";
import deleteFromList from "../../util/deleteFromList";
import useFetchState from "../../util/useFetchState";
import getErrorModal from "../../util/getErrorModal";


const jwt = tokenService.getLocalAccessToken();

export default function Login() {
  const [message, setMessage] = useState(null);
  const [visible, setVisible] = useState(false);
  const [users, setUsers] = useFetchState(
    [],
    `/api/v1/users`,
    jwt,
    setMessage,
    setVisible
  );
  const [alerts, setAlerts] = useState([]);

  function sendDeleteAccountRequest() {
    const user = tokenService.getUser();
    deleteFromList(
      `/api/v1/users/${user.id}`,
      user.id,
      [users, setUsers],
      [alerts, setAlerts],
      setMessage,
      setVisible
    );
    const jwt = window.localStorage.getItem("jwt");
    if (jwt || typeof jwt === "undefined") {
      tokenService.removeUser();
      window.location.href = "/";
    } else {
      alert("There is no user logged in");
    }
  }

  const modal = getErrorModal(setVisible, visible, message);

  return (
    <div className="authNew-page">
      <div className="authNew-form">
        <h2 className="text-center text-md">
          {modal}
          Are you sure you want to delete your account?
        </h2>
        <div className="options-row">
          <Link className="auth-button" to="/profile" style={{ textDecoration: "none" }}>
            No
          </Link>
          <button className="auth-button" onClick={() => sendDeleteAccountRequest()}>
            Yes
          </button>
        </div>
      </div>
    </div>
  );
}
