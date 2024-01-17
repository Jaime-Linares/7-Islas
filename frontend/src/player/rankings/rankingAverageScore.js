import { useState } from "react";
import tokenService from "../../services/token.service";
import getErrorModal from "../../util/getErrorModal";
import useFetchState from "../../util/useFetchState";
import { Button } from "reactstrap";
import "../../static/css/player/ranking.css";

const jwt = tokenService.getLocalAccessToken();

export default function RankingAverageScore() {
    const user = tokenService.getUser();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const atributte = "averageScore";
    const [ranking] = useFetchState(
        [],
        `/api/v1/stats/ranking/${atributte}`,
        jwt,
        setMessage,
        setVisible
    );

    const rankingList = ranking.map((player,index) => {
        let color;
        let type;
        let type1;
        let type2;
        if (index === 0) {
            color = '#c19201';
        } else if (index === 1) {
            color = '#787d82';
        } else if (index === 2) {
            color = '#CD7F32';
        }
        if(player.user.id=== user.id){
            type= "Italic"
            type1= "Underline"
            type2= "rgba(255, 255, 255, 0.3)"  
        }
        
        return (
            <tr key={player.id}>
                <td style={{ fontStyle:type ,textDecoration: type1, backgroundColor: type2, color: color}}>
                    {player.user.username}
                </td>
                <td style={{ fontStyle:type ,textDecoration: type1, backgroundColor: type2, color: color}}>
                    {player.stats.averageScore}
                </td>
            </tr>
        );
    });
    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div className="player-ranks-background">
            <div className="player-ranks-main">
                <h1>Ranking - Average Score</h1>
                {modal}
                <Button
                    size="lg"
                    color="danger"
                    onClick={() => {
                        window.location.href = '/ranking/gamesWon';
                    }}
                >
                    Ranking - Games Won
                </Button>
            </div>

            <div className="table-ranks">
                <table>
                    <thead>
                        <tr>
                            <th style={{ fontSize: 25, width: "25%"}}>Username</th>
                            <th style={{ fontSize: 25, width: "25%" }}>Average Score</th>
                        </tr>
                    </thead>
                    <tbody>{rankingList}</tbody>
                </table>
            </div>
        </div>
    );
}