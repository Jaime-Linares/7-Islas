package org.juego.siete.islas.island;

import org.juego.siete.islas.card.Card;
import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class IslandService {

    private final IslandRepository islandRepository;

    @Autowired
    public IslandService(IslandRepository islandRepository) {
        this.islandRepository = islandRepository;
    }


    @Transactional(readOnly = true)
    public Iterable<Island> findAll() throws DataAccessException {
        return islandRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Island findIslandById(int id) throws DataAccessException {
        return islandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Island", "ID", id));
    }

    @Transactional(readOnly = true)
    public List<Card> findCardsByIslandId(int islandId) {
        return this.islandRepository.findCardsByIslandId(islandId);
    }

    @Transactional
    public Island saveIsland(Island island) throws DataAccessException {
        islandRepository.save(island);
        return island;
    }

    @Transactional
    public void deleteIsland(int id) throws DataAccessException {
        Island island = findIslandById(id);
        islandRepository.delete(island);
    }

    @Transactional(readOnly = true)
    public List<Island> findIslandsByGameId(Integer gameId) {
        return islandRepository.findIslandByGameId(gameId);
    }
    
}
