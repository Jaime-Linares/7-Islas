import { Button } from "reactstrap";
import abandonGame from "../util/abandonGame";
import changeTurn from "../util/changeTurn";


export default function ShowMyPlayer(props) {
    return (
        <div className="game-jugador1">
            <div style={{ display: "flex", flexDirection: "row" }}>
                <div className="game-cartas">
                    {!props.isSpectator &&
                        <h4>{props.player.user.username}</h4>
                    }

                    {props.isPlayerTurn &&
                        <h6>Te toca jugar!!!</h6>
                    }

                    {!props.isSpectator &&
                        <div className="game-cartas-scroll">
                            {props.visualHand}
                        </div>
                    }

                    <div className="game-boton-abandonar">
                        <Button
                            size="md"
                            color='danger'
                            onClick={() => {
                                let confirmMessage = window.confirm("Are you sure you want to exit the game?")
                                if (confirmMessage) {
                                    changeTurn(props.game, props.jwt, props.setMessage, props.setVisible);
                                    setTimeout(() => {
                                        abandonGame(props.game, props.user, props.setVisible, props.setMessage, props.jwt);
                                    }, 2000);
                                }
                            }}
                        >
                            Exit the game
                        </Button>
                    </div>
                </div>

                <div className="game-cartas">
                    {props.visualTirada}
                </div>
            </div>
        </div>
    );
}
