export default function throwDice(setResultadoTirada, setDiceIsThrown, diceIsThrown) {
    if (!diceIsThrown) {
        let resultado = Math.round(Math.random() * 5 + 1);
        setResultadoTirada(resultado);
        setDiceIsThrown(true);
    }
}
