package pro.siberians.ws.stream.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pro.siberians.ws.stream.model.UserModel;
import pro.siberians.ws.stream.exception.*;

public interface AuthenticationChecker {
	public UserModel authenticateUser(HttpServletRequest request) throws NotAuthenticatedException;
	public void unauthenticateUser(HttpServletResponse response);
}
