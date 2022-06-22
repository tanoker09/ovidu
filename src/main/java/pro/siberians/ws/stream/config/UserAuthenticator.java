package pro.siberians.ws.stream.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.beans.factory.annotation.Autowired;

import pro.siberians.ws.stream.authentication.AuthenticationChecker;
import pro.siberians.ws.stream.authorization.AuthorizationChecker;
import pro.siberians.ws.stream.model.UserModel;

class AuthenticationInterceptor extends HandlerInterceptorAdapter {

	private AuthenticationChecker authenticationChecker;
	private AuthorizationChecker authorizationChecker;
	private String allowedUserRole;

	AuthenticationInterceptor(AuthenticationChecker authenticationChecker) {
		this.authenticationChecker = authenticationChecker;
	}

	AuthenticationInterceptor(
		AuthenticationChecker authenticationChecker,
		AuthorizationChecker authorizationChecker,
		String allowedUserRole
	) {
		this.authenticationChecker = authenticationChecker;
		this.authorizationChecker = authorizationChecker;
		this.allowedUserRole = allowedUserRole;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (request.getMethod().equals("OPTIONS")) return true; // skip CORS requests
		UserModel user = authenticationChecker.authenticateUser(request);
		if (authorizationChecker != null && allowedUserRole != null)
			authorizationChecker.userMustHaveRoleAtLeast(user, allowedUserRole);
		request.setAttribute("authenticatedUser", user);
		return true;
	}

}

@Configuration
public class UserAuthenticator {

	@Autowired private AuthenticationChecker authenticationChecker;
	@Autowired private AuthorizationChecker authorizationChecker;

	@Bean
	public WebMvcConfigurer authRequests() {
		return new WebMvcConfigurer() {
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				AuthenticationInterceptor authInterceptor = new AuthenticationInterceptor(authenticationChecker);

				AuthenticationInterceptor contestantInterceptor = new AuthenticationInterceptor(
					authenticationChecker, authorizationChecker, "contestant");

				AuthenticationInterceptor watcherInterceptor = new AuthenticationInterceptor(
					authenticationChecker, authorizationChecker, "watcher");

				AuthenticationInterceptor adminInterceptor = new AuthenticationInterceptor(
					authenticationChecker, authorizationChecker, "admin");

				registry
					.addInterceptor(authInterceptor)
					.addPathPatterns("/api/auth/**");

				registry
					.addInterceptor(contestantInterceptor)
					.addPathPatterns("/api/contestant/**");

				registry
					.addInterceptor(watcherInterceptor)
					.addPathPatterns("/api/watcher/**");

				registry
					.addInterceptor(adminInterceptor)
					.addPathPatterns("/api/admin/**");
			}
		};
	}

	public UserModel getUserFromRequest(HttpServletRequest request) {
		return (UserModel) request.getAttribute("authenticatedUser");
	}

	public void deleteAuthenticationInfo(HttpServletResponse response) {
		authenticationChecker.unauthenticateUser(response);
	}

}
