package com.waes.palazares.kalah.controller;

import com.waes.palazares.kalah.domain.DifferenceRecord;
import com.waes.palazares.kalah.domain.DifferenceResult;
import com.waes.palazares.kalah.domain.GameState;
import com.waes.palazares.kalah.service.DifferenceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Base64;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WithMockUser(roles = "ADMIN")
@RunWith(SpringRunner.class)
@WebFluxTest(controllers = KalahGameController.class)
public class KalahGameControllerTest {
    @Autowired
    WebTestClient client;

    @MockBean
    private DifferenceService differenceService;

    @Test
    public void shouldCallServiceWhenPutLeft() throws Exception {
        //given
        String testId = "testId";
        String testContent = "testContent";
        var testRecord = Mono.just(DifferenceRecord.builder().id(testId).left(testContent.getBytes()).build());
        //when
        when(differenceService.putLeft(testId, testContent)).thenReturn(testRecord);
        //then
        client.mutateWith(csrf())
                .put()
                .uri("/v1/diff/" + testId + "/left")
                .syncBody(testContent)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("id").isEqualTo(testId)
                .jsonPath("left").isEqualTo(Base64.getEncoder().encodeToString(testContent.getBytes()));
        verify(differenceService, times(1)).putLeft(testId, testContent);
    }

    @Test
    public void shouldCallServiceWhenPutRight() throws Exception {
        //given
        String testId = "testId";
        String testContent = "testContent";
        var testRecord = Mono.just(DifferenceRecord.builder().id(testId).right(testContent.getBytes()).build());
        //when
        when(differenceService.putRight(testId, testContent)).thenReturn(testRecord);
        //then
        client.mutateWith(csrf())
                .put()
                .uri("/v1/diff/" + testId + "/right")
                .syncBody(testContent)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("id").isEqualTo(testId)
                .jsonPath("right").isEqualTo(Base64.getEncoder().encodeToString(testContent.getBytes()));
        verify(differenceService, times(1)).putRight(testId, testContent);
    }

    @Test
    public void shouldCallServiceWhenGetDifference() throws Exception {
        //given
        String testId = "testId";
        String testMessage = "testEquals";
        var testRecord = Mono.just(DifferenceRecord.builder()
                .id(testId)
                .result(DifferenceResult.builder().type(GameState.EQUALS).message(testMessage).build())
                .build());
        //when
        when(differenceService.getDifference(testId)).thenReturn(testRecord);
        //then
        client.mutateWith(csrf())
                .get()
                .uri("/v1/diff/" + testId)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("type").isEqualTo(GameState.EQUALS.toString())
                .jsonPath("message").isEqualTo(testMessage);

        verify(differenceService, times(1)).getDifference(testId);
    }

    @Test
    public void shouldRedirectToSwaggerUI() {
        client.mutateWith(csrf()).get().uri("/").exchange()
                .expectStatus().is3xxRedirection();
    }
}