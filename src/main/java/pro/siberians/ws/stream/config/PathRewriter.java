package pro.siberians.ws.stream.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PathRewriter {

	@Bean
	public WebMvcConfigurer forwardToIndex() {
		return new WebMvcConfigurer() {
			@Override
			public void addViewControllers(ViewControllerRegistry registry) {
				registry.addViewController("/contestant").setViewName("forward:/contestant/index.html");
				registry.addViewController("/watcher").setViewName("forward:/watcher/index.html");
				registry.addViewController("/admin").setViewName("forward:/admin/index.html");
			}
		};
	}

}
