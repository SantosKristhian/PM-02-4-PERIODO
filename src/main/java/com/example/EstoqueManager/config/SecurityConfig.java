package com.example.EstoqueManager.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthFilter;

	@Autowired
	private AuthenticationProvider authenticationProvider;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				// REMOVA TODA A PARTE DE .cors()
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/login",
								"/api/emanager/login",
								"/health"  // adicione health check
						).permitAll()
						.anyRequest().authenticated()
				)
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				.sessionManagement(session ->
						session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				);

		return http.build();
	}

	// REMOVA os métodos corsConfigurationSource() e corsFilter()
}


