package com.nexturn.hms.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(@NotBlank String userName,@NotBlank String password) {

}
