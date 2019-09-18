package com.waes.palazares.kalah;

import com.waes.palazares.kalah.domain.KalahGameState;
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

import java.util.LinkedHashMap;
import java.util.UUID;

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
    public void shouldReturnGameWhenPostGame() {
        //given
        var url = "http://localhost:" + localPort + "/games";
        //when
        var postResponse = testRestTemplate.exchange(url, HttpMethod.POST, HttpEntity.EMPTY, KalahGameState.class);
        //then
        assertNotNull(postResponse);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        assertNotNull(postResponse.getBody());
        assertNotNull(postResponse.getBody().getId());
        assertEquals(url + "/" + postResponse.getBody().getId(), postResponse.getBody().getUrl());
    }

    @Test
    public void shouldReturnGameAfterTheMove() {
        //given
        var postUrl = "http://localhost:" + localPort + "/games";

        //when
        var postResponse = testRestTemplate.exchange(postUrl, HttpMethod.POST, HttpEntity.EMPTY, KalahGameState.class);
        var putUrl = postResponse.getBody().getUrl() + "/pits/1";
        var putResponse = testRestTemplate.exchange(putUrl, HttpMethod.PUT, HttpEntity.EMPTY, KalahGameState.class);

        //then
        assertNotNull(putResponse);
        assertEquals(HttpStatus.OK, putResponse.getStatusCode());
        assertNotNull(putResponse.getBody());
        assertEquals(postResponse.getBody().getId(), putResponse.getBody().getId());
        var resultMap = putResponse.getBody().getStatus();
        assertEquals(14, resultMap.size());
        var resultStatus = new int[14];
        resultMap.forEach((key, value) -> resultStatus[key - 1] = value);
        var expected = new int[]{0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0};
        assertArrayEquals(expected, resultStatus);
    }

    @Test
    public void shouldReturnBadRequestWhenEmptyGameId() {
        //given
        var putUrl = "http://localhost:" + localPort + "/games/ /pits/0";

        //when
        var putResponse = testRestTemplate.exchange(putUrl, HttpMethod.PUT, HttpEntity.EMPTY, LinkedHashMap.class);
        //then
        assertNotNull(putResponse);
        assertEquals(HttpStatus.BAD_REQUEST, putResponse.getStatusCode());
        assertNotNull(putResponse.getBody());
        assertEquals("Invalid entity Id", putResponse.getBody().get("message"));
    }

    @Test
    public void shouldReturnBadRequestWhenWrongGameId() {
        //given
        var putUrl = "http://localhost:" + localPort + "/games/123/pits/0";

        //when
        var putResponse = testRestTemplate.exchange(putUrl, HttpMethod.PUT, HttpEntity.EMPTY, LinkedHashMap.class);
        //then
        assertNotNull(putResponse);
        assertEquals(HttpStatus.BAD_REQUEST, putResponse.getStatusCode());
        assertNotNull(putResponse.getBody());
        assertEquals("Invalid entity Id", putResponse.getBody().get("message"));
    }

    @Test
    public void shouldReturnBadRequestWhenEmptyPitId() {
        //given
        var putUrl = "http://localhost:" + localPort + "/games/1/pits/ ";

        //when
        var putResponse = testRestTemplate.exchange(putUrl, HttpMethod.PUT, HttpEntity.EMPTY, LinkedHashMap.class);
        //then
        assertNotNull(putResponse);
        assertEquals(HttpStatus.BAD_REQUEST, putResponse.getStatusCode());
        assertNotNull(putResponse.getBody());
        assertEquals("Invalid entity Id", putResponse.getBody().get("message"));
    }

    @Test
    public void shouldReturnBadRequestWhenNotNumericPitId() {
        //given
        var putUrl = "http://localhost:" + localPort + "/games/" + UUID.randomUUID().toString() + "/pits/abc";

        //when
        var putResponse = testRestTemplate.exchange(putUrl, HttpMethod.PUT, HttpEntity.EMPTY, LinkedHashMap.class);
        //then
        assertNotNull(putResponse);
        assertEquals(HttpStatus.BAD_REQUEST, putResponse.getStatusCode());
        assertNotNull(putResponse.getBody());
        assertEquals("Invalid pitId. Should be in [1-6,8-13] range", putResponse.getBody().get("message"));
    }

    @Test
    public void shouldReturnBadRequestWhenOutOfRangePitId() {
        //given
        var putUrl = "http://localhost:" + localPort + "/games/" + UUID.randomUUID().toString() + "/pits/15";

        //when
        var putResponse = testRestTemplate.exchange(putUrl, HttpMethod.PUT, HttpEntity.EMPTY, LinkedHashMap.class);
        //then
        assertNotNull(putResponse);
        assertEquals(HttpStatus.BAD_REQUEST, putResponse.getStatusCode());
        assertNotNull(putResponse.getBody());
        assertEquals("Invalid pitId. Should be in [1-6,8-13] range", putResponse.getBody().get("message"));
    }

    @Test
    public void shouldReturnBadRequestWhenNotAllowedPitId() {
        //given
        var putUrl = "http://localhost:" + localPort + "/games/" + UUID.randomUUID().toString() + "/pits/7";

        //when
        var putResponse = testRestTemplate.exchange(putUrl, HttpMethod.PUT, HttpEntity.EMPTY, LinkedHashMap.class);
        //then
        assertNotNull(putResponse);
        assertEquals(HttpStatus.BAD_REQUEST, putResponse.getStatusCode());
        assertNotNull(putResponse.getBody());
        assertEquals("Invalid pitId. Should be in [1-6,8-13] range", putResponse.getBody().get("message"));
    }

    @Test
    public void shouldReturnNotFoundWhenNonExistingGameId() {
        //given
        var id = UUID.randomUUID();
        var putUrl = "http://localhost:" + localPort + "/games/" + id + "/pits/4";

        //when
        var putResponse = testRestTemplate.exchange(putUrl, HttpMethod.PUT, HttpEntity.EMPTY, LinkedHashMap.class);
        //then
        assertNotNull(putResponse);
        assertEquals(HttpStatus.NOT_FOUND, putResponse.getStatusCode());
        assertNotNull(putResponse.getBody());
        assertEquals("Game record was not found", putResponse.getBody().get("message"));
    }

    @Test
    public void shouldReturnConflictWhenWrongMove() {
        //given
        var postUrl = "http://localhost:" + localPort + "/games";

        //when
        var postResponse = testRestTemplate.exchange(postUrl, HttpMethod.POST, HttpEntity.EMPTY, KalahGameState.class);
        var putUrl = postResponse.getBody().getUrl() + "/pits/9";
        var putResponse = testRestTemplate.exchange(putUrl, HttpMethod.PUT, HttpEntity.EMPTY, LinkedHashMap.class);
        //then
        assertNotNull(putResponse);
        assertEquals(HttpStatus.CONFLICT, putResponse.getStatusCode());
        assertNotNull(putResponse.getBody());
        assertEquals("PitId conflicts current game state", putResponse.getBody().get("message"));
    }
}
