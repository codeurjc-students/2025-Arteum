package restController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import security.jwt.AuthResponse;
import security.jwt.AuthResponse.Status;
import security.jwt.LoginRequest;
import security.jwt.UserLoginService;
import service.UserService;

@RestController
@RequestMapping("/api/v1/auth")
public class LoginRestController {

	@Autowired
	private UserLoginService userLoginService;

	@Autowired
	private UserService userService;

	@Operation(summary = "Log in into an account")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Logged in", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }) })
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@CookieValue(name = "accessToken", required = false) String accessToken,
			@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletRequest request,
			@RequestBody LoginRequest loginRequest) {
		if (request.getUserPrincipal() != null) {
			return ResponseEntity
					.ok(new AuthResponse(Status.FAILURE, "Cannot login when you are not logged out", true));
		} else {
			User user = userService.findByEmail(loginRequest.getUsername())
					.orElseGet(() -> userService.findByName(loginRequest.getUsername()).orElse(null));
			if (user != null) {
				return userLoginService.login(loginRequest, accessToken, refreshToken);
			} else {
				return ResponseEntity.ok(new AuthResponse(Status.FAILURE, "Invalid credentials", true));
			}
		}
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(
			@CookieValue(name = "refreshToken", required = false) String refreshToken) {
		return userLoginService.refresh(refreshToken);
	}

	@Operation(summary = "To log out from an account")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Logged out", content = {
			@Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }) })
	@PostMapping("/logout")
	public ResponseEntity<AuthResponse> logOut(HttpServletRequest request, HttpServletResponse response) {
		if (request.getUserPrincipal() != null) {
			return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, userLoginService.logout(request, response)));
		} else {
			return ResponseEntity.ok(new AuthResponse(Status.FAILURE, "Cannot logout when you are not logged", true));
		}
	}
}
