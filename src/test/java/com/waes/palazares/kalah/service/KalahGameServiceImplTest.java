package com.waes.palazares.kalah.service;

import com.waes.palazares.kalah.domain.GameState;
import com.waes.palazares.kalah.domain.KalahGameRecord;
import com.waes.palazares.kalah.exception.*;
import com.waes.palazares.kalah.repository.KalahGameRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KalahGameServiceImplTest {
    @InjectMocks
    private KalahGameServiceImpl service;

    @Mock
    private KalahGameRepository repository;

    private static String id = UUID.randomUUID().toString();

    @Test
    public void shouldThrowInvalidIdWhenGameIdIsNull() {
        StepVerifier
                .create(service.move(null, "0"))
                .expectError(InavlidIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidIdWhenGameIdIsEmpty() {
        StepVerifier
                .create(service.move("", "0"))
                .expectError(InavlidIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidIdWhenPitIdIsNull() {
        StepVerifier
                .create(service.move(id, null))
                .expectError(InavlidIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidIdWhenPitIdIsEmpty() {
        StepVerifier
                .create(service.move(id, ""))
                .expectError(InavlidIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidPitIdWhenPitIdIsNotANumber() {
        StepVerifier
                .create(service.move(id, "x"))
                .expectError(InvalidPitIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidPitIdWhenPitIdIsNegative() {
        StepVerifier
                .create(service.move(id, "-1"))
                .expectError(InvalidPitIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidPitIdWhenPitIdIsBiggerThan13() {
        StepVerifier
                .create(service.move(id, "14"))
                .expectError(InvalidPitIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidPitIdWhenPitIdIsStorage() {
        StepVerifier
                .create(service.move(id, "7"))
                .expectError(InvalidPitIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidRecordExceptionWhenNoRecordFound() {
        // given
        when(repository.findById(any(UUID.class))).thenReturn(Mono.empty());
        // when, then
        StepVerifier
                .create(service.move(id, "5"))
                .expectError(InvalidRecordException.class)
                .verify();
    }

    @Test
    public void shouldThrowGameFinishedExceptionWhenGameFinished() {
        // given
        var gameId = UUID.randomUUID();
        var status = new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
        var state = GameState.FINISHED;
        var game = new KalahGameRecord(gameId, status, state);
        when(repository.findById(eq(gameId))).thenReturn(Mono.just(game));
        // when, then
        StepVerifier
                .create(service.move(gameId.toString(), "5"))
                .expectError(GameFinishedException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidMoveExceptionWhenSouthTurnRequested() {
        // given
        var gameId = UUID.randomUUID();
        var status = new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
        var state = GameState.NORTH_TURN;
        var game = new KalahGameRecord(gameId, status, state);
        when(repository.findById(eq(gameId))).thenReturn(Mono.just(game));
        // when, then
        StepVerifier
                .create(service.move(gameId.toString(), "3"))
                .expectError(InvalidMoveException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidMoveExceptionWhenNorthTurnRequested() {
        // given
        var gameId = UUID.randomUUID();
        var status = new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
        var state = GameState.SOUTH_TURN;
        var game = new KalahGameRecord(gameId, status, state);
        when(repository.findById(eq(gameId))).thenReturn(Mono.just(game));
        // when, then
        StepVerifier
                .create(service.move(gameId.toString(), "9"))
                .expectError(InvalidMoveException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidMoveExceptionWhenPitIsEmpty() {
        // given
        var gameId = UUID.randomUUID();
        var status = new int[]{6, 6, 0, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
        var state = GameState.SOUTH_TURN;
        var game = new KalahGameRecord(gameId, status, state);
        when(repository.findById(eq(gameId))).thenReturn(Mono.just(game));
        // when, then
        StepVerifier
                .create(service.move(gameId.toString(), "3"))
                .expectError(InvalidMoveException.class)
                .verify();
    }

    @Test
    public void shouldCreateNewGame() {
        //given
        var game = new KalahGameRecord();
        when(repository.save(any())).thenReturn(Mono.just(game));
        //when
        StepVerifier
                .create(service.create())
                .expectNextMatches(x -> x.getState().equals(GameState.SOUTH_TURN) &&
                        x.getId() == game.getId() &&
                        Arrays.equals(x.getStatus(), new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0})
                )
                .expectComplete()
                .verify();
        //then
        verify(repository, times(1)).save(any());
        verify(repository, times(0)).findById(any(UUID.class));
    }

    @Test
    public void shouldMakeAMove() {
        //given
        var gameId = UUID.randomUUID();
        var status = new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
        var state = GameState.SOUTH_TURN;
        var game = new KalahGameRecord(gameId, status, state);
        when(repository.findById(eq(gameId))).thenReturn(Mono.just(game));
        //when
        StepVerifier
                .create(service.move(gameId.toString(), "1"))
                .expectNextMatches(x -> !x.equals(game))
                .expectComplete()
                .verify();
        //then
        verify(repository).findById(eq(gameId));
        verify(repository, times(0)).save(any());
    }
}