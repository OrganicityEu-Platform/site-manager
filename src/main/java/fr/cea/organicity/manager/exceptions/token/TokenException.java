package fr.cea.organicity.manager.exceptions.token;

import org.springframework.http.HttpStatus;

import fr.cea.organicity.manager.exceptions.AppException;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public abstract class TokenException extends AppException {
	private static final long serialVersionUID = 3628247738796656593L;
	
	protected final String sub;
	
	public TokenException(HttpStatus status, String sub) {
		super(status);
		this.sub = sub;
	}
}
