package com.waes.palazares.kalah.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when game record was not found
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Game record was not found")
public class InvalidRecordException extends Exception {
}
