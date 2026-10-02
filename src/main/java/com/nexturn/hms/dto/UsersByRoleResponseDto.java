package com.nexturn.hms.dto;

import com.nexturn.hms.entity.Role;

public record UsersByRoleResponseDto(int userId,String userName,Role role) {

}
