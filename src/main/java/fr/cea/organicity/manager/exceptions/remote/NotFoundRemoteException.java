package fr.cea.organicity.manager.exceptions.remote;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundRemoteException extends RemoteException {

	private static final long serialVersionUID = 3196325324154111296L;
	
	private final String urn;
	private final Class<?> type;
		
	public NotFoundRemoteException(String url, String urn, Class<?> type) {
		super(HttpStatus.NOT_FOUND, url);
		this.urn = urn;
		this.type = type;
	}
	
	@Override
	public String getMessage() {
		return "Entity " + urn + " of type " + type + " not found";
	}
}
