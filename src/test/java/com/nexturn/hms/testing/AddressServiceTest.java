package com.nexturn.hms.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.nexturn.hms.dto.AddressRequestDto;
import com.nexturn.hms.dto.AddressResponseDto;
import com.nexturn.hms.dto.NewUserRequestDto;
import com.nexturn.hms.entity.Role;
import com.nexturn.hms.exceptions.AddressNotFoundException;
import com.nexturn.hms.exceptions.UserNotFoundException;
import com.nexturn.hms.service.AddressService;
import com.nexturn.hms.service.UserLoginService;

@SpringBootTest
@Transactional
class AddressServiceTest {

	@Autowired
	private AddressService addressService;
	@Autowired
	private UserLoginService userLoginService;

	private int createUser() {
		return userLoginService.registerNewUser(new NewUserRequestDto("Test", "User", Role.PATIENT)).userId();
	}

	@Test
	void testSaveAddress() {
		int userId = createUser();
		AddressResponseDto result = addressService.saveOrUpdateAddress(userId,
				new AddressRequestDto("Main Street", "Hyderabad", "Telangana"));
		assertEquals(userId, result.userId());
		assertEquals("Main Street", result.street());
		assertEquals("Hyderabad", result.city());
		assertEquals("Telangana", result.state());
	}

	@Test
	void testUpdateExistingAddress() {
		int userId = createUser();
		addressService.saveOrUpdateAddress(userId, new AddressRequestDto("Main Street", "Hyderabad", "Telangana"));
		AddressResponseDto result = addressService.saveOrUpdateAddress(userId,
				new AddressRequestDto("Park Road", "Chennai", "Tamil Nadu"));
		assertEquals(userId, result.userId());
		assertEquals("Park Road", result.street());
		assertEquals("Chennai", result.city());
		assertEquals("Tamil Nadu", result.state());
	}

	@Test
	void testSaveAddressForUnknownUser() {
	    AddressRequestDto request =
	            new AddressRequestDto("Main Street", "Hyderabad", "Telangana");

	    assertThrows(
	            UserNotFoundException.class,
	            () -> addressService.saveOrUpdateAddress(99999, request)
	    );
	}
	@Test
	void testGetAddressByUserId() {
		int userId = createUser();
		addressService.saveOrUpdateAddress(userId, new AddressRequestDto("Main Street", "Hyderabad", "Telangana"));
		AddressResponseDto result = addressService.getAddressByUserId(userId);
		assertEquals(userId, result.userId());
		assertEquals("Main Street", result.street());
	}

	@Test
	void testGetAddressWhenNoneSaved() {
		int userId = createUser();
		assertThrows(AddressNotFoundException.class, () -> addressService.getAddressByUserId(userId));
	}
}