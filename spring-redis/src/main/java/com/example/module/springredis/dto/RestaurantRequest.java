package com.example.module.springredis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RestaurantRequest(
        @NotNull
        Integer id,
        @NotBlank
        String name,
        @NotNull
        Double longitude,
        @NotNull
        Double latitude
) {
}