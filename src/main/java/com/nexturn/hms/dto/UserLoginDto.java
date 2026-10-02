package com.nexturn.hms.dto;

import com.nexturn.hms.entity.Role;

public record UserLoginDto(
		int userId,
        String userName,
        Role role) {

}
