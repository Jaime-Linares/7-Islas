import { useState } from "react";
import { Link } from "react-router-dom";
import { Form, Input, Label } from "reactstrap";
import tokenService from "../../services/token.service";
import getErrorModal from "../../util/getErrorModal";
import getIdFromUrl from "../../util/getIdFromUrl";
import useFetchState from "../../util/useFetchState";
import achievementChecks from "./checks/achievementChecks";
import "../../static/css/admin/achievements-admin.css";

const jwt = tokenService.getLocalAccessToken();

export default function AchievementsEditAdmin() {
    const id = getIdFromUrl(2);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const modal = getErrorModal(setVisible, visible, message);

    const emptyItem = {
        id: "",
        name: "",
        numGamesPlays: "",
        numGamesWinned: "",
        dificulty: ""
    };

    const [achievement, setAchievement] = useFetchState(
        emptyItem,
        `/api/v1/achievements/${id}`,
        jwt,
        setMessage,
        setVisible,
        id
    );

    function handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        setAchievement({ ...achievement, [name]: value });
    }

    function handleSubmit(event) {
        event.preventDefault();
        let error = achievementChecks(achievement);
        if (error) {
            setMessage(error);
            setVisible(true);
            return
        }

        fetch(`/api/v1/achievements/${id}`, {
            method: "PUT",
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(achievement),
        })
            .then((response) => {
                if (response.status === 200) {
                    window.location.href = "/achievementsAdmin";
                    return response.json();
                } else {
                    return response.json();
                }
            })
            .then((jsonResponse) => {
                setMessage(jsonResponse.message);
                setVisible(true);
            })
            .catch((message) => alert(message));
    }

    return (
        <div className="admin-ach-background">
            <h2>Edit Achievement</h2>
            {modal}
            <div className="admin-ach-main">
                <Form onSubmit={handleSubmit}>
                    <div className="custom-form-input">
                        <Label for="name" className="custom-form-input-label">
                            Name
                        </Label>
                        <Input
                            type="text"
                            required
                            name="name"
                            id="name"
                            value={achievement.name || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-form-input">
                        <Label for="numGamesPlays" className="custom-form-input-label">
                            Minimum number of games played to obtain it
                        </Label>
                        <Input
                            type="text"
                            required
                            name="numGamesPlays"
                            id="numGamesPlays"
                            value={achievement.numGamesPlays===0? "0" : achievement.numGamesPlays}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-form-input">
                        <Label for="numGamesWinned" className="custom-form-input-label">
                            Minimum number of games won to obtain it
                        </Label>
                        <Input
                            type="text"
                            required
                            name="numGamesWinned"
                            id="numGamesWinned"
                            value={achievement.numGamesWinned===0? "0" : achievement.numGamesWinned}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-form-input">
                        <Label for="dificulty" className="custom-form-input-label">
                            Difficulty
                        </Label>
                        <Input
                            type="select"
                            required
                            name="dificulty"
                            id="dificulty"
                            value={achievement.dificulty || ""}
                            onChange={handleChange}
                            className="custom-input"
                        >
                            <option value="" disabled>Select Difficulty</option>
                            <option value="Facil">Facil</option>
                            <option value="Medio">Medio</option>
                            <option value="Dificil">Dificil</option>
                            <option value="Muy Dificil">Muy Dificil</option>
                        </Input>
                    </div>
                    <div className="custom-button-row">
                        <button className="auth-button">Save</button>
                        <Link
                            to={`/achievementsAdmin`}
                            className="auth-button"
                            style={{ textDecoration: "none" }}
                        >
                            Cancel
                        </Link>
                    </div>
                </Form>
            </div>
        </div>
    );
}