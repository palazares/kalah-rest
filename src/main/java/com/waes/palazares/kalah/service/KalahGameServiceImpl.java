package com.waes.palazares.kalah.service;

import com.waes.palazares.kalah.KalahGame;
import com.waes.palazares.kalah.domain.KalahGameRecord;
import com.waes.palazares.kalah.exception.*;
import com.waes.palazares.kalah.repository.KalahGameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Implementation of {@code KalahGameService} interface.
 * Integrates with reactive CRUD storage, makes validation checks, builds game results
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KalahGameServiceImpl implements KalahGameService {
    private final KalahGameRepository repository;

    @Override
    public Mono<KalahGameRecord> create() {
        log.debug("Create new game request");

        return repository.save(new KalahGameRecord())
                .doOnSuccess(x -> log.debug("New game {} has been created", x.getId()))
                .doOnError(e -> log.debug("Error during creating a new game: {}", e.getMessage()));
    }

    @Override
    public Mono<KalahGameRecord> move(String gameId, String pitIdString) {
        log.debug("Move request {} for game {}", pitIdString, gameId);

        if (gameId == null || gameId.trim().isEmpty()) {
            log.debug("Move request has empty game id");
            return Mono.error(new InavlidIdException());
        }

        UUID id;
        try {
            id = UUID.fromString(gameId);
        } catch (Exception e) {
            log.debug("Move request has game id not in UUID format");
            return Mono.error(new InavlidIdException());
        }

        if (pitIdString == null || pitIdString.trim().isEmpty()) {
            log.debug("Move request has empty pit id");
            return Mono.error(new InavlidIdException());
        }

        int pitId;
        try {
            pitId = Integer.parseInt(pitIdString);
        } catch (NumberFormatException ex) {
            log.debug("Move request has wrong format pit id");
            return Mono.error(new InvalidPitIdException());
        }

        if (pitId < 1 || pitId == 7 || pitId > 13) {
            log.debug("Move request has pit id outside of allowed range [1-6,8-13]");
            return Mono.error(new InvalidPitIdException());
        }

        var record = repository.findById(id).switchIfEmpty(Mono.error(new InvalidRecordException()));

        return record.flatMap(r -> {
            try {
                return Mono.just(makeMove(r, pitId));
            } catch (Exception e) {
                return Mono.error(e);
            }
        }).doOnSuccess(x -> log.debug("Move {} has been successfully performed", pitId))
                .doOnError(e -> log.debug("Error during move request: {}", e.getMessage()));
    }

    private static KalahGameRecord makeMove(KalahGameRecord game, int pitId) throws GameFinishedException, InvalidMoveException {
        return KalahGame.makeMove(game, pitId);
    }
}
