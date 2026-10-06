package com.nexturn.hms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexturn.hms.dto.ChangePasswordDto;
import com.nexturn.hms.dto.LoginRequestDto;
import com.nexturn.hms.dto.LoginResponseDto;
import com.nexturn.hms.dto.NewUserRequestDto;
import com.nexturn.hms.dto.UserCredentialsResponseDto;
import com.nexturn.hms.dto.UsersByRoleResponseDto;
import com.nexturn.hms.entity.Role;
import com.nexturn.hms.service.UserLoginService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "${app.cors.allowed-origin}")
public class UserLoginController {

	private final UserLoginService userLoginService;

	public UserLoginController(UserLoginService userLoginService) {
		this.userLoginService = userLoginService;
	}

	@PostMapping("/register")
	public ResponseEntity<UserCredentialsResponseDto> registerNewUser(@Valid @RequestBody NewUserRequestDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userLoginService.registerNewUser(dto));
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
		return ResponseEntity.ok(userLoginService.verifyLogin(dto));
	}

	@PutMapping("/{userId}/password")
	public ResponseEntity<Void> changePassword(@PathVariable int userId, @Valid @RequestBody ChangePasswordDto dto) {
		userLoginService.changePassword(userId, dto);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/role/{role}")
	public ResponseEntity<List<UsersByRoleResponseDto>> getUsersByRole(@PathVariable Role role) {
		return ResponseEntity.ok(userLoginService.getUsersByRole(role));
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable int userId) {
		userLoginService.deleteUser(userId);
		return ResponseEntity.noContent().build();
	}
}