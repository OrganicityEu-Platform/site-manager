package fr.cea.organicity.manager.exceptions.local;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;



@Data
@EqualsAndHashCode(callSuper=true)
@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class BadRequestLocalException extends LocalException {
	private static final long serialVersionUID = 2205246298990527796L;

	public BadRequestLocalException(String urn) {
		super(HttpStatus.PRECONDITION_FAILED, urn);
	}
}