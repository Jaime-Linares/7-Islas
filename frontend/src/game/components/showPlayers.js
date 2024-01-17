import ButtonDeleteExitPlayer from "./buttonDeleteExitPlayer";
import tokenService from "../../services/token.service";
import '../../static/css/game/gameWaitingRoom.css';
import getProfileImage from "../util/getProfileImage";


const user = tokenService.getUser();

export default function ShowPlayers(props) {
    return (
        <div className="game-usuarios">
            <div className="game-fotos-1">
                <h2 style={{ marginLeft: '10%' }}>{props.game.players.length !== 0 ? props.game.players[0].user.username : ""}</h2>
                <img style={{ width: '50%' }} src={getProfileImage(props.game.players[0] ? (props.game.players[0].image ? props.game.players[0].image : "Estandar") : "Estandar")} alt="Pirata" />
                {/* Al creador le damos la opción de poder eliminar a un jugador de la partida */}
                <div className="game-centrado">
                    {(user.id === (props.game.creator.user ? props.game.creator.user.id : 0)) &&
                        (props.game.creator.user ? props.game.creator.user.id : 0) !== (props.game.players.length !== 0 ? props.game.players[0].user.id : 0) &&
                        props.game.players.length !== 0 &&
                        <ButtonDeleteExitPlayer gameId={props.game.id} userId={props.game.players[0].user.id} />
                    }
                </div>
            </div>

            <div className="game-fotos-2">
                <h2 style={{ marginLeft: '10%' }}>{props.game.players.length > 1 ? props.game.players[1].user.username : "Waiting"}</h2>
                <img style={{ width: '50%', }} src={getProfileImage(props.game.players[1] ? (props.game.players[1].image ? props.game.players[1].image : "Estandar") : "Estandar")} alt="Pirata" />
                {/* Al creador le damos la opción de poder eliminar a un jugador de la partida */}
                <div className="game-centrado">
                    {(user.id === (props.game.creator.user ? props.game.creator.user.id : 0)) &&
                        (props.game.creator.user ? props.game.creator.user.id : 0) !== (props.game.players.length > 1 ? props.game.players[1].user.id : 0) &&
                        props.game.players.length > 1 &&
                        <ButtonDeleteExitPlayer gameId={props.game.id} userId={props.game.players[1].user.id} />
                    }
                </div>
            </div>

            <div className="game-fotos-3">
                <h2 style={{ marginLeft: '10%' }}>{props.game.players.length > 2 ? props.game.players[2].user.username : "Waiting"}</h2>
                <img style={{ width: '50%' }} src={getProfileImage(props.game.players[2] ? (props.game.players[2].image ? props.game.players[2].image : "Estandar") : "Estandar")} alt="Pirata" />
                {/* Al creador le damos la opción de poder eliminar a un jugador de la partida */}
                <div className="game-centrado">
                    {(user.id === (props.game.creator.user ? props.game.creator.user.id : 0)) &&
                        (props.game.creator.user ? props.game.creator.user.id : 0) !== (props.game.players.length > 2 ? props.game.players[2].user.id : 0) &&
                        props.game.players.length > 2 &&
                        <ButtonDeleteExitPlayer gameId={props.game.id} userId={props.game.players[2].user.id} />
                    }
                </div>
            </div>

            <div className="game-fotos-4">
                <h2 style={{ marginLeft: '10%' }}>{props.game.players.length > 3 ? props.game.players[3].user.username : "Waiting"}</h2>
                <img style={{ width: '50%' }} src={getProfileImage(props.game.players[3] ? (props.game.players[3].image ? props.game.players[3].image : "Estandar") : "Estandar")} alt="Pirata" />
                {/* Al creador le damos la opción de poder eliminar a un jugador de la partida */}
                <div className="game-centrado">
                    {(user.id === (props.game.creator.user ? props.game.creator.user.id : 0)) &&
                        (props.game.creator.user ? props.game.creator.user.id : 0) !== (props.game.players.length > 3 ? props.game.players[3].user.id : 0) &&
                        props.game.players.length > 3 &&
                        <ButtonDeleteExitPlayer gameId={props.game.id} userId={props.game.players[3].user.id} />
                    }
                </div>
            </div>
        </div>
    );
}