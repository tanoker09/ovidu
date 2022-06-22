package pro.siberians.ws.stream.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SystemLogicException extends Exception {

    private static final long serialVersionUID = 1L;

    public SystemLogicException(String message) {
        super(message);
    }

}
