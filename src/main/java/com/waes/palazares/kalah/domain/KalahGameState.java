package com.waes.palazares.kalah.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * Kalah game state representation
 */
@AllArgsConstructor
@Getter
public class KalahGameState {
    private String id;

    private String url;

    private Map<Integer, Integer> status;
}
