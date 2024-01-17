import { useState } from "react";
import { Form, Input, Label } from "reactstrap";
import tokenService from "../../services/token.service";
import "../../static/css/crud/login_register.css";
import getErrorModal from "../../util/getErrorModal";
import getIdFromUrl from "../../util/getIdFromUrl";
import useFetchData from "../../util/useFetchData";
import useFetchState from "../../util/useFetchState";
import userChecks from "./checks/userChecks";

const jwt = tokenService.getLocalAccessToken();

export default function EditAdminProfileAndUsers() {
    const id = getIdFromUrl(2);
    const place = getIdFromUrl(1);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const auths = useFetchData(`/api/v1/users/authorities`, jwt);
    const modal = getErrorModal(setVisible, visible, message);

    const emptyItem = {
        id: null,
        username: "",
        password: "",
        authority: null,
    };

    const [user, setUser] = useFetchState(
        emptyItem,
        /^\d+$/.test(id) ? `/api/v1/users/${id}` : `/api/v1/users/${tokenService.getUser() ? tokenService.getUser().id : 0}`,
        jwt,
        setMessage,
        setVisible,
        id
    );

    const [user1, setUser1] = useFetchState(
        emptyItem,
        /^\d+$/.test(id) ? `/api/v1/users/${id}` : `/api/v1/users/${tokenService.getUser() ? tokenService.getUser().id : 0}`,
        jwt,
        setMessage,
        setVisible,
        id
    );

    function handleCancelClick() {
        if (place === "users") {
            window.location.href = "/users";
        } else {
            window.location.href = "/profile"
        }
    }

    function handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        setUser({ ...user, [name]: value });
    }

    function handleSubmit(event) {
        event.preventDefault();
        let error = userChecks(user, user1)
        if (error) {
            setMessage(error)
            setVisible(true)
            return
        }
        if (tokenService.getUser()) {
            fetch(place === "users" ? `/api/v1/users/${user.id}` : `/api/v1/users/${tokenService.getUser().id}`, {
                method: "PUT",
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    Accept: "application/json",
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(user),
            })
                .then((response) => {
                    if (response.status === 200 && place === "users") {
                        window.location.href = "/users";
                        return response.json;
                    } else if (response.status === 200) {
                        if (user1.username === user.username) {
                            window.location.href = "/profile";
                            return response.json();
                        } else {
                            if (tokenService.getUser()) {
                                fetch(`/api/v1/players/${tokenService.getUser().id}/connection`, {
                                    method: "PUT",
                                    headers: {
                                        Authorization: `Bearer ${jwt}`,
                                        Accept: "application/json",
                                        "Content-Type": "application/json",
                                    }
                                });
                                tokenService.removeUser();
                                window.location.href = "/";
                                return response.json();
                            }
                        }
                    } else {
                        return response.json();
                    }
                })
                .then((jsonResponse) => {
                    setMessage(jsonResponse.message)
                    setVisible(true)
                    return;
                })
                .catch((message) => alert(message));
        }
    }

    return (
        <div className="login-register-background">

            <div className="login-register-main">
                {<h2>{place === "users" ? "Edit user" : "Edit Admin"} </h2>}
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
                            value={user.username || ""}
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
                            value={user.password || ""}
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