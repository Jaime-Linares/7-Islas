package org.juego.siete.islas.island;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.juego.siete.islas.auth.payload.response.MessageResponse;
import org.juego.siete.islas.card.Card;
import org.juego.siete.islas.util.RestPreconditions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/islands")
@Tag(name = "Islands", description = "API for the management of islands")
@SecurityRequirement(name = "bearerAuth")
public class IslandRestController {

    private final IslandService islandService;

    @Autowired
    public IslandRestController(IslandService islandService) {
        this.islandService = islandService;
    }

    @GetMapping
    public ResponseEntity<List<Island>> findAll() {
        return new ResponseEntity<>((List<Island>) islandService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/{islandId}")
    public ResponseEntity<Island> findById(@PathVariable("islandId") int id) {
        return new ResponseEntity<>(islandService.findIslandById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/game/{gameId}")
    public ResponseEntity<List<Island>> findIslandsByGameId(@PathVariable("gameId") Integer gameId) {
        return new ResponseEntity<>(islandService.findIslandsByGameId(gameId), HttpStatus.OK);
    }

    @GetMapping(value = "/{islandId}/cards")
    public ResponseEntity<List<Card>> findCardsByIslandId(@PathVariable("islandId") int islandId) {
        return new ResponseEntity<>(this.islandService.findCardsByIslandId(islandId), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Island> create(@RequestBody @Valid Island island) throws URISyntaxException {
        Island newIsland = new Island();
        BeanUtils.copyProperties(island, newIsland, "id");
        Island savedIsland = this.islandService.saveIsland(newIsland);

        return new ResponseEntity<>(savedIsland, HttpStatus.CREATED);
    }

    @DeleteMapping("/{islandId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> delete(@PathVariable("islandId") int id) {
        RestPreconditions.checkNotNull(islandService.findIslandById(id), "Island", "ID", id);
        islandService.deleteIsland(id);
        return new ResponseEntity<>(new MessageResponse("Island deleted!"), HttpStatus.OK);
    }

}
