package com.nexturn.hms.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nexturn.hms.dto.AddressRequestDto;
import com.nexturn.hms.dto.AddressResponseDto;
import com.nexturn.hms.entity.Address;
import com.nexturn.hms.entity.UserLogin;
import com.nexturn.hms.exceptions.AddressNotFoundException;
import com.nexturn.hms.exceptions.UserNotFoundException;
import com.nexturn.hms.repository.AddressRepository;
import com.nexturn.hms.repository.UserLoginRepository;

@Service
public class AddressServiceImpl implements AddressService {

	
	
	@Autowired
	AddressRepository addressRepo;
	UserLoginRepository loginRepo;
	ModelMapper modelMapper;

	@Override
	@Transactional
	public AddressResponseDto saveOrUpdateAddress(int userId, AddressRequestDto dto) {
		UserLogin login = loginRepo.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

		// load the existing address if there is one, otherwise start a new one.
		// The id is never set by hand: @MapsId copies it from the login.
		Address address = addressRepo.findById(userId).orElseGet(() -> {
			Address a = new Address();
			a.setLogin(login);
			return a;
		});

		modelMapper.map(dto, address);
		return toResponse(addressRepo.save(address));
	}

	@Override
	@Transactional(readOnly = true)
	public AddressResponseDto getAddressByUserId(int userId) {
		Address address = addressRepo.findById(userId)
				.orElseThrow(() -> new AddressNotFoundException("No address found for user id: " + userId));
		return toResponse(address);
	}

	private AddressResponseDto toResponse(Address a) {
		return new AddressResponseDto(a.getUserId(), a.getStreet(), a.getCity(), a.getState());
	}
}