package com.waes.palazares.kalah.controller;

import com.waes.palazares.kalah.domain.GameState;
import com.waes.palazares.kalah.domain.KalahGameRecord;
import com.waes.palazares.kalah.service.KalahGameServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebFluxTest(controllers = KalahGameController.class)
public class KalahGameControllerTest {
    @Autowired
    private WebTestClient client;

    @MockBean
    private KalahGameServiceImpl service;

    @Test
    public void shouldCallServiceWhenPostGame() {
        //given
        var id = UUID.randomUUID();
        var status = new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
        var state = GameState.SOUTH_TURN;
        var game = new KalahGameRecord(id, status, state);
        //when
        when(service.create()).thenReturn(Mono.just(game));
        //then
        client
                .post()
                .uri("/games")
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("id").isEqualTo(id.toString())
                .jsonPath("url").isNotEmpty();
        verify(service, times(1)).create();
    }

    @Test
    public void shouldCallServiceWhenPutMove() {
        //given
        var id = UUID.randomUUID();
        var status = new int[]{6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
        var state = GameState.SOUTH_TURN;
        var game = new KalahGameRecord(id, status, state);
        //when
        when(service.move(any(), any())).thenReturn(Mono.just(game));
        //then
        client
                .put()
                .uri("/games/1/pits/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("id").isEqualTo(id.toString())
                .jsonPath("url").isNotEmpty()
                .jsonPath("status").isMap();
        verify(service, times(1)).move(eq("1"), eq("1"));
    }

    @Test
    public void shouldRedirectToSwaggerUI() {
        client.get().uri("/").exchange()
                .expectStatus().is3xxRedirection();
    }
}