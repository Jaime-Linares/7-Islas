import React, {useEffect, useState} from "react";
import createStartingCountdown from "../util/createStartingCountdown";
import subtractASecond from "../util/subtractASecond";
import abandonGame from "../util/abandonGame";
import changeTurn from "../util/changeTurn";


export default function Countdown(props) {
    const [fecha, setFecha] = useState(createStartingCountdown(props.startingSeconds));

    useEffect(() => {
        if (props.isPlayerTurn) {
            let sampleInterval = setInterval(() => {
                setFecha(prevFecha => subtractASecond(prevFecha));
                if (fecha.getUTCSeconds() === 1) {
                    changeTurn(props.game, props.jwt, props.setMessage, props.setVisible);
                    setTimeout(() => {
                        abandonGame(props.game, props.user, props.setVisible, props.setMessage, props.jwt);
                    }, 2000);
                }
            }, 1000);

            return () => clearInterval(sampleInterval);
        } else {
            setFecha(createStartingCountdown(props.startingSeconds));
        }

    }, [props.isPlayerTurn]);

    return (
        <div className="game-contador">
            {props.isPlayerTurn &&
                <h4>Tiempo restante: {fecha.getUTCSeconds()} segundos</h4>
            }
        </div>
    );
}
