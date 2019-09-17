package com.waes.palazares.kalah.service;

import com.waes.palazares.kalah.domain.KalahGameRecord;
import reactor.core.publisher.Mono;

/**
 * {@code KalahGameService} interface defines methods to play Kalah game
 */
public interface KalahGameService {
    /**
     * Creates a new Kalah game
     *
     * @return persisted Kalah game record
     */
    Mono<KalahGameRecord> create();

    /**
     * Makes a move in Kalah game
     *
     * @param gameId Kalah game id
     * @param pitId  pit id (from 1 to 14) to make a move
     * @return persisted Kalah game record
     */
    Mono<KalahGameRecord> move(String gameId, String pitId);
}
