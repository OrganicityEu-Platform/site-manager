package fr.cea.organicity.manager.exceptions.remote;

import org.springframework.http.HttpStatus;

import fr.cea.organicity.manager.exceptions.AppException;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public abstract class RemoteException extends AppException {
	
	private static final long serialVersionUID = 8903671000074515122L;
	
	private final String url;
	
	public RemoteException(HttpStatus status, String url) {
		super(status);
		this.url = url;
	}
}
