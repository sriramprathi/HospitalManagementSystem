package com.nexturn.hms.dto;

import com.nexturn.hms.entity.Role;

public record LoginResponseDto(int userId,Role role) {

}
