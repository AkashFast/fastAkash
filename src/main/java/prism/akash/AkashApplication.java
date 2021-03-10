package prism.akash;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
public class AkashApplication  extends SpringBootServletInitializer {

	@Value("${akashConfig.oauth.cross.urls:anyValue}")
	private String crossAllowOriginUrl;

	/**
	 * 跨域请求访问初始化
	 *
	 * @return
	 */
	@ConditionalOnProperty(name = "akashConfig.oauth.cross.enable", havingValue = "true", matchIfMissing = false)
	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedMethod("*");
		urlBasedCorsConfigurationSource.registerCorsConfiguration(crossAllowOriginUrl, corsConfiguration);
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AkashApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(AkashApplication.class, args);
	}

}
