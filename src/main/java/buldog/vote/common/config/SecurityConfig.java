package buldog.vote.common.config;

import buldog.vote.common.handler.JwtAccessDeniedHandler;
import buldog.vote.common.handler.JwtAuthenticationEntryPointHandler;
import buldog.vote.common.jwt.JwtFilter;
import buldog.vote.common.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationEntryPointHandler authenticationEntryPointHandler;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling((exception) -> exception.authenticationEntryPoint(authenticationEntryPointHandler))
                .exceptionHandling((exception) -> exception.accessDeniedHandler(accessDeniedHandler))

                .authorizeHttpRequests((requests) ->
                        requests
                                .requestMatchers("/swagger", "/swagger-ui.html", "/swagger-ui/**", "/api-docs", "/api-docs/**", "/v3/api-docs/**")
                                .permitAll()// swagger 경로 접근 허용
                                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/reissue", "/api/v1/join", "/api/v1/users/leaders/front", "/api/v1/users/leaders/back", "/api/v1/teams")
                                //  requestMatchers(HttpMethod.GET,"/post/*").permitAll()
                                //   .requestMatchers("/test/login").hasAuthority(Authority.NORMAL.toString())
                                // .requestMatchers("/api/v1/profiles").hasAuthority(Authority.NORMAL.toString())
                                // .requestMatchers("/post/*").hasAuthority("GENERAL")
                                .permitAll()
                                .anyRequest().authenticated())

                .addFilterBefore(new JwtFilter(tokenProvider, redisTemplate),
                        UsernamePasswordAuthenticationFilter.class);


        return http.build();

    }
}

