package com.waes.palazares.kalah.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

/**
 * Kalah game state representation
 */
@AllArgsConstructor
@Getter
public class KalahGameState {
    @NonNull
    private String id;

    @NonNull
    private String url;

    @NonNull
    private Map<Integer, Integer> status;
}
