import '../static/css/game/gameRoom.css';
import useFetchState from "../util/useFetchState";
import tokenService from "../services/token.service";
import { useEffect, useState } from "react";
import getIdFromUrl from "../util/getIdFromUrl";
import Barco from '../static/images/Barco.png';
import { Button } from "reactstrap";
import useIntervalFetchState from "../util/useIntervalFetchState";
import getErrorModal from "../util/getErrorModal";
import getImageForCardType from './util/getImageForCardType';
import selectCard from './util/selectCard';
import ShowIsland from './components/showIsland';
import ShowMyPlayer from './components/showMyPlayer';
import SendMessage from './components/sendMessage';
import DadoInicial from '../static/images/DADO_INICIAL.png';
import throwDice from './util/throwDice';
import getDiceNumber from './util/getImageForResultDice';
import finishGame from './util/finishGame';
import Countdown from "./components/countdown";
import ShowChat from './components/showChat';
import eyeSpectator from '../static/images/ojo_espectadores.jpg'


const jwt = tokenService.getLocalAccessToken();
const user = tokenService.getUser();

export default function GameRoom() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [chatMessage, setChatMessage] = useState("");
    const id = getIdFromUrl(2);
    const emptyPlayer = {
        id: "",
        firstName: "",
        lastName: "",
        registrationDate: "",
        user: ""
    };
    const emptyItemGame = {
        id: "",
        code: "",
        createdAt: "",
        startedAt: "",
        finishedAt: "",
        players: [emptyPlayer, emptyPlayer, emptyPlayer, emptyPlayer],
        creator: ""
    };
    const emptyItemCard = {
        id: "",
        gameId: "",
        islandId: "",
        playerId: "",
        reversed: "",
        selected: "",
        type: ""
    };
    const emptyItemIsland = {
        id: "",
        gameId: "",
        number: ""
    };
    const [cards] = useIntervalFetchState(
        emptyItemCard,
        `/api/v1/cards/game/${id}`,
        jwt,
        setMessage,
        setVisible,
        null,
        1000
    );
    const [islands] = useFetchState(
        emptyItemIsland,
        `/api/v1/islands/game/${id}`,
        jwt,
        setMessage,
        setVisible,
    );
    const [game] = useIntervalFetchState(
        emptyItemGame,
        `/api/v1/games/${id}`,
        jwt,
        setMessage,
        setVisible,
        null,
        1000
    );
    const [chat] = useIntervalFetchState(
        [],
        `/api/v1/games/chat/${id}`,
        jwt,
        setMessage,
        setVisible,
        null,
        1000
    );

    const [resultadoTirada, setResultadoTirada] = useState(0);
    const [visualTirada, setVisualTirada] = useState([]);
    const [diceIsThrown, setDiceIsThrown] = useState(false);

    const [player] = useFetchState(emptyPlayer, `/api/v1/players/user/${user.id}`, jwt, setMessage, setVisible);
    const [mano] = useIntervalFetchState([], `/api/v1/players/${player.id}/cards`, jwt, setMessage, setVisible, null, 1000);
    const [visualHand, setVisualHand] = useState([]);

    const [isPlayerTurn, setIsPlayerTurn] = useState(player.isTurn);

    const [emptyIslands, setEmptyIslands] = useState(0);

    const [isSpectator, setIsSpectator] = useState(false);

    const [gameFinished, setGameFinished] = useState(false);

    // asigna cual es la isla 1,2,3,4,5,6,7
    let island1 = islands[0];
    let island2 = islands[1];
    let island3 = islands[2];
    let island4 = islands[3];
    let island5 = islands[4];
    let island6 = islands[5];

    let cardsPlayer1 = [];
    let cardsPlayer2 = [];
    let cardsPlayer3 = [];
    let cardsPlayer4 = [];

    let cardsIsland1 = null;
    let cardsIsland2 = null;
    let cardsIsland3 = null;
    let cardsIsland4 = null;
    let cardsIsland5 = null;
    let cardsIsland6 = null;
    let cardsIsland7 = [];

    for (let i = 0; i < cards.length; i++) {
        const card = cards[i];
        if (card.player) {
            if (game.players[0] && card.player.id === game.players[0].id) {
                cardsPlayer1.push(card);
            } else if (game.players[1] && card.player.id === game.players[1].id) {
                cardsPlayer2.push(card);
            } else if (game.players[2] && card.player.id === game.players[2].id) {
                cardsPlayer3.push(card);
            } else if (game.players[3] && card.player.id === game.players[3].id) {
                cardsPlayer4.push(card);
            }
        }
        if (card.island !== null) {
            if (card.island.id === island1.id) {
                cardsIsland1 = card;
            } else if (card.island.id === island2.id) {
                cardsIsland2 = card;
            } else if (card.island.id === island3.id) {
                cardsIsland3 = card;
            } else if (card.island.id === island4.id) {
                cardsIsland4 = card;
            } else if (card.island.id === island5.id) {
                cardsIsland5 = card;
            } else if (card.island.id === island6.id) {
                cardsIsland6 = card;
            } else {
                cardsIsland7.push(card);
            }
        }
    }


    // asignar cartas a los jugadores
    let cartasJugadores = [cardsPlayer1, cardsPlayer2, cardsPlayer3, cardsPlayer4];
    let indiceCartas = 0;
    game.players.map((player) => {
        player.cards = cartasJugadores[indiceCartas];
        indiceCartas += 1;
    })


    // mostrar mano de los otros jugadores
    const restoJugadores = game.players.map((jugador) => {
        if (jugador.id !== player.id) {
            return (
                <div className="game-jugador2">
                    <h4>{jugador.user.username}</h4>
                    <div className='game-cartas'>
                        {jugador.cards.length > 0 &&
                            <div>
                                <div className='game-cartas'>
                                    <img
                                        src={Barco}
                                        alt={Barco}
                                        style={{ width: '80px', height: '80px' }}
                                    />
                                </div>
                                <h6>Tiene {jugador.cards.length} cartas</h6>
                                {jugador.isTurn &&
                                    <h6>Estamos en su turno</h6>
                                }
                            </div>
                        }
                    </div>
                </div>
            );
        }
    })


    // actualiza si un jugador es espectador
    useEffect(() => {
        if (game && game.spectators && player && player.id) {
            let espectadores = game.spectators.map(player => player.id);
            let esEspectador = espectadores.includes(player.id);
            setIsSpectator(esEspectador);
        }
    }, [game, game.spectators, player]);


    // condicion final del partida
    useEffect(() => {
        if (!gameFinished && (game.players.length === 1 || (game.players.length === emptyIslands && cardsIsland7.length === 0))) {
            if (!isSpectator) {
                finishGame(game, jwt, setMessage, setVisible);
                setGameFinished(true);
            } else {
                setTimeout(() => {
                    window.location.href = `/game/${game.id}/endRoom`;
                }, 1000);
            }
        }
    }, [game, emptyIslands, cardsIsland7, gameFinished, isSpectator]);

    useEffect(() => {
        let numIslasVacias = 0;
        if (cardsIsland1 === null) {
            numIslasVacias = numIslasVacias + 1;
        }
        if (cardsIsland2 === null) {
            numIslasVacias = numIslasVacias + 1;
        }
        if (cardsIsland3 === null) {
            numIslasVacias = numIslasVacias + 1;
        }
        if (cardsIsland4 === null) {
            numIslasVacias = numIslasVacias + 1;
        }
        if (cardsIsland5 === null) {
            numIslasVacias = numIslasVacias + 1;
        }
        if (cardsIsland6 === null) {
            numIslasVacias = numIslasVacias + 1;
        }
        setEmptyIslands(numIslasVacias);
    }, [cardsIsland1, cardsIsland2, cardsIsland3, cardsIsland4, cardsIsland5, cardsIsland6])


    // nuestro jugador esta en turno o no
    useEffect(() => {
        const currentPlayer = game.players.find((jugador) => jugador.id === player.id);
        setIsPlayerTurn(currentPlayer && currentPlayer.isTurn);
    }, [game, player, mano]);


    // para mostrar tu propia mano
    useEffect(() => {
        let visual = mano.map((card) => {
            return (
                <div className='game-cartas'>
                    <img
                        src={getImageForCardType(card.type)}
                        alt={card.type}
                        style={{ width: '80px', height: '80px' }}
                    />
                    {isPlayerTurn &&
                        <Button
                            color='danger'
                            size="md"
                            onClick={() => {
                                selectCard(card, jwt, setMessage, setVisible);
                            }}>
                            {card.selected ? "Deselect" : "Select"}
                        </Button>
                    }
                </div>
            )
        });
        setVisualHand(visual);
    }, [mano, isPlayerTurn]);


    // para mostrar la imagen del resultado del dado
    useEffect(() => {
        let tirada =
            <div>
                {isPlayerTurn && !diceIsThrown &&
                    <img
                        src={DadoInicial}
                        alt={DadoInicial}
                        style={{ width: '80px', height: '80px' }}
                        onClick={() => {
                            throwDice(setResultadoTirada, setDiceIsThrown, diceIsThrown)
                        }}

                    />
                }
                {isPlayerTurn && diceIsThrown &&
                    <img
                        src={getDiceNumber(resultadoTirada)}
                        alt={getDiceNumber(resultadoTirada)}
                        style={{ width: '80px', height: '80px' }}
                    />
                }
            </div>
        setVisualTirada(tirada);
    }, [diceIsThrown, isPlayerTurn, resultadoTirada])


    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div className="game-fondo">
            {modal}
            <div className='game-arriba'>
                <div className="game-restoJugadores">
                    {restoJugadores}
                </div>
            </div>

            <div className='game-medio'>
                <div className='game-tablero'>
                    <ShowIsland number={1} cardsIsland={cardsIsland1} player={player} game={game} resultadoTirada={resultadoTirada}
                        mano={mano} jwt={jwt} setMessage={setMessage} setVisible={setVisible} isPlayerTurn={isPlayerTurn}
                        setDiceIsThrown={setDiceIsThrown} diceIsThrown={diceIsThrown} />

                    <ShowIsland number={2} cardsIsland={cardsIsland2} player={player} game={game} resultadoTirada={resultadoTirada}
                        mano={mano} jwt={jwt} setMessage={setMessage} setVisible={setVisible} isPlayerTurn={isPlayerTurn}
                        setDiceIsThrown={setDiceIsThrown} diceIsThrown={diceIsThrown} />

                    <ShowIsland number={3} cardsIsland={cardsIsland3} player={player} game={game} resultadoTirada={resultadoTirada}
                        mano={mano} jwt={jwt} setMessage={setMessage} setVisible={setVisible} isPlayerTurn={isPlayerTurn}
                        setDiceIsThrown={setDiceIsThrown} diceIsThrown={diceIsThrown} />

                    {isPlayerTurn &&
                        <Countdown startingSeconds={60} isPlayerTurn={isPlayerTurn} game={game} user={user} jwt={jwt}
                            setMessage={setMessage} setVisible={setVisible} />
                    }

                    <ShowIsland number={4} cardsIsland={cardsIsland4} player={player} game={game} resultadoTirada={resultadoTirada}
                        mano={mano} jwt={jwt} setMessage={setMessage} setVisible={setVisible} isPlayerTurn={isPlayerTurn}
                        setDiceIsThrown={setDiceIsThrown} diceIsThrown={diceIsThrown} />

                    <ShowIsland number={5} cardsIsland={cardsIsland5} player={player} game={game} resultadoTirada={resultadoTirada}
                        mano={mano} jwt={jwt} setMessage={setMessage} setVisible={setVisible} isPlayerTurn={isPlayerTurn}
                        setDiceIsThrown={setDiceIsThrown} diceIsThrown={diceIsThrown} />

                    <ShowIsland number={6} cardsIsland={cardsIsland6} player={player} game={game} resultadoTirada={resultadoTirada}
                        mano={mano} jwt={jwt} setMessage={setMessage} setVisible={setVisible} isPlayerTurn={isPlayerTurn}
                        setDiceIsThrown={setDiceIsThrown} diceIsThrown={diceIsThrown} />

                    <ShowIsland number={7} cardsIsland={cardsIsland7} player={player} game={game} resultadoTirada={resultadoTirada}
                        mano={mano} jwt={jwt} setMessage={setMessage} setVisible={setVisible} isPlayerTurn={isPlayerTurn}
                        setDiceIsThrown={setDiceIsThrown} diceIsThrown={diceIsThrown} />
                </div>
            </div>

            <div className='game-abajo'>
                {game.spectators && !isSpectator &&
                    <div className='game-espectadores'>
                        <img src={eyeSpectator} alt='Spectators: ' style={{width: '200px', height: '100px' }} />
                        <h1>{game.spectators.length}</h1>
                    </div>
                }

                <ShowMyPlayer player={player} visualHand={visualHand} diceIsThrown={diceIsThrown} setMessage={setMessage}
                    setVisible={setVisible} isPlayerTurn={isPlayerTurn} user={user} jwt={jwt} visualTirada={visualTirada}
                    game={game} isSpectator={isSpectator} />

                <div className='game-chat'>
                    <ShowChat chat={chat} />
                    <SendMessage setChatMessage={setChatMessage} jwt={jwt} chatMessage={chatMessage} isSpectator={isSpectator} />
                </div>
            </div>
        </div>
    );
}
