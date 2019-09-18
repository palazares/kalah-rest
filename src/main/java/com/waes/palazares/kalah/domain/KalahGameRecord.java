package com.waes.palazares.kalah.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
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
    @Id
    @NonNull
    private UUID id = UUID.randomUUID();

    @NonNull
    private int[] status = {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};

    @NonNull
    private GameState state = GameState.SOUTH_TURN;
}
