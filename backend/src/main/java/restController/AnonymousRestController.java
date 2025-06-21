package restController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import model.User;
import restDTO.RegisterRequest;
import service.UserService;

@RestController
@RequestMapping("/api/v1")
public class AnonymousRestController {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Operation(summary = "Registro de nuevo usuario", description = "Registra un nuevo usuario con campos opcionales como biografía y ubicación")
	@ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
	@ApiResponse(responseCode = "400", description = "Error de validación o datos inválidos")
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
		if (request.getUsername().isBlank() || request.getEmail().isBlank() || request.getPassword().isBlank()
				|| request.getConfirmPassword().isBlank()) {
			return ResponseEntity.badRequest().body("Faltan campos obligatorios.");
		}

		if (!request.getPassword().equals(request.getConfirmPassword())) {
			return ResponseEntity.badRequest().body("Las contraseñas no coinciden.");
		}
		
		if (userService.findByName(request.getUsername()).isPresent()) {
			return ResponseEntity.badRequest().body("Nombre de usuario ya está en uso.");
		}

		if (userService.findByEmail(request.getEmail()).isPresent()) {
			return ResponseEntity.badRequest().body("Email ya en uso.");
		}

		User user = new User();
		user.setName(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		if (request.getLocation() != null && !request.getLocation().isBlank()) {
			user.setLocation(request.getLocation());
		}

		if (request.getBiography() != null && !request.getBiography().isBlank()) {
			user.setBiography(request.getBiography());
		}

		userService.save(user);

		return ResponseEntity.status(201).body("Usuario registrado correctamente.");
	}
}
