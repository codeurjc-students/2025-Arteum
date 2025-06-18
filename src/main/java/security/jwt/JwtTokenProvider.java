package security.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenProvider {

	private static final Logger LOG = LoggerFactory.getLogger(JwtRequestFilter.class);
	private Key key;

	@Value("${jwt.secret}")
	public void setJwtSecret(String secret) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	private static long JWT_EXPIRATION_IN_MS = 5400000;
	private static Long REFRESH_TOKEN_EXPIRATION_MSEC = 10800000l;

	@Autowired
	private UserDetailsService userDetailsService;

	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			LOG.debug("JWT validation error: " + e.getMessage());
			return false;
		}
	}

	public String getUsername(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}

	public Token generateToken(UserDetails user) {
		Claims claims = Jwts.claims().setSubject(user.getUsername());
		claims.put("auth", user.getAuthorities().stream().map(s -> new SimpleGrantedAuthority("ROLE_" + s))
				.filter(Objects::nonNull).collect(Collectors.toList()));
		Date now = new Date();
		Long duration = now.getTime() + JWT_EXPIRATION_IN_MS;
		Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_IN_MS);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.HOUR_OF_DAY, 8);
		String token = Jwts.builder().setSubject(user.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_IN_MS))
				.signWith(key, SignatureAlgorithm.HS256).compact();
		return new Token(Token.TokenType.ACCESS, token, duration,
				LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));
	}

	public Token generateRefreshToken(UserDetails user) {
		Claims claims = Jwts.claims().setSubject(user.getUsername());
		claims.put("auth", user.getAuthorities().stream().map(s -> new SimpleGrantedAuthority("ROLE_" + s))
				.filter(Objects::nonNull).collect(Collectors.toList()));
		Date now = new Date();
		Long duration = now.getTime() + REFRESH_TOKEN_EXPIRATION_MSEC;
		Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_MSEC);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.HOUR_OF_DAY, 8);
		String token = Jwts.builder().setSubject(user.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_IN_MS))
				.signWith(key, SignatureAlgorithm.HS256).compact();
		return new Token(Token.TokenType.REFRESH, token, duration,
				LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));
	}
}
