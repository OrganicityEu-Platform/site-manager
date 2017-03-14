package fr.cea.organicity.manager.exceptions.remote;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED) 
public class MethodNotAllowedRemoteException extends RemoteException {
	private static final long serialVersionUID = -6609550573329319844L;
	
	public MethodNotAllowedRemoteException(String url) {
		super(HttpStatus.METHOD_NOT_ALLOWED, url);
	}
}
