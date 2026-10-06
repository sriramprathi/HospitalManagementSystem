package com.nexturn.hms.service;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nexturn.hms.dto.ChangePasswordDto;
import com.nexturn.hms.dto.LoginRequestDto;
import com.nexturn.hms.dto.LoginResponseDto;
import com.nexturn.hms.dto.NewUserRequestDto;
import com.nexturn.hms.dto.UserCredentialsResponseDto;
import com.nexturn.hms.dto.UsersByRoleResponseDto;
import com.nexturn.hms.entity.Role;
import com.nexturn.hms.entity.UserLogin;
import com.nexturn.hms.exceptions.InvalidCredentialsException;
import com.nexturn.hms.exceptions.UserNotFoundException;
import com.nexturn.hms.repository.UserLoginRepository;

@Service
public class UserLoginServiceImpl implements UserLoginService{

	@Autowired
	UserLoginRepository repo;
	
	SecureRandom random = new SecureRandom();

	@Override
	@Transactional
	public UserCredentialsResponseDto registerNewUser(NewUserRequestDto userDto) {
		String first = userDto.firstName().trim().toLowerCase();
		String last = userDto.lastName().trim().toLowerCase();
 
		String userName;
		do {
			userName = first + "." + last + "@" + (1000 + random.nextInt(9000));
		}while(repo.existsByUserName(userName));
		
		String password = first + "@" + (10000 + random.nextInt(90000));

		UserLogin login=new UserLogin();
		login.setUserName(userName);
		login.setPassword(password);
		login.setRole(userDto.role());
		login=repo.save(login);
		
		return new UserCredentialsResponseDto(login.getUserName(),login.getPassword(),login.getUserId());
	}

	@Override
	public LoginResponseDto verifyLogin(LoginRequestDto loginDto) {
		UserLogin login=repo.findByUserName(loginDto.userName())
				.orElseThrow(()-> new InvalidCredentialsException("Invalid Username or Password"));
		if(!login.getPassword().equals(loginDto.password())) {
			throw new InvalidCredentialsException("Invalid Username or Password");
		}
		return new LoginResponseDto(login.getUserId(),login.getRole());
	}

	@Override
	public void changePassword(int userId, ChangePasswordDto dto) {
		UserLogin login = repo.findById(userId).orElseThrow(
				() -> new UserNotFoundException("User not found with id: " + userId));
		if (!login.getPassword().equals(dto.oldPassword())) {
			throw new InvalidCredentialsException("Invalid Old Password,Please Enter valid Password");
		}
		login.setPassword(dto.newPassword());
		repo.save(login);
	}
	

	@Override
	@Transactional
	public void deleteUser(int userId) {
		if (!repo.existsById(userId)) {
			throw new UserNotFoundException("User not found with id: " + userId);
		}
		repo.deleteById(userId);
	}

	@Override
	public List<UsersByRoleResponseDto> getUsersByRole(Role role) {
		return repo.findAllByRole(role).stream()
				.map(u -> new UsersByRoleResponseDto(u.getUserId(),u.getUserName(),u.getRole()))
				.toList();
	}

}
