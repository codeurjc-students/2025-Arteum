package security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
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
	public PasswordEncoder passwordEncoder() {
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
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
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