package com.waes.palazares.kalah.repository;

import com.waes.palazares.kalah.domain.GameState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

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
        // arrange
        DifferenceRecord sample = DifferenceRecord.builder()
                .id("testId")
                .left("leftContent".getBytes())
                .right("rightContent".getBytes())
                .result(DifferenceResult.builder().type(GameState.DIFFERENT_SIZE).message("testMessage").build())
                .build();
        // act
        repository.save(sample).block(Duration.ofSeconds(30));
        // assert
        DifferenceRecord result = mongoTemplate.findById("testId", DifferenceRecord.class).block();
        assertNotNull(result);
        assertEquals("testId", result.getId());
        assertArrayEquals("leftContent".getBytes(), result.getLeft());
        assertArrayEquals("rightContent".getBytes(), result.getRight());
        assertEquals(GameState.DIFFERENT_SIZE, result.getResult().getType());
        assertEquals("testMessage", result.getResult().getMessage());
    }
}