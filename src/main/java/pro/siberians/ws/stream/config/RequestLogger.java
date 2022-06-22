package pro.siberians.ws.stream.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


class LoggerInterceptor extends HandlerInterceptorAdapter {
	private static Logger log = LoggerFactory.getLogger(LoggerInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		request.setAttribute("startTime", System.currentTimeMillis());
		log.info("Request [{}] {} from {}", request.getMethod(), request.getRequestURI(), getRemoteAddr(request));
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, ex);
		long startTime = (Long) request.getAttribute("startTime");
		long endTime = System.currentTimeMillis();
		if (ex != null) log.error("Request raised exception", ex);
		log.info("Request completed in {} ms with status {}", endTime - startTime, response.getStatus());
	}

	private String getRemoteAddr(HttpServletRequest request) {
		String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
		if (ipFromHeader != null && ipFromHeader.length() > 0) {
			log.debug("X-FORWARDED-FOR: {}", ipFromHeader);
			return ipFromHeader;
		} else {
			return request.getRemoteAddr();
		}
	}
}

@Configuration
public class RequestLogger {

	@Bean
	public WebMvcConfigurer logRequests() {
		return new WebMvcConfigurer() {
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				registry.addInterceptor(new LoggerInterceptor());
			}
		};
	}

}
