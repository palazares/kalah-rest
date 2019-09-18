package com.waes.palazares.kalah.repository;

import com.waes.palazares.kalah.domain.GameState;
import com.waes.palazares.kalah.domain.KalahGameRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataMongoTest
public class KalahGameRepositoryTest {
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @Autowired
    private KalahGameRepository repository;

    @Test
    public void shouldReturnSaved() {
        // given
        var id = UUID.randomUUID();
        var status = new int[]{5, 7, 6, 6, 6, 6, 0, 5, 7, 6, 6, 6, 6, 0};
        var state = GameState.FINISHED;
        var sample = new KalahGameRecord(id, status, state);
        // when
        repository.save(sample).block(Duration.ofSeconds(30));
        // then
        KalahGameRecord result = mongoTemplate.findById(sample.getId(), KalahGameRecord.class).block(Duration.ofSeconds(30));
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(state, result.getState());
        assertArrayEquals(status, result.getStatus());
    }
}