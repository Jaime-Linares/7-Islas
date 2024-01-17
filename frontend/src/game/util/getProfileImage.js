import Estandar from '../../static/images/pirata_lovy.jpg';
import foto1 from '../../static/images/FotoPerfil1.jpeg';
import foto2 from '../../static/images/FotoPerfil2.jpeg';
import foto3 from '../../static/images/FotoPerfil3.jpeg';
import foto4 from '../../static/images/FotoPerfil4.jpeg';
import foto5 from '../../static/images/FotoPerfil5.jpeg';
import foto6 from '../../static/images/FotoPerfil6.jpeg';
import foto7 from '../../static/images/FotoPerfil7.jpeg';
import foto8 from '../../static/images/FotoPerfil8.jpeg';
import foto9 from '../../static/images/FotoPerfil9.jpeg';


export default function getProfileImage(props) {
    switch (props) {
        case 'Estandar':
            return Estandar;
        case 'foto1':
            return foto1;
        case 'foto2':
            return foto2;
        case 'foto3':
            return foto3;
        case 'foto4':
            return foto4;
        case 'foto5':
            return foto5;
        case 'foto6':
            return foto6;
        case 'foto7':
            return foto7;
        case 'foto8':
            return foto8;
        case 'foto9':
            return foto9;
        default:
            return Estandar;
    }
}