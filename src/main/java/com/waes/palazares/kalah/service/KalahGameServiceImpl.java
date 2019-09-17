package com.waes.palazares.kalah.service;

import com.waes.palazares.kalah.domain.GameState;
import com.waes.palazares.kalah.domain.KalahGameRecord;
import com.waes.palazares.kalah.exception.*;
import com.waes.palazares.kalah.repository.KalahGameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;

/**
 * Implementation of {@code KalahGameService} interface.
 * Integrates with reactive CRUD storage, makes validation checks, builds game results
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KalahGameServiceImpl implements KalahGameService {
    private final KalahGameRepository repository;

    public Mono<DifferenceRecord> getDifference(String id) {
        var record = repository.findById(id).switchIfEmpty(Mono.error(new InvalidRecordContentException()));
        var yesResultRecord = record.filter(r -> r.getResult() != null);

        return yesResultRecord
                .switchIfEmpty(record
                        .flatMap(rec -> compare(rec).map(x -> rec.toBuilder().result(x).build()))
                        .flatMap(repository::save));
    }

    private Mono<DifferenceRecord> putRecord(String id, String doc, boolean isLeft) {
        log.debug("Put record request with id: {}", id);


        if (doc == null || doc.trim().isEmpty()) {
            log.debug("Record request with id: {} has empty content", id);
            return Mono.error(new InvalidBase64Exception());
        }

        var decodedDoc = decode(doc);
        var record = repository.findById(id).defaultIfEmpty(DifferenceRecord.builder().id(id).build());

        var sameDocRecord = decodedDoc.flatMap(d ->
                record.filter(rec -> isLeft ? Arrays.equals(rec.getLeft(), d) : Arrays.equals(rec.getRight(), d)));

        return sameDocRecord.switchIfEmpty(
                decodedDoc.flatMap(d -> record
                        .map(rec -> isLeft ? rec.toBuilder().left(d).build() : rec.toBuilder().right(d).build()))
                        .map(rec -> rec.toBuilder().result(null).build())
                        .flatMap(repository::save));
    }

    @Override
    public Mono<KalahGameRecord> create() {
        log.debug("Create new game request");

        return repository.save(KalahGameRecord.builder().build())
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

        var record = repository.findById(gameId).switchIfEmpty(Mono.error(new InvalidRecordException()));

        return record.map(r -> makeMove(r, pitId)).doOnError(e -> log.debug("Error during move request: {}", e.getMessage()));
    }

    private static KalahGameRecord makeMove(KalahGameRecord game, int pitId) throws GameFinishedException, InvalidMoveException {
        if (game.getState() == GameState.FINISHED) {
            throw new GameFinishedException();
        }
        if (game.getState() == GameState.NORTH_TURN && pitId < 8) {
            throw new InvalidMoveException();
        }
        if (game.getState() == GameState.SOUTH_TURN && pitId > 6) {
            throw new InvalidMoveException();
        }

        var status = game.getStatus();
        var pitValue = status[pitId - 1];

        if (pitValue < 1) {
            throw new InvalidMoveException();
        }

        // seed
        int i = 1;
        for (; i <= pitValue; i++) {
            status[(pitId + i) % 14]++;
        }
        // capture
        if (status[i] == 1 &&
                (game.getState() == GameState.SOUTH_TURN && pitId < 6 ||
                        game.getState() == GameState.NORTH_TURN && pitId > 6 && pitId < 13)) {

        }

        return game.toBuilder().state(getNewState(game, i)).build();
    }

    private static GameState getNewState(KalahGameRecord game, int lastPit) throws InvalidMoveException {
        GameState newState;
        if (isGameFinished(game.getStatus())) {
            newState = GameState.FINISHED;
        } else if (lastPit == 6 || lastPit == 13) {
            newState = game.getState();
        } else {
            newState = invertState(game.getState());
        }

        return newState;
    }

    private static boolean isGameFinished(int[] status) {
        return false;
    }

    private static GameState invertState(GameState state) throws InvalidMoveException {
        switch (state) {
            case NORTH_TURN:
                return GameState.SOUTH_TURN;
            case SOUTH_TURN:
                return GameState.NORTH_TURN;
            default:
                throw new InvalidMoveException();
        }
    }
}
