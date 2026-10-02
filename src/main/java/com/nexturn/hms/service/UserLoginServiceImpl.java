package com.nexturn.hms.service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nexturn.hms.dto.ChangePasswordDto;
import com.nexturn.hms.dto.LoginRequestDto;
import com.nexturn.hms.dto.LoginResponseDto;
import com.nexturn.hms.dto.NewUserRequestDto;
import com.nexturn.hms.dto.UserCredentialsResponseDto;
import com.nexturn.hms.entity.Role;
import com.nexturn.hms.entity.UserLogin;
import com.nexturn.hms.exceptions.InvalidCredentialsException;
import com.nexturn.hms.repository.UserLoginRepository;

@Service
public class UserLoginServiceImpl implements UserLoginService{

	private final UserLoginRepository repo;
	private final SecureRandom random = new SecureRandom();

	public UserLoginServiceImpl(UserLoginRepository repo) {
		this.repo = repo;
	}

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
	public void changePassword(ChangePasswordDto dto) {
		Optional<UserLogin> login=repo.findById(dto.userId());
		UserLogin user=login.get();
		if(!user.getPassword().equals(dto.oldPassword())) {
			throw new InvalidCredentialsException("Invalid Old Password,Please Enter valid Password");
		}
		else {
			user.setPassword(dto.newPassword());
			repo.save(user);
		}
		
	}
	

	@Override
	public void deleteUser(int userId) {
		repo.deleteById(userId);
		
	}

	@Override
	public List<UserLogin> getUsersByRole(Role role) {
		List<UserLogin> userList=repo.findAllByRole(role);
		return userList;
	}

	

}
