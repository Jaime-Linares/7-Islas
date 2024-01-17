package org.juego.siete.islas.stat;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.juego.siete.islas.card.Card;
import org.juego.siete.islas.card.CardType;
import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.game.GameRepository;
import org.juego.siete.islas.player.Player;
import org.juego.siete.islas.player.PlayerService;
import org.juego.siete.islas.user.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class StatService {

    private final StatRepository statRepository;
    private final PlayerService playerService;
    private final GameRepository gameRepository;

    @Autowired
    public StatService(StatRepository statRepository, PlayerService playerService, GameRepository gameRepository) {
        this.statRepository = statRepository;
        this.playerService = playerService;
        this.gameRepository = gameRepository;
    }


    @Transactional(readOnly = true)
    public Stat findStatById(Integer statId) {
        return statRepository.findById(statId)
            .orElseThrow(() -> new ResourceNotFoundException("Stat", "ID", statId));
    }

    @Transactional(readOnly = true)
    public List<Player> findRankingByAtributte(String atributo) {
        List<Player> ranking = new ArrayList<>();
        if(atributo.equals("gamesWon")) {
            ranking = statRepository.findRankingByGamesWon();
        } else if(atributo.equals("averageScore")) {
            ranking = statRepository.findRankingByAverageScore();
        } else {
            throw new ResourceNotFoundException("It does not exist a ranking for that atributte.");
        }
        return ranking;
    }

    @Transactional(readOnly = true)
    public Stat findStatsByUserId(Integer userId) {
        Optional<Player> player = playerService.findPlayerByUser(userId);
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", userId);
        }
        Player playerToObtainStats = player.get();
        return statRepository.findStatByPlayerId(playerToObtainStats.getId());
    }

    @Transactional
    public Stat saveStat(Stat stat) {
        statRepository.save(stat);
        return stat;
    }

    @Transactional
    public Stat updateStat(Stat stat, Integer statId) {
        Stat statToUpdate = findStatById(statId);
        BeanUtils.copyProperties(stat, statToUpdate, "id");
        return saveStat(statToUpdate);
    }

    @Transactional
    public Stat updateStatAfterFinishGame(Game game, LocalTime timePlaying, User userToUpdateStat) {
        Stat statToUpdate = findStatsByUserId(userToUpdateStat.getId());

        LocalTime timePlayedUpdate = sumOfTimes(statToUpdate.getTimePlayed(), timePlaying);
        statToUpdate.setTimePlayed(timePlayedUpdate);

        updateShortestOrLongestGame(statToUpdate, timePlaying);

        statToUpdate.setNumGamesPlays(statToUpdate.getNumGamesPlays()+1);

        updateGamesWinned(statToUpdate, game, userToUpdateStat);
        updateAverageScore(statToUpdate, game, userToUpdateStat);
        updateAverageNumCardsEndGames(statToUpdate, userToUpdateStat);
        return updateStat(statToUpdate, statToUpdate.getId());
    }

    private LocalTime sumOfTimes(LocalTime before, LocalTime timeToPlus) {
        LocalTime suma = before.plusHours(timeToPlus.getHour()).plusMinutes(timeToPlus.getMinute()).plusSeconds(timeToPlus.getSecond());
        return suma;
    }

    private void updateShortestOrLongestGame(Stat statToUpdate, LocalTime timePlaying) {
        int comparacionShortest = statToUpdate.getTimeShortestGame().compareTo(timePlaying);
        if(comparacionShortest > 0) {
            statToUpdate.setTimeShortestGame(timePlaying);
        }

        int comparacionLongest = statToUpdate.getTimeLongestGame().compareTo(timePlaying);
        if(comparacionLongest < 0) {
            statToUpdate.setTimeLongestGame(timePlaying);
        }
    }

    private void updateGamesWinned(Stat statToUpdate, Game game, User userToUpdateStat) {
        Player playerToUpdateStat = findPlayerByUserId(userToUpdateStat);
        Map<Player,Integer> puntosJugadores = puntosPorJugador(game);
        Integer puntosGanador = puntosJugadores.values().stream().max(Comparator.naturalOrder()).get();
        Integer puntosPlayerToUpdateStat = puntosJugadores.get(playerToUpdateStat);
        if(puntosPlayerToUpdateStat == puntosGanador) {
            statToUpdate.setNumGamesWinned(statToUpdate.getNumGamesWinned()+1);
        }
    }

    private void updateAverageScore(Stat statToUpdate, Game game, User userToUpdateStat) {
        Player playerToUpdateStat = findPlayerByUserId(userToUpdateStat);
        Map<Player,Integer> puntosJugadores = puntosPorJugador(game);
        Integer puntosPlayerToUpdateStat = puntosJugadores.get(playerToUpdateStat);
        Integer numPartidasAcabadas = gameRepository.findFinishedGameByPlayerId(playerToUpdateStat.getId()).size() - 1;
        Double puntosPartidasAnteriores = statToUpdate.getAverageScore() * numPartidasAcabadas;
        Double averageScoreUpdate = (puntosPartidasAnteriores + puntosPlayerToUpdateStat) / (numPartidasAcabadas + 1);
        statToUpdate.setAverageScore(averageScoreUpdate);
    }

    private void updateAverageNumCardsEndGames(Stat statToUpdate, User userToUpdateStat) {
        Player playerToUpdateStat = findPlayerByUserId(userToUpdateStat);
        List<Card> cartasEnUltimaPartida = playerService.findCardsByPlayerId(playerToUpdateStat.getId());
        Integer numCartasToUpdateStat = cartasEnUltimaPartida.size();
        Integer numPartidasAcabadas = gameRepository.findFinishedGameByPlayerId(playerToUpdateStat.getId()).size() - 1;
        Double sumNumCartasPartidasAnteriores = statToUpdate.getAverageNumCardsEndGames() * numPartidasAcabadas;
        Double averageNumCardsEndGames = (sumNumCartasPartidasAnteriores + numCartasToUpdateStat) / (numPartidasAcabadas + 1);
        statToUpdate.setAverageNumCardsEndGames(averageNumCardsEndGames);
    }

    private Player findPlayerByUserId(User user) {
        Optional<Player> player = playerService.findPlayerByUser(user.getId());
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", user.getId());
        }
        return  player.get();
    }

    private Map<Player,Integer> puntosPorJugador(Game game) {
        Map<Player,Integer> res = new HashMap<>();
        for(Player player: game.getPlayers()) {
            List<Card> cartasPlayer = playerService.findCardsByPlayerId(player.getId());
            Integer puntosPlayer = calculaPuntos(cartasPlayer);
            res.put(player, puntosPlayer);
        }
        return res;
    }

    private Integer calculaPuntos(List<Card> cartasPlayer) {
        Integer puntosPorDoblon = cartasPlayer.stream().filter(carta -> carta.getType().equals(CardType.Doblon)).toList().size();

        Integer puntosPorTesoros = 0;
        Set<CardType> diferentesTiposTesoros = cartasPlayer.stream().filter(carta -> !carta.getType().equals(CardType.Doblon))
                                                .map(carta -> carta.getType()).collect(Collectors.toSet());
        Integer numTiposTesoresDistintos = diferentesTiposTesoros.size();
        if(numTiposTesoresDistintos == 1) {
            puntosPorTesoros = 1;
        } else if(numTiposTesoresDistintos == 2) {
            puntosPorTesoros = 3;
        } else if(numTiposTesoresDistintos == 3) {
            puntosPorTesoros = 7;
        } else if(numTiposTesoresDistintos == 4) {
            puntosPorTesoros = 13;
        } else if(numTiposTesoresDistintos == 5) {
            puntosPorTesoros = 21;
        } else if(numTiposTesoresDistintos == 6) {
            puntosPorTesoros = 30;
        } else if(numTiposTesoresDistintos == 7) {
            puntosPorTesoros = 40;
        } else if(numTiposTesoresDistintos == 8) {
            puntosPorTesoros = 50;
        } else if(numTiposTesoresDistintos == 9) {
            puntosPorTesoros = 60;
        }

        return puntosPorDoblon + puntosPorTesoros;
    }

}
