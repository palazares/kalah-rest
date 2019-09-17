package com.waes.palazares.kalah.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when provided id has wrong format (null or empty)
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid entity Id")
public class InavlidIdException extends Exception {
}
