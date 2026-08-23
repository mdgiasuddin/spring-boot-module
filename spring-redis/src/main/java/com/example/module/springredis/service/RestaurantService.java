package com.example.module.springredis.service;

import com.example.module.springredis.dto.GeoResponse;
import com.example.module.springredis.dto.RestaurantRequest;
import com.example.module.springredis.dto.RestaurantResponse;
import com.example.module.springredis.enity.Restaurant;
import com.example.module.springredis.util.JsonUtil;
import com.example.module.springredis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Distance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.geo.Metrics.KILOMETERS;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RedisUtil redisUtil;
    private final JsonUtil jsonUtil;

    private static final String RESTAURANT_LOCATION_KEY = "locations.restaurant";

    public void addRestaurant(RestaurantRequest request) {
        Restaurant restaurant = new Restaurant(request.id(), request.name());
        redisUtil.addGeoPoint(RESTAURANT_LOCATION_KEY, request.longitude(), request.latitude(), restaurant);
    }

    public List<RestaurantResponse> findNearbyRestaurants(double longitude, double latitude, double radiusKm) {
        List<GeoResponse> responses = redisUtil.getNearbyPoints(
                RESTAURANT_LOCATION_KEY, longitude, latitude,
                new Distance(radiusKm, KILOMETERS)
        );

        List<RestaurantResponse> restaurants = new ArrayList<>();
        responses.forEach(response -> {
            Restaurant restaurant = jsonUtil.stringToObject(response.value(), Restaurant.class);
            restaurants.add(
                    new RestaurantResponse(
                            restaurant.id(),
                            restaurant.name(),
                            response.point().getX(),
                            response.point().getY(),
                            response.distance()
                    )
            );
        });
        return restaurants;
    }
}
