package hackathon.kermmit360.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer configure(){
        return web -> web.ignoring()
                .requestMatchers(PathRequest
                        .toStaticResources()
                        .atCommonLocations()
                );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
<<<<<<< HEAD
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
=======
                .authorizeHttpRequests((auth)-> auth
                                .requestMatchers("/","/auth/signin","/auth/signup").permitAll()
                                .requestMatchers("/api/**").authenticated()
                                .anyRequest().authenticated()
                        //.requestMatchers("/admin").hasRole("ADMIN")
                        //.requestMatchers("/").hasAnyRole("ADMIN","USER")

                );

        http
                .formLogin((auth)->auth.loginPage("/auth/signin")
                        .loginProcessingUrl("/auth/signin")
                        .defaultSuccessUrl("/home",true)
                        .permitAll()
                );

        http
                .logout((auth) -> auth
                        .logoutUrl("/auth/signout")
                        .logoutSuccessUrl("/auth/signin")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        http
                .csrf((auth)->auth.disable());


        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
>>>>>>> main-v2
