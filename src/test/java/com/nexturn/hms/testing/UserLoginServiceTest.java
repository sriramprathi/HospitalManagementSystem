package com.nexturn.hms.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.nexturn.hms.dto.ChangePasswordDto;
import com.nexturn.hms.dto.LoginRequestDto;
import com.nexturn.hms.dto.LoginResponseDto;
import com.nexturn.hms.dto.NewUserRequestDto;
import com.nexturn.hms.dto.UserCredentialsResponseDto;
import com.nexturn.hms.dto.UsersByRoleResponseDto;
import com.nexturn.hms.entity.Role;
import com.nexturn.hms.exceptions.InvalidCredentialsException;
import com.nexturn.hms.exceptions.UserNotFoundException;
import com.nexturn.hms.service.UserLoginService;

@SpringBootTest
@Transactional
class UserLoginServiceTest {

	@Autowired
	private UserLoginService userLoginService;
	private UserCredentialsResponseDto registerUser() {
		NewUserRequestDto dto = new NewUserRequestDto("Test", "User", Role.Patient);
		return userLoginService.registerNewUser(dto);
	}
	@Test
	void testRegisterNewUser() {
		UserCredentialsResponseDto result = registerUser();
		assertNotNull(result);
		assertTrue(result.userId() > 0);
		assertTrue(result.userName().startsWith("test.user@"));
		assertFalse(result.password().isBlank());
	}

	@Test
	void testRegisterSameNameTwiceGivesDifferentUsernames() {
		UserCredentialsResponseDto first = registerUser();
		UserCredentialsResponseDto second = registerUser();
		assertNotEquals(first.userName(), second.userName());
	}

	@Test
	void testVerifyLogin() {
		UserCredentialsResponseDto user = registerUser();
		LoginResponseDto result = userLoginService.verifyLogin(new LoginRequestDto(user.userName(), user.password()));
		assertEquals(user.userId(), result.userId());
		assertEquals(Role.Patient, result.role());
	}

	@Test
	void testVerifyLoginWithWrongPassword() {
		UserCredentialsResponseDto user = registerUser();
		assertThrows(InvalidCredentialsException.class,
				() -> userLoginService.verifyLogin(new LoginRequestDto(user.userName(), "WrongPass@1")));
	}

	@Test
	void testVerifyLoginWithUnknownUser() {
		assertThrows(InvalidCredentialsException.class,
				() -> userLoginService.verifyLogin(new LoginRequestDto("no.such.user@0000", "Any@123")));
	}

	@Test
	void testChangePassword() {
		UserCredentialsResponseDto user = registerUser();
		userLoginService.changePassword(user.userId(), new ChangePasswordDto(user.password(), "NewPass@5678"));
		LoginResponseDto result = userLoginService.verifyLogin(new LoginRequestDto(user.userName(), "NewPass@5678"));
		assertEquals(user.userId(), result.userId());
		assertThrows(InvalidCredentialsException.class,
				() -> userLoginService.verifyLogin(new LoginRequestDto(user.userName(), user.password())));
	}

	@Test
	void testChangePasswordWithWrongOldPassword() {
		UserCredentialsResponseDto user = registerUser();
		assertThrows(InvalidCredentialsException.class, () -> userLoginService.changePassword(user.userId(),
				new ChangePasswordDto("WrongOld@1", "NewPass@5678")));
	}

	@Test
	void testChangePasswordForUnknownUser() {
		assertThrows(UserNotFoundException.class,
				() -> userLoginService.changePassword(99999, new ChangePasswordDto("Old@123", "New@123")));
	}

	@Test
	void testDeleteUser() {
		UserCredentialsResponseDto user = registerUser();
		userLoginService.deleteUser(user.userId());
		assertThrows(InvalidCredentialsException.class,
				() -> userLoginService.verifyLogin(new LoginRequestDto(user.userName(), user.password())));
	}

	@Test
	void testDeleteInvalidUser() {
		assertThrows(UserNotFoundException.class, () -> userLoginService.deleteUser(99999));
	}

	@Test
	void testGetUsersByRole() {
		registerUser();
		List<UsersByRoleResponseDto> result = userLoginService.getUsersByRole(Role.Patient);
		assertFalse(result.isEmpty());
	}
}