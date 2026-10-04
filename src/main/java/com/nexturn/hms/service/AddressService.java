package com.nexturn.hms.service;

import com.nexturn.hms.dto.AddressRequestDto;
import com.nexturn.hms.dto.AddressResponseDto;

public interface AddressService {
	AddressResponseDto saveOrUpdateAddress(int userId, AddressRequestDto dto);
	AddressResponseDto getAddressByUserId(int userId);
}