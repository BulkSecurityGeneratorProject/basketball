package com.stucom.basketball.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.stucom.basketball.domain.GameRating;
import com.stucom.basketball.service.GameRatingService;
import com.stucom.basketball.web.rest.util.HeaderUtil;
import com.stucom.basketball.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing GameRating.
 */
@RestController
@RequestMapping("/api")
public class GameRatingResource {

    private final Logger log = LoggerFactory.getLogger(GameRatingResource.class);
        
    @Inject
    private GameRatingService gameRatingService;

    /**
     * POST  /game-ratings : Create a new gameRating.
     *
     * @param gameRating the gameRating to create
     * @return the ResponseEntity with status 201 (Created) and with body the new gameRating, or with status 400 (Bad Request) if the gameRating has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/game-ratings")
    @Timed
    public ResponseEntity<GameRating> createGameRating(@RequestBody GameRating gameRating) throws URISyntaxException {
        log.debug("REST request to save GameRating : {}", gameRating);
        if (gameRating.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("gameRating", "idexists", "A new gameRating cannot already have an ID")).body(null);
        }
        GameRating result = gameRatingService.save(gameRating);
        return ResponseEntity.created(new URI("/api/game-ratings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("gameRating", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /game-ratings : Updates an existing gameRating.
     *
     * @param gameRating the gameRating to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated gameRating,
     * or with status 400 (Bad Request) if the gameRating is not valid,
     * or with status 500 (Internal Server Error) if the gameRating couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/game-ratings")
    @Timed
    public ResponseEntity<GameRating> updateGameRating(@RequestBody GameRating gameRating) throws URISyntaxException {
        log.debug("REST request to update GameRating : {}", gameRating);
        if (gameRating.getId() == null) {
            return createGameRating(gameRating);
        }
        GameRating result = gameRatingService.save(gameRating);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("gameRating", gameRating.getId().toString()))
            .body(result);
    }

    /**
     * GET  /game-ratings : get all the gameRatings.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of gameRatings in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/game-ratings")
    @Timed
    public ResponseEntity<List<GameRating>> getAllGameRatings(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of GameRatings");
        Page<GameRating> page = gameRatingService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/game-ratings");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /game-ratings/:id : get the "id" gameRating.
     *
     * @param id the id of the gameRating to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the gameRating, or with status 404 (Not Found)
     */
    @GetMapping("/game-ratings/{id}")
    @Timed
    public ResponseEntity<GameRating> getGameRating(@PathVariable Long id) {
        log.debug("REST request to get GameRating : {}", id);
        GameRating gameRating = gameRatingService.findOne(id);
        return Optional.ofNullable(gameRating)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /game-ratings/:id : delete the "id" gameRating.
     *
     * @param id the id of the gameRating to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/game-ratings/{id}")
    @Timed
    public ResponseEntity<Void> deleteGameRating(@PathVariable Long id) {
        log.debug("REST request to delete GameRating : {}", id);
        gameRatingService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("gameRating", id.toString())).build();
    }

}
