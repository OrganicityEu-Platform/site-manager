package fr.cea.organicity.manager.exceptions.local;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class ServerErrorLocalException extends LocalException {
	private static final long serialVersionUID = 5828306114603773723L;
	
	private final Exception exception;
	
	public ServerErrorLocalException(String urn, Exception exception) {
		super(HttpStatus.PRECONDITION_FAILED, urn);
		this.exception = exception;
	}
}