import React from "react";
import { Link } from "react-router-dom";
import "../../static/css/auth/authButton.css";
import "../../static/css/auth/authPageNew.css";
import tokenService from "../../services/token.service";


const Logout = () => {
  async function sendLogoutRequest() {
    const jwt = window.localStorage.getItem("jwt");
    if (jwt || typeof jwt === "undefined") {
      await fetch(`/api/v1/players/${tokenService.getUser().id}/connection`, {
        method: "PUT",
        headers: {
            Authorization: `Bearer ${tokenService.getLocalAccessToken()}`,
            Accept: "application/json",
            "Content-Type": "application/json",
        }
    })
      tokenService.removeUser();
      window.location.href = "/";
    } else {
      alert("There is no user logged in");
    }
  }

  return (
    <div className="authNew-page">
      <div className="authNew-formlogout">
        <h2> Are you sure you want to log out? </h2>
        <div >
          <Link className="auth-button" to="/" style={{ textDecoration: "none" }}>
            No
          </Link>
          <button className="auth-button" onClick={() => sendLogoutRequest()}>
            Yes
          </button>
        </div>
      </div>
    </div>
  );
};

export default Logout;
