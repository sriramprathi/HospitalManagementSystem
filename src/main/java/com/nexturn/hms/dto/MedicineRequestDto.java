package com.nexturn.hms.dto;

import java.math.BigDecimal;
import com.nexturn.hms.entity.MedicineCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record MedicineRequestDto(
		@NotBlank @Size(max = 40) String medicineName,
		@NotNull @Positive BigDecimal price,
		@PositiveOrZero int availability,
		@NotNull MedicineCategory category) {

}