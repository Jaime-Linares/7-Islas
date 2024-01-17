package org.juego.siete.islas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Getter
public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -3906338266891937036L;

	public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
		super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
	}

	public ResourceNotFoundException(final String message) {
		super(message);
	}

}
