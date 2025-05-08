package hackathon.kermmit360.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(c->c.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**","/", "/login**","/login/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login").permitAll()
                        .defaultSuccessUrl("/allRepo", true)
                )
        ;
        return http.build();
    }

//    @Bean
//    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository repo) {
//        return new InMemoryOAuth2AuthorizedClientService(repo);
//    }
}
