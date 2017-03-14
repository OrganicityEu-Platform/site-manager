package fr.cea.organicity.manager.exceptions.local;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundLocalException extends LocalException {

	private static final long serialVersionUID = -8830805307627537449L;

	private final Class<?> type;
		
	public NotFoundLocalException(String urn, Class<?> type) {
		super(HttpStatus.NOT_FOUND, urn);
		this.type = type;
	}
	
	@Override
	public String getMessage() {
		return "Entity " + getUrn() + " of type " + type + " not found in repository";
	}
}
