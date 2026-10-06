package com.nexturn.hms.service;

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

    @Autowired
    UserLoginRepository loginRepo;

    @Override
    @Transactional
    public AddressResponseDto saveOrUpdateAddress(int userId, AddressRequestDto dto) {

        UserLogin login = loginRepo.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with id: " + userId));

        Address address = addressRepo.findById(userId)
                .orElseGet(() -> {
                    Address a = new Address();
                    a.setLogin(login);
                    return a;
                });

        // Explicit mapping instead of ModelMapper
        address.setStreet(dto.street());
        address.setCity(dto.city());
        address.setState(dto.state());

        Address savedAddress = addressRepo.save(address);

        return toResponse(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponseDto getAddressByUserId(int userId) {

        Address address = addressRepo.findById(userId)
                .orElseThrow(() ->
                        new AddressNotFoundException(
                                "No address found for user id: " + userId));

        return toResponse(address);
    }

    private AddressResponseDto toResponse(Address address) {

        return new AddressResponseDto(
                address.getUserId(),
                address.getStreet(),
                address.getCity(),
                address.getState()
        );
    }
}