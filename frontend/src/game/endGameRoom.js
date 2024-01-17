import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import { Button } from "reactstrap";
import getIdFromUrl from "../util/getIdFromUrl";
import { useState, useEffect } from "react";
import calculatePoints from "./util/calculatePoints";
import getErrorModal from "../util/getErrorModal";
import removeDependencies from "./util/removeDependencies";
import '../static/css/game/gameEndGameRoom.css';


const jwt = tokenService.getLocalAccessToken();

export default function EndGameRoom() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const id = getIdFromUrl(2);
    const [game] = useFetchState(
        {},
        `/api/v1/games/${id}`,
        jwt,
        setMessage,
        setVisible,
        id
    );

    const [playersList, setPlayersList] = useState([]);
    const [quittersList, setQuittersList] = useState([]);
    const [spectatorsList, setSpectatorsList] = useState([]);

    const [canRemoveDependencies, setCanRemoveDependencies] = useState(false);

    const [canGoHome, setCanGoHome] = useState(false);

    // para borrar las dependencias (cartas, islas, chats) de la partida
    useEffect(() => {
        if (canRemoveDependencies) {
            setTimeout(() => {
                removeDependencies(game, jwt);
                setCanGoHome(true);
            }, 5000);
        }
    }, [canRemoveDependencies]);

    // para mostrar los jugadores con su puntuacion
    useEffect(() => {
        const fetchPlayersPoints = async () => {
            if (!game.players || game.players.length === 0) {
                return;
            }

            const playerListItems = await Promise.all(
                game.players.map(async (player) => {
                    const points = await calculatePoints(player, jwt, setMessage, setVisible);
                    return (
                        <li key={player.id}>
                            <div>{player.user && player.user.username}</div>
                            <div>{(points && `Points: ${points}`) || 'quitter'}</div>
                        </li>
                    );
                })
            );
            setPlayersList(playerListItems);
            setCanRemoveDependencies(true);
        };

        fetchPlayersPoints();
    }, [game.players]);

    // para mostrar los jugadores que se han ido de la partida
    useEffect(() => {
        const fetchQuitters = () => {
            if (!game.quitters || game.quitters.length === 0) {
                return;
            }

            const quitterListItems = game.quitters.map((player) => {
                return (
                    <li key={player.id}>
                        {player.user && player.user.username}
                    </li>
                );
            });
            setQuittersList(quitterListItems);
        };

        fetchQuitters();
    }, [game.quitters]);

    // para mostrar los jugadores que han estado observando la partida
    useEffect(() => {
        const fetchSpectator = () => {
            if (!game.spectators || game.spectators.length === 0) {
                return;
            }

            const spectatorListItems = game.spectators.map((player) => {
                return (
                    <li key={player.id}>
                        {player.user && player.user.username}
                    </li>
                );
            });
            setSpectatorsList(spectatorListItems);
        };

        fetchSpectator();
    }, [game.spectators]);


    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div className="gameend-fondo">
            <h1>SCOREBOARD</h1>
            {modal}

            <div className="gameend-resultados">
                <div className="gameend-resultados-tablas">
                    <h4>Points</h4>
                    <div>
                        <ul>{playersList}</ul>
                    </div>
                </div>

                {game.quitters && game.quitters.length !== 0 &&
                    <div className="gameend-resultados-tablas">
                        <h4>Quitters</h4>
                        <div>
                            <ul>{quittersList}</ul>
                        </div>
                    </div>
                }

                {game.spectators && game.spectators.length !== 0 &&
                    <div className="gameend-resultados-tablas">
                        <h4>Spectators</h4>
                        <div>
                            <ul>{spectatorsList}</ul>
                        </div>
                    </div>
                }
            </div>
            {canGoHome &&
                <Button
                    size="lg"
                    color="danger"
                    onClick={() => {
                        window.location.href = '/homePlayer';
                    }}
                >
                    Back to home
                </Button>
            }
        </div>
    );
}
