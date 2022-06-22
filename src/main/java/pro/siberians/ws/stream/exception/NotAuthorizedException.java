package pro.siberians.ws.stream.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotAuthorizedException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotAuthorizedException(String message) {
		super(message);
	}

}
