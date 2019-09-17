package com.waes.palazares.kalah.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when provided pitId conflicting current game state.
 * Possible reasons are:
 * Move from the other player is expected
 * Pit is empty
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "PitId conflicts current game state")
public class InvalidMoveException extends Exception {
}
