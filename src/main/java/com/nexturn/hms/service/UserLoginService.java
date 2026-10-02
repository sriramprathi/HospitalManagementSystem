package com.nexturn.hms.service;

import java.util.List;

import com.nexturn.hms.dto.ChangePasswordDto;
import com.nexturn.hms.dto.LoginRequestDto;
import com.nexturn.hms.dto.LoginResponseDto;
import com.nexturn.hms.dto.NewUserRequestDto;
import com.nexturn.hms.dto.UserCredentialsResponseDto;
import com.nexturn.hms.entity.Role;
import com.nexturn.hms.entity.UserLogin;

public interface UserLoginService {
	UserCredentialsResponseDto registerNewUser(NewUserRequestDto userDto);
	LoginResponseDto verifyLogin(LoginRequestDto loginDto);
	void changePassword(ChangePasswordDto dto);
	void deleteUser(int userId);
	List<UserLogin> getUsersByRole(Role role);
}
