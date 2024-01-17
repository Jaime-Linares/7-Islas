export default function createStartingCountdown(sec) {

    const duracionEnMilisegundos = sec * 1000;

    return new Date(duracionEnMilisegundos);
}
