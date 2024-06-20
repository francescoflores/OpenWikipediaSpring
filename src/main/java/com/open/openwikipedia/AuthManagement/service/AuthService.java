package com.open.openwikipedia.AuthManagement.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.open.openwikipedia.common.Exceptions.CustomException;
import com.open.openwikipedia.common.model.Role;
import com.open.openwikipedia.common.model.User;
import com.open.openwikipedia.common.model.dto.JwtDTO;
import com.open.openwikipedia.common.model.dto.LoginDTO;
import com.open.openwikipedia.common.model.dto.UserDTO;
import com.open.openwikipedia.common.model.dto.UserDataDTO;
import com.open.openwikipedia.common.repository.RoleRepository;
import com.open.openwikipedia.common.repository.UserRepository;
import com.open.openwikipedia.security.jwt.JwtUtils;
import com.open.openwikipedia.security.service.UserDetailsImpl;

@Service
public class AuthService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	public JwtDTO loginUser(LoginDTO loginRequest) throws AuthenticationException, CustomException {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		return new JwtDTO(jwt,
				userDetails.getId(),
				userDetails.getUsername(),
				userDetails.getEmail(),
				userDetails.getCreatedAt());
	}

	public Map<String, Object> register(UserDTO signUpRequest) throws CustomException {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			throw new CustomException(HttpStatus.CONFLICT, "Username is already taken!");
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new CustomException(HttpStatus.CONFLICT, "Email is already in use!");
		}

		Role userRole = roleRepository.findByName("ROLE_USER")
				.orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

		Set<Role> roles = new HashSet<>();
		roles.add(userRole);

		User user = new User(signUpRequest.getUsername(),
				signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));
		user.setRoles(roles);

		userRepository.save(user);

		return Map.of("success", true, "message", "User registered successfully!", "data",
				new UserDataDTO(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt()));
	}
}
