package fr.cea.organicity.manager.exceptions.token;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class RoleComputationTokenException extends TokenException {
	private static final long serialVersionUID = -8900330262374191459L;
	
	public RoleComputationTokenException(String sub) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, sub);
	}
}