import { useState } from 'react';
import { Button } from 'reactstrap';
import tokenService from "../../services/token.service";
import getErrorModal from '../../util/getErrorModal';


const jwt = tokenService.getLocalAccessToken();

export default function ButtonDeleteExitPlayer(props) {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);

    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div>
            {modal}
            <Button
                size="lg"
                color='danger'
                onClick={() => {
                    let confirmMessage = window.confirm("Are you sure you want to exit the game?");
                    if (confirmMessage) {
                        fetch(`/api/v1/games/game/${props.gameId}/exit/${props.userId}`, {
                            method: "PUT",
                            headers: {
                                Authorization: `Bearer ${jwt}`,
                            },
                        })
                            .then((response) => response.json())
                            .then((data) => {
                                if (data.message) {
                                    setMessage(data.message);
                                    setVisible(true);
                                }
                            })
                            .catch((message) => alert(message));
                    }
                }}
            >
                Kick out
            </Button>
        </div>
    );
}