package fr.cea.organicity.manager.exceptions.token;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotAuthorizedTokenException extends TokenException {
	private static final long serialVersionUID = -7811933740584334266L;
	
	private final String message;
	
	public UserNotAuthorizedTokenException(String sub, String message) {
		super(HttpStatus.UNAUTHORIZED, sub);
		this.message = message;
	}
	
	public UserNotAuthorizedTokenException(String sub) {
		this(sub, null);
	}
}