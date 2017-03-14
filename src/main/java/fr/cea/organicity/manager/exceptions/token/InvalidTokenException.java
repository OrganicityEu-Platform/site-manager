package fr.cea.organicity.manager.exceptions.token;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends TokenException {
	private static final long serialVersionUID = -4186247192048440673L;
	private final String message;
	
	public InvalidTokenException(String sub, String message) {
		super(HttpStatus.UNAUTHORIZED, sub);
		this.message = message;
	}
}