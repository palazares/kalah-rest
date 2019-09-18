package com.waes.palazares.kalah;

import com.waes.palazares.kalah.domain.GameState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Base64;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KalahWebApplicationTests {
    @LocalServerPort
    private int localPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void shouldReturnRecordWhenPutLeft() {
        //given
        String testId = "testId";
        String testContent = "testContent";
        String url = "http://localhost:" + localPort + "/v1/diff/" + testId + "/left";
        HttpEntity<String> request = new HttpEntity<>(Base64.getEncoder().encodeToString(testContent.getBytes()));
        //when
        ResponseEntity<DifferenceRecord> putResponse = testRestTemplate.exchange(url, HttpMethod.PUT, request, DifferenceRecord.class);
        //then
        assertNotNull(putResponse);
        assertEquals(HttpStatus.OK, putResponse.getStatusCode());
        assertNotNull(putResponse.getBody());
        assertEquals(testId, putResponse.getBody().getId());
        assertArrayEquals(testContent.getBytes(), putResponse.getBody().getLeft());
    }

    @Test
    public void shouldReturnRecordWhenPutRight() {
        //given
        String testId = "testId";
        String testContent = "testContent";
        String url = "http://localhost:" + localPort + "/v1/diff/" + testId + "/right";
        HttpEntity<String> request = new HttpEntity<>(Base64.getEncoder().encodeToString(testContent.getBytes()));
        //when
        ResponseEntity<DifferenceRecord> putResponse = testRestTemplate.exchange(url, HttpMethod.PUT, request, DifferenceRecord.class);
        //then
        assertNotNull(putResponse);
        assertEquals(HttpStatus.OK, putResponse.getStatusCode());
        assertNotNull(putResponse.getBody());
        assertEquals(testId, putResponse.getBody().getId());
        assertArrayEquals(testContent.getBytes(), putResponse.getBody().getRight());
    }

    @Test
    public void shouldReturnDifferenceWhenPutRightAndLeft() {
        //given
        String testId = "testId";
        String testContent = "testContent";
        String rightUrl = "http://localhost:" + localPort + "/v1/diff/" + testId + "/right";
        String leftUrl = "http://localhost:" + localPort + "/v1/diff/" + testId + "/left";
        String diffUrl = "http://localhost:" + localPort + "/v1/diff/" + testId;
        HttpEntity<String> request = new HttpEntity<>(Base64.getEncoder().encodeToString(testContent.getBytes()));
        //when
        testRestTemplate.exchange(rightUrl, HttpMethod.PUT, request, DifferenceRecord.class);
        testRestTemplate.exchange(leftUrl, HttpMethod.PUT, request, DifferenceRecord.class);
        ResponseEntity<DifferenceResult> differenceResponse = testRestTemplate.getForEntity(diffUrl, DifferenceResult.class);
        //then
        assertNotNull(differenceResponse);
        assertEquals(HttpStatus.OK, differenceResponse.getStatusCode());
        assertNotNull(differenceResponse.getBody());
        assertEquals(GameState.EQUALS, differenceResponse.getBody().getType());
        assertNotNull(differenceResponse.getBody().getMessage());
    }

    @Test
    public void shouldReturnEqualDifferenceWhenMultiplePutRightAndLeft() {
        //given
        String testId = "testId";
        String testContent = "testContent";
        String rightUrl = "http://localhost:" + localPort + "/v1/diff/" + testId + "/right";
        String leftUrl = "http://localhost:" + localPort + "/v1/diff/" + testId + "/left";
        String diffUrl = "http://localhost:" + localPort + "/v1/diff/" + testId;
        HttpEntity<String> request = new HttpEntity<>(Base64.getEncoder().encodeToString(testContent.getBytes()));
        //when
        testRestTemplate.exchange(rightUrl, HttpMethod.PUT, request, DifferenceRecord.class);
        testRestTemplate.exchange(leftUrl, HttpMethod.PUT, request, DifferenceRecord.class);
        testRestTemplate.exchange(rightUrl, HttpMethod.PUT, request, DifferenceRecord.class);
        testRestTemplate.exchange(leftUrl, HttpMethod.PUT, request, DifferenceRecord.class);
        ResponseEntity<DifferenceResult> differenceResponse = testRestTemplate.getForEntity(diffUrl, DifferenceResult.class);
        //then
        assertNotNull(differenceResponse);
        assertEquals(HttpStatus.OK, differenceResponse.getStatusCode());
        assertNotNull(differenceResponse.getBody());
        assertEquals(GameState.EQUALS, differenceResponse.getBody().getType());
        assertNotNull(differenceResponse.getBody().getMessage());
    }

    @Test
    public void shouldReturnSizeDifferenceWhenMultiplePutRightAndLeft() {
        //given
        String testId = "testId";
        String leftContent = "leftContent";
        String rightContent = "rightContent";
        String rightUrl = "http://localhost:" + localPort + "/v1/diff/" + testId + "/right";
        String leftUrl = "http://localhost:" + localPort + "/v1/diff/" + testId + "/left";
        String diffUrl = "http://localhost:" + localPort + "/v1/diff/" + testId;
        HttpEntity<String> leftRequest = new HttpEntity<>(Base64.getEncoder().encodeToString(leftContent.getBytes()));
        HttpEntity<String> rightRequest = new HttpEntity<>(Base64.getEncoder().encodeToString(rightContent.getBytes()));
        //when
        testRestTemplate.exchange(rightUrl, HttpMethod.PUT, leftRequest, DifferenceRecord.class);
        testRestTemplate.exchange(leftUrl, HttpMethod.PUT, leftRequest, DifferenceRecord.class);
        testRestTemplate.exchange(rightUrl, HttpMethod.PUT, rightRequest, DifferenceRecord.class);
        testRestTemplate.exchange(leftUrl, HttpMethod.PUT, leftRequest, DifferenceRecord.class);
        ResponseEntity<DifferenceResult> differenceResponse = testRestTemplate.getForEntity(diffUrl, DifferenceResult.class);
        //then
        assertNotNull(differenceResponse);
        assertEquals(HttpStatus.OK, differenceResponse.getStatusCode());
        assertNotNull(differenceResponse.getBody());
        assertEquals(GameState.DIFFERENT_SIZE, differenceResponse.getBody().getType());
        assertNotNull(differenceResponse.getBody().getMessage());
    }

    @Test
    public void shouldReturnContentDifferenceWhenMultiplePutRightAndLeft() {
        //given
        String testId = "testId";
        String leftContent = "rightSAMEPARTright";
        String rightContent = "lleftSAMEPARTlleft";
        String rightUrl = "http://localhost:" + localPort + "/v1/diff/" + testId + "/right";
        String leftUrl = "http://localhost:" + localPort + "/v1/diff/" + testId + "/left";
        String diffUrl = "http://localhost:" + localPort + "/v1/diff/" + testId;
        HttpEntity<String> leftRequest = new HttpEntity<>(Base64.getEncoder().encodeToString(leftContent.getBytes()));
        HttpEntity<String> rightRequest = new HttpEntity<>(Base64.getEncoder().encodeToString(rightContent.getBytes()));
        //when
        testRestTemplate.exchange(rightUrl, HttpMethod.PUT, leftRequest, DifferenceRecord.class);
        testRestTemplate.exchange(leftUrl, HttpMethod.PUT, leftRequest, DifferenceRecord.class);
        testRestTemplate.exchange(rightUrl, HttpMethod.PUT, rightRequest, DifferenceRecord.class);
        testRestTemplate.exchange(leftUrl, HttpMethod.PUT, leftRequest, DifferenceRecord.class);
        ResponseEntity<DifferenceResult> differenceResponse = testRestTemplate.getForEntity(diffUrl, DifferenceResult.class);
        //then
        assertNotNull(differenceResponse);
        assertEquals(HttpStatus.OK, differenceResponse.getStatusCode());
        assertNotNull(differenceResponse.getBody());
        assertEquals(GameState.DIFFERENT_CONTENT, differenceResponse.getBody().getType());
        assertNotNull(differenceResponse.getBody().getMessage());
        assertTrue(differenceResponse.getBody().getMessage().contains("(0, 4)"));
        assertTrue(differenceResponse.getBody().getMessage().contains("(13, 4)"));
    }

    @Test
    public void shouldReturnBadRequestWhenEmptyLeftContent() {
        //given
        String testId = "testId";
        String url = "http://localhost:" + localPort + "/v1/diff/" + testId + "/left";
        HttpEntity<String> request = new HttpEntity<>("");
        //when
        ResponseEntity<DifferenceRecord> putResponse = testRestTemplate.exchange(url, HttpMethod.PUT, request, DifferenceRecord.class);
        //then
        assertNotNull(putResponse);
        assertEquals(HttpStatus.BAD_REQUEST, putResponse.getStatusCode());
        assertNotNull(putResponse.getBody());
        assertNull(putResponse.getBody().getId());
    }

    @Test
    public void shouldReturnBadRequestWhenEmptyRightContent() {
        //given
        String testId = "testId";
        String url = "http://localhost:" + localPort + "/v1/diff/" + testId + "/right";
        HttpEntity<String> request = new HttpEntity<>("");
        //when
        ResponseEntity<DifferenceRecord> putResponse = testRestTemplate.exchange(url, HttpMethod.PUT, request, DifferenceRecord.class);
        //then
        assertNotNull(putResponse);
        assertEquals(HttpStatus.BAD_REQUEST, putResponse.getStatusCode());
        assertNotNull(putResponse.getBody());
        assertNull(putResponse.getBody().getId());
    }

    @Test
    public void shouldReturnNotFoundWhenWrongId() {
        //given
        String testId = "testId";
        String leftContent = "leftContent";
        String rightContent = "rightContent";
        String rightUrl = "http://localhost:" + localPort + "/v1/diff/" + testId + "/right";
        String leftUrl = "http://localhost:" + localPort + "/v1/diff/" + testId + "/left";
        String diffUrl = "http://localhost:" + localPort + "/v1/diff/" + testId + "1";
        HttpEntity<String> leftRequest = new HttpEntity<>(Base64.getEncoder().encodeToString(leftContent.getBytes()));
        HttpEntity<String> rightRequest = new HttpEntity<>(Base64.getEncoder().encodeToString(rightContent.getBytes()));
        //when
        testRestTemplate.exchange(rightUrl, HttpMethod.PUT, rightRequest, DifferenceRecord.class);
        testRestTemplate.exchange(leftUrl, HttpMethod.PUT, leftRequest, DifferenceRecord.class);
        ResponseEntity<DifferenceResult> differenceResponse = testRestTemplate.getForEntity(diffUrl, DifferenceResult.class);
        //then
        assertNotNull(differenceResponse);
        assertEquals(HttpStatus.NOT_FOUND, differenceResponse.getStatusCode());
        assertNotNull(differenceResponse.getBody());
        assertNotNull(differenceResponse.getBody().getMessage());
    }

    @Test
    public void shouldReturnNotFoundWhenOnlyOnePartExists() {
        //given
        String testId = "testId";
        String leftContent = "leftContent";
        String leftUrl = "http://localhost:" + localPort + "/v1/diff/" + testId + "/left";
        String diffUrl = "http://localhost:" + localPort + "/v1/diff/" + testId;
        HttpEntity<String> leftRequest = new HttpEntity<>(Base64.getEncoder().encodeToString(leftContent.getBytes()));
        //when
        testRestTemplate.exchange(leftUrl, HttpMethod.PUT, leftRequest, DifferenceRecord.class);
        ResponseEntity<DifferenceResult> differenceResponse = testRestTemplate.getForEntity(diffUrl, DifferenceResult.class);
        //then
        assertNotNull(differenceResponse);
        assertEquals(HttpStatus.NOT_FOUND, differenceResponse.getStatusCode());
        assertNotNull(differenceResponse.getBody());
        assertNotNull(differenceResponse.getBody().getMessage());
    }
}
