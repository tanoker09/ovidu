package pro.siberians.ws.stream.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NotAuthenticatedException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotAuthenticatedException(String message) {
		super(message);
	}

}
