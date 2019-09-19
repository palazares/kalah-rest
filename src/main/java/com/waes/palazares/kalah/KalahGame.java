package com.waes.palazares.kalah;

import com.waes.palazares.kalah.domain.GameState;
import com.waes.palazares.kalah.domain.KalahGameRecord;
import com.waes.palazares.kalah.exception.GameFinishedException;
import com.waes.palazares.kalah.exception.InvalidMoveException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Utility class responsible for Kalah game process.
 * It updates the game status and internal state after the move
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class KalahGame {
    /**
     * Updates the game status and internal state using the move
     *
     * @param game  Game instance to update
     * @param pitId Pit id of the move
     * @return New game instance with updated values
     * @throws GameFinishedException when the game is in finished state
     */
    public static KalahGameRecord makeMove(KalahGameRecord game, int pitId) throws GameFinishedException, InvalidMoveException {
        if (game.getState() == GameState.FINISHED) {
            log.debug("Move request for already finished game");
            throw new GameFinishedException();
        }
        if (game.getState() == GameState.NORTH_TURN && pitId < 8) {
            log.debug("Move request for wrong player. pit: {}", pitId);
            throw new InvalidMoveException();
        }
        if (game.getState() == GameState.SOUTH_TURN && pitId > 6) {
            log.debug("Move request for wrong player. pit: {}", pitId);
            throw new InvalidMoveException();
        }
        if (game.getStatus()[pitId - 1] < 1) {
            log.debug("Move request for empty pit: {}", pitId);
            throw new InvalidMoveException();
        }

        var status = Arrays.copyOf(game.getStatus(), 14);
        var pitValue = status[pitId - 1];

        status[pitId - 1] = 0;
        IntStream.range(pitId, pitId + pitValue).forEach(i -> status[i % 14]++);

        var lastPitIndex = (pitId + pitValue - 1) % 14;
        captureIfrequired(status, game.getState(), lastPitIndex);

        return new KalahGameRecord(game.getId(), status, getNewState(status, game.getState(), lastPitIndex));
    }

    private static void captureIfrequired(int[] status, GameState state, int lastPitIndex) {
        if (status[lastPitIndex] != 1 ||
                ((state != GameState.SOUTH_TURN || lastPitIndex >= 6) &&
                        (state != GameState.NORTH_TURN || lastPitIndex <= 6))) {
            return;
        }
        // 5 - (lastPitIndex % 7) mirrors the index: 0-5, 1-4, 2-3, 3-2, 4-1, 5-0
        // (((lastPitIndex + 7) % 14) / 7) == 0 for 7,8,9,10,11,12
        // (((lastPitIndex + 7) % 14) / 7) == 1 for 0,1,2,3,4,5
        var capturedIndex = 5 - (lastPitIndex % 7) + 7 * (((lastPitIndex + 7) % 14) / 7);
        var storageIndex = 7 * ((lastPitIndex / 7) + 1) - 1;
        status[storageIndex] += status[capturedIndex] + 1;
        status[lastPitIndex] = 0;
        status[capturedIndex] = 0;
    }

    private static GameState getNewState(int[] status, GameState state, int lastPitIndex) throws GameFinishedException {
        if (isGameFinished(status)) {
            return GameState.FINISHED;
        }
        if (lastPitIndex == 6 || lastPitIndex == 13) {
            return state;
        }

        return invertState(state);
    }

    private static boolean isGameFinished(int[] status) {
        var southTotalInGame = Arrays.stream(status, 0, 6).sum();
        var northTotalInGame = Arrays.stream(status, 7, 14).sum();
        var halfTotal = Arrays.stream(status).sum() / 2;

        return southTotalInGame == 0 || northTotalInGame == 0 || status[6] > halfTotal || status[13] > halfTotal;
    }

    private static GameState invertState(GameState state) throws GameFinishedException {
        switch (state) {
            case NORTH_TURN:
                return GameState.SOUTH_TURN;
            case SOUTH_TURN:
                return GameState.NORTH_TURN;
            default:
                throw new GameFinishedException();
        }
    }
}
