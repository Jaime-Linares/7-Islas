import { useState } from "react";
import getErrorModal from "../../util/getErrorModal";
import tokenService from "../../services/token.service";
import { Form, Input, Label } from "reactstrap";
import getIdFromUrl from "../../util/getIdFromUrl";
import playerChecks from "./checks/playerRegisterChecks";

const jwt = tokenService.getLocalAccessToken();

export default function CreateRegisterPlayer() {
    const place = getIdFromUrl(1);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const modal = getErrorModal(setVisible, visible, message);

    const [playerDTO, setPlayerDTO] = useState({
        id: "",
        image: "",
        firstName: "",
        lastName: "",
        birthdayDate: "",
        email: "",
        username: "",
        password: ""
    });

    function handleCancelClick() {
        if (place === "register") {
            window.location.href = "/";
        } else {
            window.location.href = "/players"
        }

    }

    function handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        setPlayerDTO({ ...playerDTO, [name]: value });
    }

    function singIn(loginRequest) {
        let state = "0";
        fetch("/api/v1/auth/signin", {
            headers: { "Content-Type": "application/json" },
            method: "POST",
            body: JSON.stringify(loginRequest),
        })
            .then((response) => {
                if (response.status === 200) {
                    state = "200";
                } else {
                    state = "";
                }
                return response.json()
            })
            .then((jsonResponse) => {
                if (state === "200") {
                    tokenService.setUser(jsonResponse);
                    tokenService.updateLocalAccessToken(jsonResponse.token);
                    fetch(`/api/v1/players/${tokenService.getUser().id}/connection`, {
                        method: "PUT",
                        headers: {
                            Authorization: `Bearer ${tokenService.getLocalAccessToken()}`,
                            Accept: "application/json",
                            "Content-Type": "application/json",
                        }
                    })
                    window.location.href = "/homePlayer";
                } else {
                    setMessage(jsonResponse.message)
                    setVisible(true)
                }
            })
    }

    function handleSubmit(event) {
        event.preventDefault();
        let error= playerChecks(playerDTO);
        if(error){
            setMessage(error);
            setVisible(true);
            return;
        }

        fetch("/api/v1/auth/signup", {
            method: "POST",
            headers: {
                Authorization: place==="register"? "" : `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(playerDTO),
        })
            .then((response) => {
                if (response.status === 200 && place === "register") {
                    const loginRequest = {
                        username: playerDTO.username,
                        password: playerDTO.password,
                    };
                    singIn(loginRequest)
                    return response.json();
                } else if (response.status === 200) {
                    window.location.href = "/players";
                    return response.json();
                } else {
                    return response.json();
                }
            })
            .then((jsonResponse) => {
                setMessage(jsonResponse.message);
                setVisible(true);
                return;
            })
            .catch((error) => alert(error));
    }

    return (
        <div className="login-register-background">
            
            <div className="login-register-main">
                {<h2>{place === "register" ? "Register" : "Create Player"}</h2>}
                {modal}
                <Form onSubmit={handleSubmit}>
                    <div className="custom-form-input">
                        <Label for="username" className="custom-form-input-label">
                            Username
                        </Label>
                        <Input
                            type="text"
                            required
                            name="username"
                            id="username"
                            value={playerDTO.username || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-form-input">
                        <Label for="password" className="custom-form-input-label">
                            Password
                        </Label>
                        <Input
                            type="password"
                            required
                            name="password"
                            id="password"
                            value={playerDTO.password || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-form-input">
                        <Label for="firstName" className="custom-form-input-label">
                            First Name
                        </Label>
                        <Input
                            type="text"
                            required
                            name="firstName"
                            id="firstName"
                            value={playerDTO.firstName || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-form-input">
                        <Label for="lastName" className="custom-form-input-label">
                            Last Name
                        </Label>
                        <Input
                            type="text"
                            required
                            name="lastName"
                            id="lastName"
                            value={playerDTO.lastName || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-form-input">
                        <Label for="birthdayDate" className="custom-form-input-label">
                            Birthday Date
                        </Label>
                        <Input
                            type="date"
                            required
                            name="birthdayDate"
                            id="birthdayDate"
                            value={playerDTO.birthdayDate || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-form-input">
                        <Label for="email" className="custom-form-input-label">
                            Email
                        </Label>
                        <Input
                            type="email"
                            required
                            name="email"
                            id="email"
                            value={playerDTO.email || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-button-row">
                        <button type="submit" className="auth-button">
                            Save
                        </button>
                        <button type="submit" className="auth-button" onClick={handleCancelClick}>
                            Cancel
                        </button>

                    </div>
                </Form>
            </div>
        </div>
    );
}