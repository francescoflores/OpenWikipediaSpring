package com.open.openwikipedia.AuthManagement.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.open.openwikipedia.AuthManagement.service.AuthService;
import com.open.openwikipedia.common.Exceptions.CustomException;
import com.open.openwikipedia.common.model.dto.JwtDTO;
import com.open.openwikipedia.common.model.dto.LoginDTO;
import com.open.openwikipedia.common.model.dto.UserDTO;
import jakarta.validation.Valid;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	static final Logger logger = LogManager.getLogger(AuthController.class);

	@Autowired
	AuthService authService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO loginRequest) {

		try {
			JwtDTO loginResponse = authService.loginUser(loginRequest);
			return ResponseEntity.ok(loginResponse);
		} catch (CustomException e) {
			return ResponseEntity.status(e.getStatus()).body(Map.of("success", false, "message", e.getMessage()));
		}
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO signUpRequest) {
		try {
			Map<String, Object> registeredUser = authService.register(signUpRequest);
			return ResponseEntity.ok(registeredUser);
		} catch (CustomException e) {
			return ResponseEntity.status(e.getStatus()).body(Map.of("success", false, "message", e.getMessage()));
		}
	}

}
