package security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	@Autowired
	private RepositoryUserDetailsService userDetailService;

	@Autowired
	private security.jwt.JwtRequestFilter jwtRequestFilter;
	
	@Autowired
	private security.jwt.UnauthorizedHandlerJwt unauthorizedHandlerJwt;
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.setAllowedOrigins(Arrays.asList("*"));
	    configuration.setAllowedMethods(Arrays.asList("*"));
	    configuration.setAllowedHeaders(Arrays.asList("*"));
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}
    
    @Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());

		http.securityMatcher("/api/v1/**")
				.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

		http.authorizeHttpRequests(authorize -> authorize
			    // --- PUBLIC REST ENDPOINTS ---
			    .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
			    .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()
			    .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").permitAll()

			    .requestMatchers(HttpMethod.GET, "/api/v1/artists/**").permitAll()
			    .requestMatchers(HttpMethod.GET, "/api/v1/artworks/**").permitAll()
			    .requestMatchers(HttpMethod.GET, "/api/v1/museums/**").permitAll()
			    
			    .requestMatchers(HttpMethod.POST, "/api/v1/register").permitAll()
			    
			    // --- USER ROLE REQUIRED ---
			    .requestMatchers("/api/v1/users/**").hasRole("USER")
			    .requestMatchers("/api/v1/reviews/**").hasRole("USER")

			    // --- ADMIN ROLE REQUIRED ---
			    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

			    // --- FALLBACK ---
			    .anyRequest().permitAll()
		);

		// Disable Form login Authentication
		http.formLogin(formLogin -> formLogin.disable());

		// Disable CSRF protection (it is difficult to implement in REST APIs)
		http.csrf(csrf -> csrf.disable());

		// Disable Basic Authentication
		http.httpBasic(httpBasic -> httpBasic.disable());

		// Stateless session
		http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Add JWT Token filter
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		
		http.cors();
		
		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());
		http.authorizeHttpRequests(authorize -> authorize
				// STATIC RESOURCES
				.requestMatchers("/static/**", "/assets/**", "/css/**", "/js/**", "/img/**").permitAll()
				.requestMatchers("/").permitAll()
				.requestMatchers("/error").permitAll()
				// PUBLIC PAGES
				.requestMatchers("/faq").permitAll()
				.requestMatchers("/contact").permitAll()
				.requestMatchers("/about").permitAll()
				.requestMatchers("/terms-condition").permitAll()
				.requestMatchers("/privacy-policy").permitAll()
				
				.requestMatchers("/login").permitAll()
				.requestMatchers("/register").permitAll()
				.requestMatchers("/forgot-password").permitAll()
				.requestMatchers("/signup").permitAll()
				.requestMatchers("/logout").permitAll()
				
				.requestMatchers("/artworks/**").permitAll()
				.requestMatchers("/artwork/**").permitAll()
				
				.requestMatchers("/artists/**").permitAll()
				.requestMatchers("/artist/**").permitAll()
				
				.requestMatchers("/museums/**").permitAll()
				.requestMatchers("/museum/**").permitAll()
				
				.requestMatchers("/error").permitAll()
				
				// USER PAGES
				.requestMatchers("/users/**").hasAnyRole("USER")
				.requestMatchers("/user/**").hasAnyRole("USER")
				.requestMatchers("/public-profile/**").hasAnyRole("USER")
				.requestMatchers("/public-profile").hasAnyRole("USER")
				// ADMIN PAGES
				.requestMatchers("/admin/**").hasAnyRole("ADMIN")
				
				.anyRequest().permitAll())
				// ERRORS
				.exceptionHandling(exception -> exception.accessDeniedPage("/error"))
				// LOGIN
				.formLogin(formLogin -> formLogin.loginPage("/login").failureUrl("/login?error=true").defaultSuccessUrl("/").permitAll())
				// LOGOUT
				 .logout(logout -> logout
					        .logoutUrl("/logout")
					        .logoutSuccessUrl("/")
					        .invalidateHttpSession(true)
					        .deleteCookies("JSESSIONID"));

		return http.build();
	}
}