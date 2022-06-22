package pro.siberians.ws.stream.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OpenViduSessionNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public OpenViduSessionNotFoundException(String message) {
		super(message);
	}

}
