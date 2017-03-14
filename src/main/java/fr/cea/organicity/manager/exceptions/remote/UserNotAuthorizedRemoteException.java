package fr.cea.organicity.manager.exceptions.remote;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@ResponseStatus(HttpStatus.UNAUTHORIZED) 
public class UserNotAuthorizedRemoteException extends RemoteException {
	
	private static final long serialVersionUID = 8310878610613102141L;

	public UserNotAuthorizedRemoteException(String url) {
		super(HttpStatus.UNAUTHORIZED, url);
	}
}