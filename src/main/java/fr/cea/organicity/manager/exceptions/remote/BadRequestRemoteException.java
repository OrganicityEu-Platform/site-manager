package fr.cea.organicity.manager.exceptions.remote;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestRemoteException extends RemoteException {
	private static final long serialVersionUID = -835645135963020602L;
	
	public BadRequestRemoteException(String url) {
		super(HttpStatus.BAD_REQUEST, url);
	}
}
