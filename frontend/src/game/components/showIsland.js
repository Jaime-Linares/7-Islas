import getImageForCardType from "../util/getImageForCardType";
import { Button } from "reactstrap";
import move from "../util/move";
import Barco from '../../static/images/Barco.png';


export default function ShowIsland(props) {
    let rutacss;
    if(props.number === 7) {
        rutacss = "game-mazo";
    } else {
        rutacss = "game-isla-" + props.number;
    }

    return (
        <div className={rutacss}>
            <h3>ISLA {props.number}</h3>
            {props.number !== 7 &&
                <div className="game-cartas">
                    <img src={props.cardsIsland === null ? '' : getImageForCardType(props.cardsIsland.type)}
                        alt={props.cardsIsland === null ? 'Empty' : props.cardsIsland.type}
                        style={{ width: '90px', height: '90px' }}
                    />
                    {props.isPlayerTurn && props.cardsIsland !== null &&
                        <Button
                            size="md"
                            onClick={() =>
                                move(props.game, props.cardsIsland, props.resultadoTirada, props.mano, props.player, props.jwt,
                                    props.setMessage, props.setVisible, props.setDiceIsThrown, props.diceIsThrown)
                            }>
                            Select
                        </Button>
                    }
                </div>
            }
            {props.number === 7 &&
                <div>
                    <img src={(props.cardsIsland.length > 0 ? Barco : '')}
                        alt={props.cardsIsland.length > 0 ? Barco : 'Empty'}
                        style={{ width: '90px', height: '90px' }}
                    />
                </div>
            }
        </div>
    );
}
