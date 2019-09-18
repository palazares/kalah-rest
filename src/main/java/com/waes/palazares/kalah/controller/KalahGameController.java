package com.waes.palazares.kalah.controller;

import com.waes.palazares.kalah.domain.KalahGameRecord;
import com.waes.palazares.kalah.domain.KalahGameState;
import com.waes.palazares.kalah.service.KalahGameService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Kalah game endpoints controller
 * Provides post endpoint to start a game and put endpoint to make turns
 */
@Api("Kalah game endpoints. Create a game and start making turns")
@RestController
@RequestMapping("/")
public class KalahGameController {
    private KalahGameService service;

    @Autowired
    public KalahGameController(KalahGameService service) {
        this.service = service;
    }

    /**
     * Endpoint to make a turn
     *
     * @param gameId id of the game
     * @param pitId  pit number for a turn
     * @return Kalah game state after a turn
     */
    @PutMapping("/games/{gameId}/pits/{pitId}")
    public Mono<KalahGameState> makeTurn(@PathVariable String gameId, @PathVariable String pitId, ServerHttpRequest request) {
        return service.move(gameId, pitId).map(x -> toState(x, request.getURI()));
    }

    /**
     * Endpoint to create a Kalah game
     *
     * @return New Kalah game state
     */
    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<KalahGameState> createGame(ServerHttpRequest request) {
        return service.create().map(x -> toState(x, request.getURI()));
    }

    private static KalahGameState toState(KalahGameRecord gameRecord, URI baseUrl) {
        var id = gameRecord.getId().toString();
        var url = baseUrl.toString() + "/" + id;
        var status = IntStream.range(0, 14)
                .collect(HashMap<Integer, Integer>::new, (m, i) -> m.put(i + 1, gameRecord.getStatus()[i]), Map::putAll);
        return new KalahGameState(id, url, status);
    }
}
