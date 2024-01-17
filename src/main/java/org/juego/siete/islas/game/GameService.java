package org.juego.siete.islas.game;

import org.juego.siete.islas.achievements.Achievement;
import org.juego.siete.islas.achievements.AchievementService;
import org.juego.siete.islas.builders.gameBuilder.GameBuilderService;
import org.juego.siete.islas.card.Card;
import org.juego.siete.islas.card.CardService;
import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.game.Chat.Chat;
import org.juego.siete.islas.game.Chat.ChatRepository;
import org.juego.siete.islas.island.Island;
import org.juego.siete.islas.island.IslandService;
import org.juego.siete.islas.player.Player;
import org.juego.siete.islas.player.PlayerService;
import org.juego.siete.islas.stat.Stat;
import org.juego.siete.islas.stat.StatService;
import org.juego.siete.islas.user.User;
import org.juego.siete.islas.user.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerService playerService;
    private final UserService userService;
    private final GameBuilderService gameBuilderService;
    private final IslandService islandService;
    private final CardService cardService;
    private final ChatRepository chatRepository;
    private final StatService statService;
    private final AchievementService achievementService;

    @Autowired
    public GameService(GameRepository gameRepository, PlayerService playerService, UserService userService,
            GameBuilderService gameBuilderService, CardService cardService, IslandService islandService,
            ChatRepository chatRepository, StatService statService, AchievementService achievementService) {
        this.gameRepository = gameRepository;
        this.playerService = playerService;
        this.userService = userService;
        this.gameBuilderService = gameBuilderService;
        this.cardService = cardService;
        this.islandService = islandService;
        this.chatRepository = chatRepository;
        this.statService = statService;
        this.achievementService = achievementService;
    }


    @Transactional(readOnly = true)
    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Game> findGamesByStatus(GameStatus status) {
        if (status == GameStatus.CREATED) {
            return gameRepository.findByStartedAtIsNull();
        } else if (status == GameStatus.STARTED) {
            return gameRepository.findByFinishAtIsNull();
        } else {
            return gameRepository.findByFinishAtIsNotNull();
        }
    }

    @Transactional(readOnly = true)
    public Game findGameById(Integer gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", "ID", gameId));
    }

    @Transactional(readOnly = true)
    public Optional<Game> findGamesUnstartedByUserId(Integer userId) {
        Optional<Player> player = playerService.findPlayerByUser(userId);
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", userId);
        }
        Integer id = player.get().getId();
        return gameRepository.findUnstartedGameByPlayerId(id);
    }

    @Transactional(readOnly = true)
    public Optional<Game> findGamesUnfinishedByUserId(Integer userId) {
        Optional<Player> player = playerService.findPlayerByUser(userId);
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", userId);
        }
        Integer id = player.get().getId();
        return gameRepository.findUnfinishedGameByPlayerId(id);
    }

    @Transactional(readOnly = true)
    public List<Game> findGamesByUserId(Integer userId) {
        Optional<Player> player = playerService.findPlayerByUser(userId);
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", userId);
        }
        Integer id = player.get().getId();
        return gameRepository.findGamesByPlayerId(id);
    }

    @Transactional(readOnly = true)
    public List<Game> findGamesByUserIdNotStarted(Integer userId) {
        Optional<Player> player = playerService.findPlayerByUser(userId);
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", userId);
        }
        Integer id = player.get().getId();
        return gameRepository.findGamesByPlayerIdNotStarted(id);
    }

    @Transactional(readOnly = true)
    public Game findRecentGameByPlayerId() {
        User user = userService.findCurrentUser();
        Player player = userService.findPlayerByUserId(user.getId());
        return gameRepository.findMostRecentGameByPlayerId(player.getId());
    }

    @Transactional(readOnly = true)
    public Integer findRecentGameByPlayerIdForInvitations(Integer userId) {
        User user = userService.findUserById(userId);
        Player player = userService.findPlayerByUserId(user.getId());
        return gameRepository.findMostRecentGameByPlayerIdForInvitation(player.getId());
    }

    @Transactional
    public Game saveGame() {
        Game newGame = new Game();
        String code = generateCode(4);
        newGame.setCode(code);
        LocalDateTime createdAt = LocalDateTime.now();
        newGame.setCreatedAt(createdAt);
        LocalDateTime startedAt = null;
        newGame.setStartedAt(startedAt);
        LocalDateTime finishedAt = null;
        newGame.setFinishedAt(finishedAt);
        User user = userService.findCurrentUser();
        Optional<Player> player = playerService.findPlayerByUser(user.getId());
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", user.getId());
        }
        Player creator = player.get();
        Game lobby = gameRepository.findUnstartedGameByPlayerId(creator.getId()).orElse(null);
        Game game = gameRepository.findUnfinishedGameByPlayerId(creator.getId()).orElse(null);

        if (lobby != null) {
            throw new IllegalStateException(
                    "No se puede estar en dos lobbys simultáneamente, revise si ya está en una.");
        }
        if (game != null) {
            throw new IllegalStateException(
                    "No se puede estar en dos partidas simultáneamente, revise si ya está en una.");
        }
        newGame.setCreator(creator);
        List<Player> players = new ArrayList<>();
        players.add(creator);
        newGame.setPlayers(players);
        return gameRepository.save(newGame);
    }

    private static String generateCode(Integer length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rnd = new Random();
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = rnd.nextInt(chars.length());
            code.append(chars.charAt(randomIndex));
        }
        return code.toString();
    }


    @Transactional
    public Game invitationByCode(String codigo) {
        // buscamos la partida por el code
        Optional<Game> gameToUpdate = gameRepository.findGameByCode(codigo);
        if (!gameToUpdate.isPresent()) {
            throw new ResourceNotFoundException("Game with code", "Code", codigo);
        }
        Game game = gameToUpdate.get();
        // buscamos el player que esta haciendo la peticion para unirse
        User user = userService.findCurrentUser();
        Optional<Player> player = playerService.findPlayerByUser(user.getId());
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", user.getId());
        }
        Player playerToAdd = player.get();
        // si no esta en la lista de jugadores de la partida lo añadimos
        addPlayer(playerToAdd, game);

        gameRepository.save(game);
        return game;
    }

    private void addPlayer(Player player, Game game) {
        Game lobby = gameRepository.findUnstartedGameByPlayerId(player.getId()).orElse(null);
        Game game1 = gameRepository.findUnfinishedGameByPlayerId(player.getId()).orElse(null);

        if (lobby != null) {
            throw new IllegalStateException(
                    "No se puede estar en dos lobbys simultáneamente, revise si ya está en una.");
        }
        if (game1 != null) {
            throw new IllegalStateException(
                    "No se puede estar en dos partidas simultáneamente, revise si ya está en una.");
        }
        if (game.getPlayers().size() < 4) {
            game.getPlayers().add(player);
        } else {
            throw new IllegalStateException("La partida esta llena, ya tiene 4 jugadores.");
        }
    }


    @Transactional
    public Game invitationByCodeSpectators(String codigo) {
        // buscamos la partida por el codigo
        Optional<Game> gameToUpdate = gameRepository.findGameByCode(codigo);
        if (!gameToUpdate.isPresent()) {
            throw new ResourceNotFoundException("Game with code", "Code", codigo);
        }
        Game game = gameToUpdate.get();
        // buscamos el player que esta haciendo la peticion para unirse
        User user = userService.findCurrentUser();
        Optional<Player> player = playerService.findPlayerByUser(user.getId());
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", user.getId());
        }
        Player spectatorToAdd = player.get();
        // si no esta en la lista de espectadores de la partida lo añadimos
        addSpectator(spectatorToAdd, game);

        gameRepository.save(game);
        return game;
    }

    private void addSpectator(Player spectator, Game game) {
        Game game1 = gameRepository.findUnstartedGameByPlayerId(spectator.getId()).orElse(null);
        if (game1 != null) {
            throw new IllegalStateException(
                    "No se puede estar en dos juegos simultáneamente, revise si ya está en una.");
        }
        game.getSpectators().add(spectator);
    }


    @Transactional
    public Game exitUserById(Integer gameId, Integer userId) {
        Game gameToUpdate = findGameById(gameId);
        // player relacionado con el user que se quiere salir
        Optional<Player> player = playerService.findPlayerByUser(userId);
        if (!player.isPresent()) {
            throw new ResourceNotFoundException("Player con userId", "ID", userId);
        }
        Player playerToExit = player.get();
        // quitar player de la lista de jugadores y ponerlo en los que han abandonado si
        // la partida ha empezado
        if (gameToUpdate.getPlayers().contains(playerToExit)) {
            List<Player> players = gameToUpdate.getPlayers();
            players.remove(playerToExit);
            gameToUpdate.setPlayers(players);
            if (gameToUpdate.getStartedAt() != null) {
                gameToUpdate.quitters.add(playerToExit);
                // eliminamos las cartas que pueda tener asigandas
                removeCardDependencies(playerToExit.getId());
                // actualizamos las horas
                updateSomeStats(playerToExit, gameToUpdate);
            }
        }
        gameRepository.save(gameToUpdate);
        return gameToUpdate;
    }

    private void removeCardDependencies(Integer playerId) {
        List<Card> cardsPlayer = playerService.findCardsByPlayerId(playerId);
        cardsPlayer.forEach(card -> cardService.deleteCard(card.getId()));
    }

    private void updateSomeStats(Player playerToExit, Game game) {
        Stat statPlayerToExit = statService.findStatsByUserId(playerToExit.getUser().getId());
        LocalTime timeExpendInGame = timeExpend(game.getStartedAt(), LocalDateTime.now());
        LocalTime timePlayedUpdate = sumOfTimes(timeExpendInGame, statPlayerToExit.getTimePlayed());
        statPlayerToExit.setTimePlayed(timePlayedUpdate);
        statService.updateStat(statPlayerToExit, statPlayerToExit.getId());
    }

    public LocalTime timeExpend(LocalDateTime creacion, LocalDateTime ahora) {
        LocalTime time1 = creacion.toLocalTime();
        LocalTime time2 = ahora.toLocalTime();
        Long diferenciaEnSegundos = ChronoUnit.SECONDS.between(time1, time2);
        return LocalTime.ofSecondOfDay(diferenciaEnSegundos);
    }

    private LocalTime sumOfTimes(LocalTime before, LocalTime timeToPlus) {
        LocalTime suma = before.plusHours(timeToPlus.getHour()).plusMinutes(timeToPlus.getMinute())
                .plusSeconds(timeToPlus.getSecond());
        return suma;
    }


    @Transactional
    public Game startPlayGameById(Integer gameId) {
        Game gameStart = findGameById(gameId);
        gameStart.setStartedAt(LocalDateTime.now());
        gameBuilderService.startGame(gameStart);
        return gameRepository.save(gameStart);
    }

    @Transactional
    public Game updateTurnByGameId(Integer gameId) {
        Game gameToChangeTurn = findGameById(gameId);
        List<Player> players = gameToChangeTurn.getPlayers();
        Integer positionOfTurnNow = findPositionInTurn(players);
        Integer positionOfTurnInFuture = findPositionInTurnAfter(positionOfTurnNow, players);
        updatePlayersTurn(players, positionOfTurnNow, positionOfTurnInFuture);
        return gameToChangeTurn;
    }

    private Integer findPositionInTurn(List<Player> players) {
        Integer position = null;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getIsTurn()) {
                position = i;
            }
        }
        return position;
    }

    private Integer findPositionInTurnAfter(Integer positionOfTurnNow, List<Player> players) {
        Integer positionOfTurnInFuture = null;
        if (positionOfTurnNow == players.size() - 1) {
            positionOfTurnInFuture = 0;
        } else {
            positionOfTurnInFuture = positionOfTurnNow + 1;
        }
        return positionOfTurnInFuture;
    }

    private void updatePlayersTurn(List<Player> players, Integer positionOfTurnNow, Integer positionOfTurnInFuture) {
        Player playerInTurnNow = players.get(positionOfTurnNow);
        playerInTurnNow.setIsTurn(false);
        Player playerInTurnFuture = players.get(positionOfTurnInFuture);
        playerInTurnFuture.setIsTurn(true);
        playerService.updatePlayerTurn(playerInTurnNow, playerInTurnNow.getId());
        playerService.updatePlayerTurn(playerInTurnFuture, playerInTurnFuture.getId());
    }


    @Transactional
    public Game finishGame(Integer gameId) throws Exception {
        Game gameToUpdate = findGameById(gameId);
        gameToUpdate.setFinishedAt(LocalDateTime.now());
        // actualizacion de estadisticas
        LocalTime timeExpendInGame = timeExpend(gameToUpdate.getStartedAt(), gameToUpdate.getFinishedAt());
        User userToUpdateStat = userService.findCurrentUser();
        statService.updateStatAfterFinishGame(gameToUpdate, timeExpendInGame, userToUpdateStat);
        // actualizacion de logros
        updateAchievementAfterFinishGame(gameToUpdate);
        return gameRepository.save(gameToUpdate);
    }

    @Transactional
    private void updateAchievementAfterFinishGame(Game game) throws Exception {
        List<Achievement> todosLosLogros = (List<Achievement>) achievementService.findAll();
        for(Achievement achievement: todosLosLogros) {
            achievementService.saveAchievement(achievement);
        }
    }

    
    @Transactional
    public void updateGameFinishedDependencies(Integer gameId) {
        List<Card> cardsGame = cardService.findCardsByGameId(gameId);
        List<Island> islandsGame = islandService.findIslandsByGameId(gameId);
        List<Chat> chatsGame = chatRepository.findMessagesByGame(gameId);
        cardsGame.forEach(card -> cardService.deleteCard(card.getId()));
        islandsGame.forEach(island -> islandService.deleteIsland(island.getId()));
        chatsGame.forEach(chat -> chatRepository.delete(chat));
    }

    @Transactional
    public Game updateGameByGameId(Integer gameId, Game game) {
        Game gameToUpdate = findGameById(gameId);
        BeanUtils.copyProperties(game, gameToUpdate, "id");
        return gameRepository.save(gameToUpdate);
    }

    @Transactional
    public void deleteGame(Integer id) {
        Game game = findGameById(id);
        gameRepository.delete(game);
    }

}
