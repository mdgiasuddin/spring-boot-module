package com.example.module.springredis.dto;

import org.springframework.data.geo.Distance;

public record RestaurantResponse(
        Integer id,
        String name,
        Double longitude,
        Double latitude,
        Distance distance
) {
}
