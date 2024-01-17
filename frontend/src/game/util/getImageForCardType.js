import Barco from '../../static/images/Barco.png';
import BarrilDeRon from '../../static/images/BarrilDeRon.png';
import MapaDelTesoro from '../../static/images/MapaDelTesoro.png';
import Collar from '../../static/images/Collar.png';
import Caliz from '../../static/images/Caliz.png';
import Corona from '../../static/images/Corona.png';
import Diamante from '../../static/images/Diamante.png';
import Doblon from '../../static/images/Doblon.png';
import Espada from '../../static/images/Espada.png';
import Rubi from '../../static/images/Rubi.png';
import Revolver from '../../static/images/Revolver.png';


export default function getImageForCardType(cardType) {
    switch (cardType) {
        case 'Barco':
            return Barco;
        case 'Caliz':
            return Caliz;
        case 'MapaDelTesoro':
            return MapaDelTesoro;
        case 'Collar':
            return Collar;
        case 'Corona':
            return Corona;
        case 'Diamante':
            return Diamante;
        case 'Doblon':
            return Doblon;
        case 'Espada':
            return Espada;
        case 'Rubi':
            return Rubi;
        case 'BarrilDeRon':
            return BarrilDeRon;
        case 'Revolver':
            return Revolver;
        default:
            return '';
    }
}