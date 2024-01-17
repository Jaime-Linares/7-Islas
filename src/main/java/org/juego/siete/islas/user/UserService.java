/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.juego.siete.islas.user;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.juego.siete.islas.exceptions.AccessDeniedException;
import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.game.Game;
import org.juego.siete.islas.game.GameRepository;
import org.juego.siete.islas.player.Player;
import org.juego.siete.islas.player.PlayerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    private final PasswordEncoder encoder;
    private UserRepository userRepository;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;

    @Autowired
    public UserService(UserRepository userRepository, PlayerRepository playerRepository, GameRepository gameRepository,
                       PasswordEncoder encoder) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.encoder = encoder;
    }


    @Transactional
    public User saveUser(User user) throws DataAccessException {
        userRepository.save(user);
        return user;
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByUsername(String username) throws ResourceNotFoundException {
        if (userRepository.findUserByUsername(username).isPresent()) {
            return userRepository.findUserByUsername(username);
        } else {
            throw new ResourceNotFoundException("User", "username", username);
        }
    }

    @Transactional(readOnly = true)
    public User findUserById(Integer id) {
        return userRepository.findUserById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Transactional(readOnly = true)
    public Player findPlayerByUsername(String username) {
        return userRepository.findPlayerByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Player", "username", username));
    }

    @Transactional(readOnly = true)
    public Player findPlayerByUserId(int id) {
        return userRepository.findPlayerByUserId(id)
            .orElseThrow(() -> new ResourceNotFoundException("Player", "ID", id));
    }

    @Transactional(readOnly = true)
    public User findCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new ResourceNotFoundException("Nobody authenticated!");
        } else {
            return userRepository.findUserByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "Username", auth.getName()));
        }
    }

    public Boolean existsUserByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public Page<User> findAllPagination(Pageable pageable) {
        return userRepository.findAllPagination(pageable);
    }

    public Page<User> findAllUserByAuthorityPagination(String auth, Pageable pageable) {
        return userRepository.findAllUserByAuthorityPagination(auth, pageable);
    }

    @Transactional(readOnly = true)
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public Iterable<User> findAllUserByAuthority(String auth) {
        return userRepository.findAllUserByAuthority(auth);
    }

    @Transactional
    public User updateUser(@Valid User user, Integer idToUpdate) {
        User toUpdate = findUserById(idToUpdate);
        User Updater = findCurrentUser();
        if (Updater.getAuthority().getId() == 2) {
            if (idToUpdate == Updater.getId()) {
                if (!user.getPassword().equals(toUpdate.getPassword())) { // si password ha cambiado, la codificamos
                    user.setPassword(encoder.encode(user.getPassword()));
                    BeanUtils.copyProperties(user, toUpdate, "id");
                } else { // si password no ha cambiado, no la codificamos otra vez
                    BeanUtils.copyProperties(user, toUpdate, "id", "password");
                }
                userRepository.save(toUpdate);
            } else {
                throw new AccessDeniedException("You cannot update another player");
            }
        } else {
            if (idToUpdate == Updater.getId()) {
                if (!user.getPassword().equals(toUpdate.getPassword())) { // si password ha cambiado, la codificamos
                    user.setPassword(encoder.encode(user.getPassword()));
                    BeanUtils.copyProperties(user, toUpdate, "id");
                } else { // si password no ha cambiado, no la codificamos otra vez
                    BeanUtils.copyProperties(user, toUpdate, "id", "password");
                }
                userRepository.save(toUpdate);
            } else if (toUpdate.getAuthority().getId() != 1) {
                if (!user.getPassword().equals(toUpdate.getPassword())) { // si password ha cambiado, la codificamos
                    user.setPassword(encoder.encode(user.getPassword()));
                    BeanUtils.copyProperties(user, toUpdate, "id");
                } else { // si password no ha cambiado, no la codificamos otra vez
                    BeanUtils.copyProperties(user, toUpdate, "id", "password");
                }
                userRepository.save(toUpdate);
            } else {
                throw new AccessDeniedException("You cannot udpate another admin");
            }
        }
        return toUpdate;
    }

    @Transactional
    public void deleteUser(Integer id) {
        User toDelete = findUserById(id);
        User deleter = findCurrentUser();
        if (deleter.getAuthority().getId() == 2) {
            if (id == deleter.getId()) {
                deleteRelations(id, toDelete.getAuthority().getAuthority());
                this.userRepository.delete(toDelete);
            } else {
                throw new AccessDeniedException("You cannot delete another player");
            }
        } else {
            if (id == deleter.getId()) {
                deleteRelations(id, toDelete.getAuthority().getAuthority());
                this.userRepository.delete(toDelete);
            } else if (toDelete.getAuthority().getId() != 1) {
                deleteRelations(id, toDelete.getAuthority().getAuthority());
                this.userRepository.delete(toDelete);
            } else {
                throw new AccessDeniedException("You cannot delete another admin");
            }
        }
    }

    private void deleteRelations(Integer id, String auth) {
        Player player = userRepository.findPlayerByUserId(id).get();
        // ponemos a null el jugador de la lista de jugadores de las partidas que haya jugado
        List<Game> games = gameRepository.findGamesByPlayerId(player.getId());
        if (!games.isEmpty()) {
            for (Game game : games) {
                game.removePlayerById(player.getId());
                game.removePlayerQuitterById(player.getId());
                game.removePlayerSpectatorById(player.getId());
                gameRepository.save(game);
            }
        }
        playerRepository.delete(player);
    }

}
