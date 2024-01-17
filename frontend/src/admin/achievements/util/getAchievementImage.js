import Facil from '../../../static/images/cofreFacil.jpg';
import Medio from '../../../static/images/cofreMedio.jpg';
import Dificil from '../../../static/images/cofreDificil.jpg';
import MuyDificil from '../../../static/images/cofreMuyDificil.jpg';



export default function GetAchievementImage(props) {
    switch (props) {
        case 'Facil':
            return Facil;
        case 'Medio':
            return Medio;
        case 'Dificil':
            return Dificil;
        case 'Muy Dificil':
            return MuyDificil;
        default:
            return Facil;
    }
}