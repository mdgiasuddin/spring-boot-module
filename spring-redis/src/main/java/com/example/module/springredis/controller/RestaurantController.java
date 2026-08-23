package com.example.module.springredis.controller;

import com.example.module.springredis.dto.RestaurantRequest;
import com.example.module.springredis.dto.RestaurantResponse;
import com.example.module.springredis.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public void addRestaurant(@Valid @RequestBody RestaurantRequest request) {
        restaurantService.addRestaurant(request);
    }

    @GetMapping
    public List<RestaurantResponse> nearbyRestaurants(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam double radiusKm
    ) {
        return restaurantService.findNearbyRestaurants(longitude, latitude, radiusKm);
    }
}
