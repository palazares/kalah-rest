package com.waes.palazares.kalah;

import com.waes.palazares.kalah.domain.GameState;
import com.waes.palazares.kalah.domain.KalahGameRecord;
import com.waes.palazares.kalah.exception.GameFinishedException;
import com.waes.palazares.kalah.exception.InvalidMoveException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@RunWith(JUnit4.class)
public class KalahGameTest {

    @Test(expected = GameFinishedException.class)
    public void shouldThrowGameFinishedException() throws GameFinishedException, InvalidMoveException {
        //given
        var id = UUID.randomUUID();
        var status = new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
        var state = GameState.FINISHED;
        var game = new KalahGameRecord(id, status, state);
        //when
        KalahGame.makeMove(game, 1);
    }

    @Test(expected = InvalidMoveException.class)
    public void shouldThrowInvalidMoveExceptionWhenWrongPlayerTurn() throws GameFinishedException, InvalidMoveException {
        //given
        var id = UUID.randomUUID();
        var status = new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
        var state = GameState.NORTH_TURN;
        var game = new KalahGameRecord(id, status, state);
        //when
        KalahGame.makeMove(game, 1);
    }

    @Test(expected = InvalidMoveException.class)
    public void shouldThrowInvalidMoveExceptionWhenOtherWrongPlayerTurn() throws GameFinishedException, InvalidMoveException {
        //given
        var id = UUID.randomUUID();
        var status = new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
        var state = GameState.SOUTH_TURN;
        var game = new KalahGameRecord(id, status, state);
        //when
        KalahGame.makeMove(game, 9);
    }

    @Test(expected = InvalidMoveException.class)
    public void shouldThrowInvalidMoveExceptionWhenEmptyPit() throws GameFinishedException, InvalidMoveException {
        //given
        var id = UUID.randomUUID();
        var status = new int[]{0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0};
        var state = GameState.NORTH_TURN;
        var game = new KalahGameRecord(id, status, state);
        //when
        KalahGame.makeMove(game, 1);
    }

    @Test
    public void shouldMakeSimpleMove() throws GameFinishedException, InvalidMoveException {
        //given
        var id = UUID.randomUUID();
        var status = new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
        var state = GameState.SOUTH_TURN;
        var game = new KalahGameRecord(id, status, state);
        //when
        var resultGame = KalahGame.makeMove(game, 2);
        //then
        assertNotNull(resultGame);
        assertEquals(id, resultGame.getId());
        assertEquals(GameState.NORTH_TURN, resultGame.getState());
        Assert.assertArrayEquals(new int[]{6, 0, 7, 7, 7, 7, 1, 7, 6, 6, 6, 6, 6, 0}, resultGame.getStatus());
    }

    @Test
    public void shouldMakeComplexMove() throws GameFinishedException, InvalidMoveException {
        //given
        var id = UUID.randomUUID();
        var status = new int[]{6, 0, 7, 7, 7, 7, 1, 7, 6, 6, 6, 6, 6, 0};
        var state = GameState.NORTH_TURN;
        var game = new KalahGameRecord(id, status, state);
        //when
        var resultGame = KalahGame.makeMove(game, 11);
        //then
        assertNotNull(resultGame);
        assertEquals(id, resultGame.getId());
        assertEquals(GameState.SOUTH_TURN, resultGame.getState());
        Assert.assertArrayEquals(new int[]{7, 1, 8, 7, 7, 7, 1, 7, 6, 6, 0, 7, 7, 1}, resultGame.getStatus());
    }

    @Test
    public void shouldRepeatTurnWhenLastPitIsStorage() throws GameFinishedException, InvalidMoveException {
        //given
        var id = UUID.randomUUID();
        var status = new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
        var state = GameState.SOUTH_TURN;
        var game = new KalahGameRecord(id, status, state);
        //when
        var resultGame = KalahGame.makeMove(game, 1);
        //then
        assertNotNull(resultGame);
        assertEquals(id, resultGame.getId());
        assertEquals(GameState.SOUTH_TURN, resultGame.getState());
        Assert.assertArrayEquals(new int[]{0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0}, resultGame.getStatus());
    }

    @Test
    public void shouldCaptureWhenLastPitIsEmptyAndOwned() throws GameFinishedException, InvalidMoveException {
        //given
        var id = UUID.randomUUID();
        var status = new int[]{6, 2, 6, 0, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
        var state = GameState.SOUTH_TURN;
        var game = new KalahGameRecord(id, status, state);
        //when
        var resultGame = KalahGame.makeMove(game, 2);
        //then
        assertNotNull(resultGame);
        assertEquals(id, resultGame.getId());
        assertEquals(GameState.NORTH_TURN, resultGame.getState());
        Assert.assertArrayEquals(new int[]{6, 0, 7, 0, 6, 6, 7, 6, 6, 0, 6, 6, 6, 0}, resultGame.getStatus());
    }

    @Test
    public void shouldNotCaptureWhenLastPitIsEmptyAndNotOwned() throws GameFinishedException, InvalidMoveException {
        //given
        var id = UUID.randomUUID();
        var status = new int[]{0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0};
        var state = GameState.NORTH_TURN;
        var game = new KalahGameRecord(id, status, state);
        //when
        var resultGame = KalahGame.makeMove(game, 9);
        //then
        assertNotNull(resultGame);
        assertEquals(id, resultGame.getId());
        assertEquals(GameState.SOUTH_TURN, resultGame.getState());
        Assert.assertArrayEquals(new int[]{1, 7, 7, 7, 7, 7, 1, 6, 0, 7, 7, 7, 7, 1}, resultGame.getStatus());
    }

    @Test
    public void shouldFinishTheGameWhenMoreThanHalfOwned() throws GameFinishedException, InvalidMoveException {
        //given
        var id = UUID.randomUUID();
        var status = new int[]{0, 0, 0, 0, 2, 0, 36, 1, 1, 1, 1, 0, 0, 30};
        var state = GameState.SOUTH_TURN;
        var game = new KalahGameRecord(id, status, state);
        //when
        var resultGame = KalahGame.makeMove(game, 5);
        //then
        assertNotNull(resultGame);
        assertEquals(id, resultGame.getId());
        assertEquals(GameState.FINISHED, resultGame.getState());
        Assert.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 1, 37, 1, 1, 1, 1, 0, 0, 30}, resultGame.getStatus());
    }

    @Test
    public void shouldFinishTheGameWhenAllOwnedAreEmpty() throws GameFinishedException, InvalidMoveException {
        //given
        var id = UUID.randomUUID();
        var status = new int[]{0, 0, 0, 0, 0, 1, 34, 1, 1, 1, 1, 1, 1, 31};
        var state = GameState.SOUTH_TURN;
        var game = new KalahGameRecord(id, status, state);
        //when
        var resultGame = KalahGame.makeMove(game, 6);
        //then
        assertNotNull(resultGame);
        assertEquals(id, resultGame.getId());
        assertEquals(GameState.FINISHED, resultGame.getState());
        Assert.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0, 35, 1, 1, 1, 1, 1, 1, 31}, resultGame.getStatus());
    }
}