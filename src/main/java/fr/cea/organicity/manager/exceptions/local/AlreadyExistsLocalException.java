package fr.cea.organicity.manager.exceptions.local;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class AlreadyExistsLocalException extends LocalException {
	private static final long serialVersionUID = 7893793218363655763L;
	
	public AlreadyExistsLocalException(String urn) {
		super(HttpStatus.PRECONDITION_FAILED, urn);
	}
}
