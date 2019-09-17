package com.waes.palazares.kalah.service;

import com.waes.palazares.kalah.domain.DifferenceRecord;
import com.waes.palazares.kalah.domain.DifferenceResult;
import com.waes.palazares.kalah.domain.GameState;
import com.waes.palazares.kalah.exception.InavlidIdException;
import com.waes.palazares.kalah.exception.InvalidPitIdException;
import com.waes.palazares.kalah.exception.InvalidRecordContentException;
import com.waes.palazares.kalah.repository.kalahGameRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Base64;
import java.util.Objects;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DifferenceServiceImplTest {
    @InjectMocks
    private DifferenceServiceImpl diffService;

    @Mock
    private kalahGameRepository repository;

    @Test
    public void shouldThrowInvalidIdWhenLeftIdIsNull() {
        StepVerifier
                .create(diffService.putLeft(null, null))
                .expectError(InavlidIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidIdWhenLeftIdIsEmpty() {
        StepVerifier
                .create(diffService.putLeft("", null))
                .expectError(InavlidIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidIdWhenRightIdIsNull() {
        StepVerifier
                .create(diffService.putRight(null, null))
                .expectError(InavlidIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidIdWhenRightIdIsEmpty() {
        StepVerifier
                .create(diffService.putRight("", null))
                .expectError(InavlidIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidIdWhenDifIdIsNull() {
        StepVerifier
                .create(diffService.getDifference(null))
                .expectError(InavlidIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidIdWhenDifIdIsEmpty() {
        StepVerifier
                .create(diffService.getDifference(""))
                .expectError(InavlidIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidBase64WhenLeftDocIsNull() {
        StepVerifier
                .create(diffService.putLeft("testID", null))
                .expectError(InvalidPitIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidBase64WhenLeftDocIsEmpty() {
        StepVerifier
                .create(diffService.putLeft("testID", ""))
                .expectError(InvalidPitIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidBase64WhenLeftDocIsInvalidBase64() {
        when(repository.findById(eq("testID"))).thenReturn(Mono.just(DifferenceRecord.builder().build()));
        StepVerifier
                .create(diffService.putLeft("testID", "_- &^%"))
                .expectError(InvalidPitIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidBase64WhenRightDocIsNull() {
        StepVerifier
                .create(diffService.putRight("testID", null))
                .expectError(InvalidPitIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidBase64WhenRightDocIsEmpty() {
        StepVerifier
                .create(diffService.putLeft("testID", ""))
                .expectError(InvalidPitIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidBase64WhenRightDocIsInvalidBase64() {
        when(repository.findById(eq("testID"))).thenReturn(Mono.just(DifferenceRecord.builder().build()));
        StepVerifier
                .create(diffService.putRight("testID", "_- &^%"))
                .expectError(InvalidPitIdException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidRecordContentWhenRecordWasNotFound() {
        //when
        when(repository.findById(eq("testID"))).thenReturn(Mono.empty());
        StepVerifier
                .create(diffService.getDifference("testID"))
                .expectError(InvalidRecordContentException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidRecordContentWhenRightDocIsNull() {
        //given
        DifferenceRecord testIdRecord = DifferenceRecord.builder().id("testID").left("content".getBytes()).build();
        //when
        when(repository.findById(eq("testID"))).thenReturn(Mono.just(testIdRecord));
        StepVerifier
                .create(diffService.getDifference("testID"))
                .expectError(InvalidRecordContentException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidRecordContentWhenRightDocIsEmpty() {
        //given
        DifferenceRecord testIdRecord = DifferenceRecord.builder()
                .id("testID")
                .left("content".getBytes())
                .right(new byte[0])
                .build();
        //when
        when(repository.findById(eq("testID"))).thenReturn(Mono.just(testIdRecord));
        StepVerifier
                .create(diffService.getDifference("testID"))
                .expectError(InvalidRecordContentException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidRecordContentWhenLeftDocIsNull() {
        //given
        DifferenceRecord testIdRecord = DifferenceRecord.builder()
                .id("testID")
                .right("content".getBytes())
                .build();
        //when
        when(repository.findById(eq("testID"))).thenReturn(Mono.just(testIdRecord));
        StepVerifier
                .create(diffService.getDifference("testID"))
                .expectError(InvalidRecordContentException.class)
                .verify();
    }

    @Test
    public void shouldThrowInvalidRecordContentWhenLeftDocIsEmpty() {
        //given
        DifferenceRecord testIdRecord = DifferenceRecord.builder()
                .id("testID")
                .right("content".getBytes())
                .left(new byte[0])
                .build();
        //when
        when(repository.findById(eq("testID"))).thenReturn(Mono.just(testIdRecord));
        StepVerifier
                .create(diffService.getDifference("testID"))
                .expectError(InvalidRecordContentException.class)
                .verify();
    }

    @Test
    public void shouldNotFailWhenCorrectBase64() {
        //given
        String testId = "testID";
        String leftContent = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        //when
        when(repository.findById(eq(testId))).thenReturn(Mono.empty());
        when(repository.save(any())).thenReturn(Mono.just(DifferenceRecord.builder().build()));
        StepVerifier
                .create(diffService.putLeft(testId, leftContent))
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
        //then
        verify(repository).findById(eq(testId));
        verify(repository).save(ArgumentMatchers.argThat(arg -> {
            assertEquals(testId, arg.getId());
            assertNotNull(arg.getLeft());
            assertNull(arg.getRight());
            assertNull(arg.getResult());
            return true;
        }));
    }

    @Test
    public void shouldSaveNewRecordWhenLeftDoc() {
        //given
        String testId = "testID";
        byte[] leftContent = "testContent".getBytes();
        //when
        when(repository.findById(eq(testId))).thenReturn(Mono.empty());
        when(repository.save(any())).thenReturn(Mono.just(DifferenceRecord.builder().build()));
        StepVerifier
                .create(diffService.putLeft(testId, Base64.getEncoder().encodeToString(leftContent)))
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
        //then
        verify(repository).findById(eq(testId));
        verify(repository).save(ArgumentMatchers.argThat(arg -> {
            assertEquals(testId, arg.getId());
            assertArrayEquals(leftContent, arg.getLeft());
            assertNull(arg.getRight());
            assertNull(arg.getResult());
            return true;
        }));
    }

    @Test
    public void shouldSaveNewRecordWhenRightDoc() {
        //given
        String testId = "testID";
        byte[] rightContent = "testContent".getBytes();
        //when
        when(repository.findById(eq(testId))).thenReturn(Mono.empty());
        when(repository.save(any())).thenReturn(Mono.just(DifferenceRecord.builder().build()));
        StepVerifier
                .create(diffService.putRight(testId, Base64.getEncoder().encodeToString(rightContent)))
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
        //then
        verify(repository).findById(eq(testId));
        verify(repository).save(ArgumentMatchers.argThat(arg -> {
            assertEquals(testId, arg.getId());
            assertArrayEquals(rightContent, arg.getRight());
            assertNull(arg.getLeft());
            assertNull(arg.getResult());
            return true;
        }));
    }

    @Test
    public void shouldUpdateRecordWhenLeftDoc() {
        //given
        String testId = "testID";
        byte[] leftContent = "leftTestContent".getBytes();
        byte[] rightContent = "rightTestContent".getBytes();
        DifferenceRecord differenceRecord = DifferenceRecord.builder()
                .id(testId)
                .left("oldContent".getBytes())
                .right(rightContent)
                .result(DifferenceResult.builder().type(GameState.DIFFERENT_SIZE).message("testResult").build())
                .build();
        //when
        when(repository.findById(eq(testId))).thenReturn(Mono.just(differenceRecord));
        when(repository.save(any())).thenReturn(Mono.just(DifferenceRecord.builder().build()));
        StepVerifier
                .create(diffService.putLeft(testId, Base64.getEncoder().encodeToString(leftContent)))
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
        //then
        verify(repository).findById(eq(testId));
        verify(repository).save(ArgumentMatchers.argThat(arg -> {
            assertEquals(testId, arg.getId());
            assertArrayEquals(leftContent, arg.getLeft());
            assertArrayEquals(rightContent, arg.getRight());
            assertNull(arg.getResult());
            return true;
        }));
    }

    @Test
    public void shouldUpdateRecordWhenRightDoc() {
        //given
        String testId = "testID";
        byte[] leftContent = "leftTestContent".getBytes();
        byte[] rightContent = "rightTestContent".getBytes();
        DifferenceRecord differenceRecord = DifferenceRecord.builder()
                .id(testId)
                .left(leftContent)
                .right("oldContent".getBytes())
                .result(DifferenceResult.builder().type(GameState.DIFFERENT_SIZE).message("testResult").build())
                .build();
        //when
        when(repository.findById(eq(testId))).thenReturn(Mono.just(differenceRecord));
        when(repository.save(any())).thenReturn(Mono.just(DifferenceRecord.builder().build()));
        StepVerifier
                .create(diffService.putRight(testId, Base64.getEncoder().encodeToString(rightContent)))
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
        ;
        //then
        verify(repository).findById(eq(testId));
        verify(repository).save(ArgumentMatchers.argThat(arg -> {
            assertEquals(testId, arg.getId());
            assertArrayEquals(leftContent, arg.getLeft());
            assertArrayEquals(rightContent, arg.getRight());
            assertNull(arg.getResult());
            return true;
        }));
    }

    @Test
    public void shouldNotUpdateRecordResultWhenRightDocIsTheSame() {
        //given
        String testId = "testID";
        byte[] leftContent = "leftTestContent".getBytes();
        byte[] rightContent = "rightTestContent".getBytes();
        DifferenceResult testResult = DifferenceResult.builder().type(GameState.DIFFERENT_SIZE).message("testResult").build();
        DifferenceRecord differenceRecord = DifferenceRecord.builder()
                .id(testId)
                .left(leftContent)
                .right(rightContent)
                .result(testResult)
                .build();
        //when
        when(repository.findById(eq(testId))).thenReturn(Mono.just(differenceRecord));
        StepVerifier
                .create(diffService.putRight(testId, Base64.getEncoder().encodeToString(rightContent)))
                .expectNextMatches(x -> x.equals(differenceRecord))
                .expectComplete()
                .verify();
        //then
        verify(repository).findById(eq(testId));
        verify(repository, times(0)).save(any());
    }

    @Test
    public void shouldNotUpdateRecordResultWhenLeftDocIsTheSame() {
        //given
        String testId = "testID";
        byte[] leftContent = "leftTestContent".getBytes();
        byte[] rightContent = "rightTestContent".getBytes();
        DifferenceResult testResult = DifferenceResult.builder().type(GameState.DIFFERENT_SIZE).message("testResult").build();
        DifferenceRecord differenceRecord = DifferenceRecord.builder()
                .id(testId)
                .left(leftContent)
                .right(rightContent)
                .result(testResult)
                .build();
        //when
        when(repository.findById(eq(testId))).thenReturn(Mono.just(differenceRecord));
        StepVerifier
                .create(diffService.putLeft(testId, Base64.getEncoder().encodeToString(leftContent)))
                .expectNextMatches(x -> x.equals(differenceRecord))
                .expectComplete()
                .verify();
        //then
        verify(repository).findById(eq(testId));
        verify(repository, times(0)).save(any());
    }

    @Test
    public void shouldReturnEqualResultWhenDocsAreTheSame() {
        //given
        String testId = "testID";
        byte[] equalContent = "equalTestContent".getBytes();
        DifferenceRecord differenceRecord = DifferenceRecord.builder()
                .id(testId)
                .left(equalContent)
                .right(equalContent)
                .build();
        //when
        when(repository.findById(eq(testId))).thenReturn(Mono.just(differenceRecord));
        when(repository.save(any())).thenReturn(Mono.just(DifferenceRecord.builder().build()));
        StepVerifier
                .create(diffService.getDifference(testId))
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
        //then
        verify(repository).findById(eq(testId));
        verify(repository).save(ArgumentMatchers.argThat(arg -> {
            assertEquals(testId, arg.getId());
            assertArrayEquals(equalContent, arg.getLeft());
            assertArrayEquals(equalContent, arg.getRight());
            assertEquals(GameState.EQUALS, arg.getResult().getType());
            assertNotNull(arg.getResult().getMessage());
            return true;
        }));
    }

    @Test
    public void shouldReturnDifferentSizeResultWhenDocsAreDifferent() {
        //given
        String testId = "testID";
        byte[] leftContent = "leftTestContent".getBytes();
        byte[] rightContent = "rightTestContent".getBytes();
        DifferenceRecord differenceRecord = DifferenceRecord.builder()
                .id(testId)
                .left(leftContent)
                .right(rightContent)
                .build();
        //when
        when(repository.findById(eq(testId))).thenReturn(Mono.just(differenceRecord));
        when(repository.save(any())).thenReturn(Mono.just(DifferenceRecord.builder().build()));
        StepVerifier
                .create(diffService.getDifference(testId))
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
        //then
        verify(repository).findById(eq(testId));
        verify(repository).save(any());
        verify(repository).save(ArgumentMatchers.argThat(arg -> {
            assertEquals(testId, arg.getId());
            assertArrayEquals(leftContent, arg.getLeft());
            assertArrayEquals(rightContent, arg.getRight());
            assertEquals(GameState.DIFFERENT_SIZE, arg.getResult().getType());
            assertNotNull(arg.getResult().getMessage());
            return true;
        }));
    }

    @Test
    public void shouldReturnDifferentContentResultWhenDocsContentsAreDifferent() {
        //given
        String testId = "testID";
        byte[] leftContent = "leftTestContent".getBytes();
        byte[] rightContent = "ightTestContent".getBytes();
        DifferenceRecord differenceRecord = DifferenceRecord.builder()
                .id(testId)
                .left(leftContent)
                .right(rightContent)
                .build();
        //when
        when(repository.findById(eq(testId))).thenReturn(Mono.just(differenceRecord));
        when(repository.save(any())).thenReturn(Mono.just(DifferenceRecord.builder().build()));
        StepVerifier
                .create(diffService.getDifference(testId))
                .expectNextMatches(Objects::nonNull)
                .expectComplete()
                .verify();
        //then
        verify(repository).findById(eq(testId));
        verify(repository).save(ArgumentMatchers.argThat(arg -> {
            assertEquals(testId, arg.getId());
            assertArrayEquals(leftContent, arg.getLeft());
            assertArrayEquals(rightContent, arg.getRight());
            assertEquals(GameState.DIFFERENT_CONTENT, arg.getResult().getType());
            assertNotNull(arg.getResult().getMessage());
            return true;
        }));
    }

    @Test
    public void shouldNotSaveWhenLeftDocIsTheSame() {
        //given
        String testId = "testID";
        byte[] equalContent = "equalTestContent".getBytes();
        DifferenceRecord differenceRecord = DifferenceRecord.builder()
                .id(testId)
                .left(equalContent)
                .right(equalContent)
                .result(DifferenceResult.builder().type(GameState.EQUALS).message("equals").build())
                .build();
        //when
        when(repository.findById(eq(testId))).thenReturn(Mono.just(differenceRecord));
        StepVerifier
                .create(diffService.getDifference(testId))
                .expectNextMatches(x -> x.equals(differenceRecord))
                .expectComplete()
                .verify();
        //then
        verify(repository).findById(eq(testId));
        verify(repository, times(0)).save(any());
    }

    @Test
    public void shouldNotCompareWhenResultExists() {
        //given
        String testId = "testID";
        byte[] equalContent = "equalTestContent".getBytes();
        DifferenceRecord differenceRecord = DifferenceRecord.builder()
                .id(testId)
                .left(equalContent)
                .right(equalContent)
                .result(DifferenceResult.builder().type(GameState.EQUALS).message("equals").build())
                .build();
        //when
        when(repository.findById(eq(testId))).thenReturn(Mono.just(differenceRecord));
        StepVerifier
                .create(diffService.getDifference(testId))
                .expectNextMatches(x -> x.equals(differenceRecord))
                .expectComplete()
                .verify();
        //then
        verify(repository).findById(eq(testId));
        verify(repository, times(0)).save(any());
    }
}