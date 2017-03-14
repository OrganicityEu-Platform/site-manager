package fr.cea.organicity.manager.exceptions;

import java.io.IOException;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class AppException extends IOException {
	private static final long serialVersionUID = -4261915637862626137L;
	private final HttpStatus status;
}
