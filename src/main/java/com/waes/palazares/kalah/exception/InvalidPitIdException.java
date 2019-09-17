package com.waes.palazares.kalah.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when provided pitId is outside of [1-6,8-13] range
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid pitId. Should be in [1-6,8-13] range")
public class InvalidPitIdException extends Exception {
}
