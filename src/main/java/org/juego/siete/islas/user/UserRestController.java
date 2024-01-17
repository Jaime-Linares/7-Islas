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

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.juego.siete.islas.auth.payload.response.MessageResponse;
import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.juego.siete.islas.player.Player;
import org.juego.siete.islas.util.RestPreconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "API for the management of users")
@SecurityRequirement(name = "bearerAuth")
class UserRestController {

    private final UserService userService;
    private final AuthoritiesService authService;

    @Autowired
    public UserRestController(UserService userService, AuthoritiesService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<Page<User>> findAll(@RequestParam(required = false) String auth, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
        Page<User> res;

        if (auth != null) {
            res = userService.findAllUserByAuthorityPagination(auth, PageRequest.of(page, pageSize));
        } else {
            res = userService.findAllPagination(PageRequest.of(page, pageSize));
        }

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("authorities")
    public ResponseEntity<List<Authorities>> findAllAuths() {
        List<Authorities> res = (List<Authorities>) authService.findAll();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<User> findById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
    }

    @GetMapping("/{userId}/player")
    public ResponseEntity<Player> findPlayerByUserId(@PathVariable("userId") Integer id) {
        return new ResponseEntity<>(userService.findPlayerByUserId(id), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> create(@RequestBody @Valid User user) {
        User savedUser = userService.saveUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping(value = "{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> update(@PathVariable("userId") Integer id, @RequestBody @Valid User user) {
        try {
            User repUser = userService.findUserByUsername(user.getUsername()).orElse(null);
            if (repUser != null) {
                if (userService.existsUserByUsername(user.getUsername()).equals(true) && repUser.getId() != user.getId()) {
                    return ResponseEntity.badRequest()
                        .body(new MessageResponse("Este nombre de usuario ya ha sido escogido, prueba con otro."));
                }
            }
            RestPreconditions.checkNotNull(userService.findUserById(id), "User", "ID", id);
            this.userService.updateUser(user, id);
            return ResponseEntity.ok(new MessageResponse("User edited successfully!"));
        } catch (ResourceNotFoundException noUsuario) {
            RestPreconditions.checkNotNull(userService.findUserById(id), "User", "ID", id);
            this.userService.updateUser(user, id);
            return ResponseEntity.ok(new MessageResponse("User edited successfully!"));
        }
    }

    @DeleteMapping(value = "{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> delete(@PathVariable("userId") int id) {
        RestPreconditions.checkNotNull(userService.findUserById(id), "User", "ID", id);
        userService.deleteUser(id);
        return new ResponseEntity<>(new MessageResponse("User deleted!"), HttpStatus.OK);
    }

}
