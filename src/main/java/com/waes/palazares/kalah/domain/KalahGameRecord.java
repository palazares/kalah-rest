package com.waes.palazares.kalah.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * Entity object used to store game details
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "kalahgame")
public class KalahGameRecord {
    private UUID id = UUID.randomUUID();

    private int[] status = {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};

    private GameState state = GameState.SOUTH_TURN;
}
