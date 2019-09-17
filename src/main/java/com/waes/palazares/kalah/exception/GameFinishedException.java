package com.waes.palazares.kalah.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when provided game id belongs to already finished game
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "The game has finished")
public class GameFinishedException extends Exception {
}
