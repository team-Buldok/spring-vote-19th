package buldog.vote.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // cors를 적용할 spring서버의 url 패턴.
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH",
                        "DELETE", "OPTIONS") // cors를 허용할 method + DELETE 추가
                .allowedHeaders("Content-Type", "Authorization","accessToken","refreshToken")
                .exposedHeaders("Authorization","Set-Cookie","Refreshtoken")
                .allowCredentials(true);

    }
}
