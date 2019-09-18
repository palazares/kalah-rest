package com.waes.palazares.kalah;

import com.waes.palazares.kalah.domain.GameState;
import com.waes.palazares.kalah.domain.KalahGameRecord;
import com.waes.palazares.kalah.exception.GameFinishedException;
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
    public void shouldThrowGameFinishedException() throws GameFinishedException {
        //given
        var id = UUID.randomUUID();
        var status = new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
        var state = GameState.FINISHED;
        var game = new KalahGameRecord(id, status, state);
        //when
        KalahGame.makeMove(game, 1);
    }

    @Test
    public void shouldMakeSimpleMove() throws GameFinishedException {
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
}