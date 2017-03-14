package fr.cea.organicity.manager.exceptions.local;

import org.springframework.http.HttpStatus;

import fr.cea.organicity.manager.exceptions.AppException;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public abstract class LocalException extends AppException {

	private static final long serialVersionUID = 1422368026467811522L;
	
	private final String urn;
	
	public LocalException(HttpStatus status, String urn) {
		super(status);
		this.urn = urn;
	}
}
