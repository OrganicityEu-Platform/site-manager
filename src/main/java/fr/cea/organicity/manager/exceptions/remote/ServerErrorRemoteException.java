package fr.cea.organicity.manager.exceptions.remote;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) 
public class ServerErrorRemoteException extends RemoteException {
	private static final long serialVersionUID = 322401364444680606L;
	
	public ServerErrorRemoteException(String url) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, url);
	}
}
