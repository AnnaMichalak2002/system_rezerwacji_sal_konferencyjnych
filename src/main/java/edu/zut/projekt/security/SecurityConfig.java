package edu.zut.projekt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;
    @Value("${app.test.user.email}")
    private String testUserEmail;

    @Value("${app.test.user.password}")
    private String testUserPassword;

    public SecurityConfig(RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                          RestAccessDeniedHandler restAccessDeniedHandler) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST, "/rooms").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/rooms/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/rooms/*/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/reservations").hasRole("ADMIN")

                        .requestMatchers("/rooms/**").authenticated()
                        .requestMatchers("/users/**").authenticated()
                        .requestMatchers("/reservations/**").authenticated()

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername("jan.kowalski@yourfurniture.pl")
                .password(passwordEncoder.encode("User123!"))
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin@yourfurniture.pl")
                .password(passwordEncoder.encode("Admin123!"))
                .roles("ADMIN")
                .build();

        UserDetails user2 = User.withUsername(testUserEmail)
                .password(passwordEncoder.encode(testUserPassword))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user, admin, user2);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}