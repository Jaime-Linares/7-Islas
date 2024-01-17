import useFetchState from "../util/useFetchState";
import tokenService from "../services/token.service";
import { useState, useEffect } from "react";
import getIdFromUrl from "../util/getIdFromUrl";
import '../static/css/game/gameWaitingRoom.css';
import { Button } from 'reactstrap';
import InviteFriendsForm from "./components/inviteFriendsForm";
import InviteFriendsAsSpectatorForm from "./components/inviteFriendsAsSpectatorForm";
import ShowPlayers from "./components/showPlayers";
import getErrorModal from "../util/getErrorModal";


const jwt = tokenService.getLocalAccessToken();
const user = tokenService.getUser();

export default function GameWaitingRoom() {
    const emptyItem = {
        id: "",
        code: "",
        createdAt: "",
        startedAt: "",
        finishedAt: "",
        players: [],
        creator: ""
    };
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const id = getIdFromUrl(2);
    const [game, setGame] = useFetchState(
        emptyItem,
        `/api/v1/games/${id}`,
        jwt,
        setMessage,
        setVisible
    );

    useEffect(() => {
        // define la función que realiza la petición a la partida
        const fetchGame = async () => {
            try {
                const response = await fetch(`/api/v1/games/${id}`, {
                    method: "GET",
                    headers: {
                        Authorization: `Bearer ${jwt}`,
                    },
                });
                // si la respuesta es un 404 (Not Found) es que ha sido eliminada
                if (response.status === 404) {
                    window.location.href = `/homePlayer`;
                } else {    // si no es 404 es que sigue estando
                    const data = await response.json();
                    setGame(data);
                }
                // miramos si una partida ha sido iniciada por otro jugador, si es asi nos mandará a la pantalla de la partida
                if (game.startedAt !== null && game.startedAt !== "") {
                    window.location.href = `/gameRoom/${game.id}`;
                }
                // si el jugador no esta en la lista de jugadores o de espectadores(porque ha sido expulsado) se le devuelve a la pantalla de inicio
                if (!(game.players.some(player => player.user.id === user.id) || game.spectators.some(player => player.user.id === user.id))) {
                    window.location.href = `/homePlayer`;
                }
            } catch (error) {
                console.error("Error fetching game:", error);
            }
        };
        // establece un intervalo para realizar la petición cada 2 segundos
        const intervalId = setInterval(fetchGame, 2000);
        // limpia el intervalo cuando el componente se desmonta
        return () => clearInterval(intervalId);
    }, [game.id, game.players, game.spectators, game.startedAt, id, setGame]);

    const modal = getErrorModal(setVisible, visible, message);    

    return (
        <div className="game-home-background">
            <div className="game-home-arriba">
                {(user.id === (game.creator.user ? game.creator.user.id : 0)) &&
                    <InviteFriendsForm />
                }
                <div className="game-page-container-head">
                    <h1 className="text-center">LOBBY</h1>
                    <h3>Code of the game: {game.code}</h3>
                    {modal}
                </div>
                {(user.id === (game.creator.user ? game.creator.user.id : 0)) &&
                    <InviteFriendsAsSpectatorForm />
                }
            </div>

            <ShowPlayers game={game} />

            <div className="game-centrado">
                {/* Empezar partida si eres creador y hay, al menos, 2 jugadores */}
                {(user.id === (game.creator.user ? game.creator.user.id : 0)) && game.players.length > 1 &&
                    <div>
                        <Button
                            size="lg"
                            color='danger'
                            onClick={() => {
                                let confirmMessage = window.confirm("Are you sure you want to start the game?");
                                if (confirmMessage) {
                                    try {
                                        fetch(`/api/v1/games/start/${game.id}`, {
                                            method: "PUT",
                                            headers: {
                                                Authorization: `Bearer ${jwt}`,
                                            },
                                        });
                                        window.location.href = `/gameRoom/${game.id}`;
                                    } catch (error) {
                                        console.error("Error starting game:", error);
                                    }
                                }
                            }}
                        >
                            Start play
                        </Button>
                    </div>
                }
                {/* Borrar partida si eres creador */}
                {(user.id === (game.creator.user ? game.creator.user.id : 0)) &&
                    <div className="game-centrado">
                        <Button
                            size="lg"
                            color="danger"
                            onClick={() => {
                                let confirmMessage = window.confirm("Are you sure you want to delete it?");
                                if (confirmMessage) {
                                    try {
                                        fetch(`/api/v1/games/${game.id}`, {
                                            method: "DELETE",
                                            headers: {
                                                Authorization: `Bearer ${jwt}`,
                                            },
                                        });
                                        window.location.href = `/homePlayer`;
                                    } catch (error) {
                                        console.error("Error deleting the game: ", error);
                                    }
                                }
                            }}
                        >
                            Delete game
                        </Button>
                    </div>
                }
                {/* Salirse de partida si no eres el creador */}
                {user.id !== (game.creator.user ? game.creator.user.id : 0) &&
                    <div className="game-centrado">
                        <Button
                            size="lg"
                            color='danger'
                            onClick={() => {
                                let confirmMessage = window.confirm("Are you sure you want to exit the game?");
                                if (confirmMessage) {
                                    try {
                                        fetch(`/api/v1/games/game/${game.id}/exit/${user.id}`, {
                                            method: "PUT",
                                            headers: {
                                                Authorization: `Bearer ${jwt}`,
                                            },
                                        });
                                        window.location.href = `/homePlayer`;
                                    } catch (error) {
                                        console.error("Error going away from the game:", error);
                                    }
                                }
                            }}
                        >
                            Exit the game
                        </Button>
                    </div>
                }
            </div>
        </div >
    );
}
