package com.waes.palazares.kalah.repository;

import com.waes.palazares.kalah.domain.KalahGameRecord;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * Repository used to store games states
 */
public interface KalahGameRepository extends ReactiveCrudRepository<KalahGameRecord, String> {
}
