import React, { useState } from "react";
import { Alert } from "reactstrap";
import FormGenerator from "../../components/formGenerator/formGenerator";
import tokenService from "../../services/token.service";
import "../../static/css/auth/authButton.css";
import { loginFormInputs } from "./form/loginFormInputs";

export default function Login() {
  const [message, setMessage] = useState(null)
  const loginFormRef = React.createRef();

  async function handleSubmit({ values }) {
    const reqBody = values;
    setMessage(null);
    await fetch("/api/v1/auth/signin", {
      headers: { "Content-Type": "application/json" },
      method: "POST",
      body: JSON.stringify(reqBody),
    })
      .then(function (response) {
        if (response.status === 200) return response.json();
        else return Promise.reject("Invalid login attempt");
      })
      .then(function (data) {
        tokenService.setUser(data);
        tokenService.updateLocalAccessToken(data.token);
        const user = tokenService.getUser();
        fetch(`/api/v1/players/${user.id}/connection`, {
          method: "PUT",
          headers: {
              Authorization: `Bearer ${tokenService.getLocalAccessToken()}`,
              Accept: "application/json",
              "Content-Type": "application/json",
          }
      })
        const userAuthority = user.roles[0];
        if (userAuthority === "ADMIN") {
          window.location.href = "/";
        } else if (userAuthority === "PLAYER") {
          window.location.href = "/homePlayer";
        }
      })
      .catch((error) => {
        setMessage(error);
      });
  }


  return (
    <div className="authNew-page">
      {message ? (
        <Alert color="primary">{message}</Alert>
      ) : (
        <></>
      )}

      <h1>Login</h1>

      <div className="authNew-form">
        <FormGenerator
          ref={loginFormRef}
          inputs={loginFormInputs}
          onSubmit={handleSubmit}
          numberOfColumns={1}
          listenEnterKey
          buttonText="Login"
          buttonClassName="auth-button"
        />
      </div>
    </div>
  );
}