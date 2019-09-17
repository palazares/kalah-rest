package com.waes.palazares.kalah.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * Entity object used to store game details
 */
@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@Document(collection = "kalahgame")
public class KalahGameRecord {
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Builder.Default
    private int[] status = {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};

    @Builder.Default
    private GameState state = GameState.SOUTH_TURN;
}
