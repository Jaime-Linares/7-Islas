export default function AchievementChecks(achievement) {

    let message;
    if (!/^[a-zA-Z]/.test(achievement.name)) {
        message="El nombre del logro debe comenzar con letras.";
        return message;
    }

    if (!/^\d+$/.test(achievement.numGamesPlays)) {
       message= "El número de juegos jugados debe ser obligatoriamente un número.";
        return message;
    }

    if (!/^\d+$/.test(achievement.numGamesWinned)) {
        message="El número de juegos ganados debe ser obligatoriamente un número.";
        return message;
    }

    if (parseInt(achievement.numGamesWinned) > parseInt(achievement.numGamesPlays)) {
        message= "El número de juegos ganados debe ser menor o igual que el número de juegos jugados.";
        return message;
    }
}