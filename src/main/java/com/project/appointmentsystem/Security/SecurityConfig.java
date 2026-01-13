package com.project.appointmentsystem.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }



        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


            http
                    .csrf(csrf -> csrf
                            .ignoringRequestMatchers("/h2-console/**")
                            .disable()
                    )
                    .headers(headers -> headers
                            .frameOptions(frame -> frame.sameOrigin())
                    )
                    .sessionManagement(s ->
                            s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .authorizeHttpRequests(auth -> auth

                            // ================= AUTH =================
                            .requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers("/h2-console/**").permitAll()

                            //WebSocket
                            .requestMatchers("/ws/**").permitAll()
                            .requestMatchers("/index.html").permitAll()
                            .requestMatchers("/").permitAll()

                            // ================= STAFF =================
                            .requestMatchers(HttpMethod.POST,   "/api/service/create").hasRole("STAFF")
                            .requestMatchers(HttpMethod.PUT,    "/api/service/**").hasRole("STAFF")
                            .requestMatchers(HttpMethod.DELETE, "/api/service/**").hasRole("STAFF")
                            .requestMatchers(HttpMethod.GET,    "/api/service/my").hasRole("STAFF")
                            .requestMatchers(HttpMethod.POST,   "/api/schedules/create").hasRole("STAFF")
                            .requestMatchers(HttpMethod.GET,    "/api/schedules/my").hasRole("STAFF")
                            .requestMatchers(HttpMethod.PUT,    "/api/schedules/**").hasRole("STAFF")
                            .requestMatchers(HttpMethod.DELETE, "/api/schedules/**").hasRole("STAFF")
                            .requestMatchers(HttpMethod.GET, "/api/appointments/my").hasRole("STAFF")

                            // ================= CUSTOMER =================
                            .requestMatchers(HttpMethod.POST, "/api/appointments/book").hasRole("CUSTOMER")
                            .requestMatchers(HttpMethod.PUT,  "/api/appointments/{id}").hasRole("CUSTOMER")
                            .requestMatchers(HttpMethod.PUT,  "/api/appointments/cancel/**")
                            .hasAnyRole("CUSTOMER","ADMIN")
                            .requestMatchers(HttpMethod.GET,  "/api/appointments/customer/my")
                            .hasRole("CUSTOMER")

                            // ================= ADMIN =================
                            .requestMatchers("/api/appointments/admin/**").hasRole("ADMIN")

                            // ================= PUBLIC SERVICES =================
                            .requestMatchers(HttpMethod.GET, "/api/service/**")
                            .hasAnyRole("CUSTOMER","ADMIN")

                            // ================= DEFAULT =================
                            .anyRequest().authenticated()
                    )
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }



