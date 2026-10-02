package com.nexturn.hms.dto;

public record ChangePasswordDto(int userId,String oldPassword, String newPassword) {

}
